package aaa.modules.regression.conversions.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tatsiana Saltsevich
 * @name Manual Hybrid Conversion Docs Verification ("D-T-AU-SS-KS-966-CNV")
 *
 * Hybrid Conversion "Statement Electing Lower Limits For Uninsured/Underinsured Motorists Coverage form"
 * (AA52KS)  2nd renewal and for all subsequent renewal terms triggers
 **/

public class TestManualConversionScenario4 extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = Constants.States.KS)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS)
	public void manualConversionDocsScenario4(@Optional("KS") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime billGenDate;
		LocalDateTime renewalDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(45);
		LocalDateTime secondRenewalDate = renewalDate.plusYears(1);
		//Pre-conditions:-
		//	> Create a customer in PAS.
		//  > Policy has 2 NI, 2 Driver and 2 Vehicle
		//	> One of the driver is excluded from rating.
		//	> Uninsured Motorists (UM)/Underinsured Motorists (UIM) limit is lower than Bodily Injury (BI) limit
		TestData policyTd = getConversionPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), "NII"),
						getTestSpecificTD("TestData_NamedInsured").getTestDataList("NamedInsuredInformation")).resolveLinks()
				.adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("TestData_Drivers").getTestDataList("Drivers"))
				.adjust(VehicleTab.class.getSimpleName(), getTestSpecificTD("TestData_Vehicles").getTestDataList("Vehicles"))
				.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab"))
				.adjust(DocumentsAndBindTab.class.getSimpleName(), getTestSpecificTD("DocumentsAndBindTab"));
		//1. (R-45) Login with user role = E34 having privilege 'Initiate Renewal Entry' and retrieve the customer created above -> Renewal entry is initiated
		mainApp().open(getLoginTD(Constants.UserGroups.L41));
		//Create a new customer in PAS
		createCustomerIndividual();
		//Select the action "Initiate Renewal Entry" from 'Select Action:' dropdown box on Customer UI and click on the Go button.
		//Enter the value for the Previous Policy Number/Source System and provide valid values for the other mandatory fields and click on the OK button.
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalDate);
		//Open the renewal image in Data Gathering mode. Enter mandatory data on all pages
		//Rate the policy. Navigate to the Bind tab and 'Save and Exit'
		policy.getDefaultView().fill(policyTd);
		//Navigate to policy consolidated view
		Tab.buttonBack.click();
		String policyNum = PolicySummaryPage.getPolicyNumber();
		//Click on the renewal image Button ->  Status of the Renewal Image is in 'Premium calculated'
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//2. (R-35) Run the batch job: renewalOfferGenerationPart2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		//Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal offer is generated and the policy is in 'Proposed' status
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AA52KS);
		//3. (R-20) Run the batch job - aaaRenewalNoticeBillAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		//Navigate to the Billing tab ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(renewalDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(0));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(0), billGenDate, renewalDate, BillingHelper.DZERO);
		//4. (R) Make the renewal term payment. -> Payment is successfully made
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(renewalDate))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//5. (R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		//6. Navigate to Policy Consolidated View. -> Policy Status = Active
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		//7. (2R-96) Run the following job - renewalOfferGenerationPart2 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		//8. (2R-63) Run the following job - renewalOfferGenerationPart1 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		//9. (2R-45) Run the following job - renewalOfferGenerationPart2 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		//Validate status of renewal image -> Status of renewal image = Premium Calculated
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//10. (2R-35) Run the batch jobs: renewalOfferGenerationPart2, aaaDocGen
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		//Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal status is 'Proposed'
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//Navigate to Policy Consolidated View
		//#V1 - Renewal declaration (AA02) will be generated in renewal E-folder.
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AA02KS);
		//#V2 - Form number AA52CT will be printed on the Renewal DEC page in the FORMS & ENDORSEMENT section
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AA52KS);
		//11. (2R-20) Run the following job - aaaRenewalNoticeBillAsyncJob ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(0));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(0), billGenDate, secondRenewalDate, BillingHelper.DZERO);
		//12. (2R) Make the renewal term payment.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(secondRenewalDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(secondRenewalDate))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//13. (2R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(secondRenewalDate));
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		//Navigate to Policy Consolidated View. -> Policy Status = Proposed
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}