package aaa.modules.regression.conversions.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tatsiana Saltsevich
 * @name Manual Hybrid Conversion Docs Verification ("D-T-AU-SS-OR-958-CNV")
 * @scenario
 * 1. Pre-conditions:- Create a new customer in PAS
 * Ensure policy meets following conditions:-
 * i. 1 or more Named Insured and 1 or more vehicles and drivers where the age of the Named Insured - Applicant  is greater than or equal to 18 years, with an excluded driver.
 * ii. The Uninsured Motorists (UM) limits were less than BI Limits.
 *
 * 2. (R-45) Login with user role = E34 having privilege 'Initiate Renewal Entry' and retrieve the customer created above -> Renewal entry is initiated
 * 3. Select the action "Initiate Renewal Entry" from 'Select Action:' dropdown box on Customer UI and click on the Go button.
 * 4. Enter the value for the Previous Policy Number/Source System and provide valid values for the other mandatory fields and click on the OK button.
 * 5. Open the renewal image in Data Gathering mode. Enter mandatory data on all pages
 * 6. Rate the policy.
 * 7. Navigate to the Bind tab and 'Save and Exit'
 * 8. Navigate to policy consolidated view
 * 9. Click on the renewal image Button ->  Status of the Renewal Image is in 'Premium calculated'
 * 10. (R-35) Run the batch jobs: Renewal_Offer_Generation_Part2, Run aaaDocGen Job
 * 11. Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal offer is generated and the policy is in 'Proposed' status
 * 12. (R-20) Run the batch job - aaaRenewalNoticeBillAsyncJob
 * 13. Navigate to the Billing tab ->
 * Installment bill is generated under Bills and Statement section of the Billing tab
 * Type = "Bill", Date = Installment due date.
 * 14. (R) Make the renewal term payment. -> Payment is successfully made
 * 15. (R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
 * 16. Navigate to Policy Consolidated View. -> Policy Status = Active
 * 17. (2DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
 * Installment bill is generated under Bills and Statement section of the Billing tab
 * Type = ""Bill"", Date = Installment due date."
 * 18. (2DD3) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
 * 19. (2DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
 * Installment bill is generated under Bills and Statement section of the Billing tab
 * Type = "Bill", Date = Installment due date.
 * 20. (2DD6) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
 * 21. (2DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
 * Installment bill is generated under Bills and Statement section of the Billing tab
 * Type = "Bill", Date = Installment due date.
 * 22. (2R-96) Run the following job - Renewal_Offer_Generation_Part2 -> Job run is successful.
 * 23. (2DD9) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
 * 24. (2R-63) Run the following job - Renewal_Offer_Generation_Part1 -> Job run is successful.
 * 25. (2R-45) Run the following job - Renewal_Offer_Generation_Part2 -> Job run is successful.
 * 26. Validate status of renewal image -> Status of renewal image = Premium Calculated
 * 27. (2R-40) Run the batch jobs: AAAPreRenewalNoticeAsyncJob, aaaDocGen -> Batch job is run successfully
 * 28. Retrieve the policy and validate the Renewal Image Status for converted policy
 * 29. Navigate to Policy Consolidated View
 * 30. Navigate to E-Folder -> #V1 - Pre-Renewal notice form #AAPRN1OR is NOT generated and NOT found for 2R in e-folder
 * 31. (2R-35) Run the batch jobs: Renewal_Offer_Generation_Part2, aaaDocGen
 * 32. Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal status is 'Proposed'
 * 33. Navigate to Policy Consolidated View
 * 34. Navigate to E-Folder ->
 * #V2 - Renewal Dec Page (AA02OR) is generated in the renewal e-folder.
 * #V3 - Dec Page type should be "Notice of Renewal Policy" and not "Notice of New Policy". Note: Please refer to document template for content validation.
 * #V4 - Named Driver Exclusion (AA43OR) Endorsement form will not generate as a part of Renewal packet
 * #V5 - Form number will be printed on the Renewal DEC page in the FORMS & ENDORSEMENT section ( <aaan:TextField>AA43OR</aaan:TextField>)
 * #V6 - OREGON ELECTION OF LOWER LIMITS FOR UNINSURED MOTORISTS COVERAGE (AA43OR) form will not generate
 * #V7 - Form number for Uninsured and Underinsured Motorist Disclosure State and Rejection of Coverage form DOES print on the Subsequent Renewal DEC page in the FORMS & ENDORSEMENT section.
 * 35. (2R-20) Run the following job - aaaRenewalNoticeBillAsyncJob ->
 * Installment bill is generated under Bills and Statement section of the Billing tab
 * Type = "Bill", Date = Installment due date.
 * 36. Navigate to the Billing tab ->
 * #V6: System archives the form "Insurance Renewal Bill" (AHRBXX 03 16) is available in the Policy E-folder under Renewal. Note:- Refer to form template and requirement for content validation
 * 37. (2R) Do not make the renewal term payment.
 * 38. (2R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
 * 39. Navigate to Policy Consolidated View. -> Policy Status = Proposed
 * 40. (2R+5) Run the batch jobs: policyLapsedRenewalProcessAsyncJob, aaaRenewalReminderGenerationAsyncJob, aaaDocGen
 * 41. Retrieve the policy and navigate to Policy Consolidated View
 * 42. Navigate to E-Folder
 * #V8 - Validate Expiration Notice(AH64XX) is generated for the policy in the Efolder
 * Note:- Refer to the Mapping Document for static, variable and dynamic text on AH64XX for OR for document content validation
 */

public class TestManualHybridConversionScenario2 extends AutoSSBaseTest {
	@Parameters({"state"})
	@StateList(states = Constants.States.OR)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS)
	public void manualHybridConversionDocsScenario2(@Optional("OR") String state) {
		List<LocalDateTime> installmentDueDates;
		ErrorTab errorTab = new ErrorTab();
		LocalDateTime billGenDate;
		LocalDateTime renewalDate = getTimePoints().getConversionEffectiveDate();
		LocalDateTime secondRenewalDate = renewalDate.plusYears(1);
		// Ensure policy meets following conditions:-
		// i. 1 or more Named Insured and 1 or more vehicles and drivers where the age of the Named Insured -
		// Applicant  is greater than or equal to 18 years, with an excluded driver.
		// ii. The Uninsured Motorists (UM) limits were less than BI Limits.
		TestData policyTd = getConversionPolicyDefaultTD()
				.adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab"))
				.adjust(DocumentsAndBindTab.class.getSimpleName(), getTestSpecificTD("DocumentsAndBindTab"));
		//2. (R-45) Login with user role = E34 having privilege 'Initiate Renewal Entry' and retrieve the customer created above -> Renewal entry is initiated
		mainApp().open(getLoginTD(Constants.UserGroups.L41));
		//1. Create a new customer in PAS
		//createCustomerIndividual();
		SearchPage.openCustomer("700032274");
		//3. Select the action "Initiate Renewal Entry" from 'Select Action:' dropdown box on Customer UI and click on the Go button.
		//4. Enter the value for the Previous Policy Number/Source System and provide valid values for the other mandatory fields and click on the OK button.
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalDate);
		//5. Open the renewal image in Data Gathering mode. Enter mandatory data on all pages
		//6. Rate the policy.
		//7. Navigate to the Bind tab and 'Save and Exit'
		policy.getDefaultView().fill(policyTd);
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200111);
		errorTab.override();
		policy.getDefaultView().getTab(DocumentsAndBindTab.class).submitTab();
		//8. Navigate to policy consolidated view
		Tab.buttonBack.click();
		String policyNum = PolicySummaryPage.getPolicyNumber();
		//9. Click on the renewal image Button ->  Status of the Renewal Image is in 'Premium calculated'
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//10. (R-35) Run the batch jobs: Renewal_Offer_Generation_Part2, Run aaaDocGen Job
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//11. Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal offer is generated and the policy is in 'Proposed' status
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//12. (R-20) Run the batch job - aaaRenewalNoticeBillAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		//13. Navigate to the Billing tab ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(renewalDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(0));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(0), billGenDate, renewalDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		//14. (R) Make the renewal term payment. -> Payment is successfully made
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(renewalDate))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//15. (R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		//16. Navigate to Policy Consolidated View. -> Policy Status = Active
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		//17. (2DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = ""Bill"", Date = Installment due date."
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(1), billGenDate, renewalDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		//18. (2DD3) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(1)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//19. (2DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), billGenDate, renewalDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		//20. (2DD6) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(2)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(2)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//21. (2DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(3), billGenDate, renewalDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		//22. (2R-96) Run the following job - Renewal_Offer_Generation_Part2 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//23. (2DD9) Make Payment for the generated bill -> Payment made is listed down in the Payment and Other transaction section
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(3)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(3)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
		//24. (2R-63) Run the following job - Renewal_Offer_Generation_Part1 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		//25. (2R-45) Run the following job - Renewal_Offer_Generation_Part2 -> Job run is successful.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//26. Validate status of renewal image -> Status of renewal image = Premium Calculated
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//27. (2R-40) Run the batch jobs: AAAPreRenewalNoticeAsyncJob, aaaDocGen -> Batch job is run successfully
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPreRenewalLetterGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//28. Retrieve the policy and validate the Renewal Image Status for converted policy - ommited as excessive
		//29. Navigate to Policy Consolidated View
		//30. Navigate to E-Folder -> Pre-Renewal notice form #AAPRN1OR is NOT generated and NOT found for 2R in e-folder
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AAPRN1OR);
		//31. (2R-35) Run the batch jobs: Renewal_Offer_Generation_Part2, aaaDocGen
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//32. Retrieve the policy and validate the Renewal Image Status for converted policy -> Renewal status is 'Proposed'
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//33. Navigate to Policy Consolidated View
		//34. Navigate to E-Folder ->
		//#V2 - Renewal Dec Page (AA02OR) is generated in the renewal e-folder.
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AA02OR);
		//#V3 - Dec Page type should be "Notice of Renewal Policy" and not "Notice of New Policy". Note: Please refer to document template for content validation.
		//#V4 - Named Driver Exclusion (AA43OR) Endorsement form will not generate as a part of Renewal packet
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AA43OR);
		//#V5 - Form number will be printed on the Renewal DEC page in the FORMS & ENDORSEMENT section ( <aaan:TextField>AA43OR</aaan:TextField>)
		//#V6 - OREGON ELECTION OF LOWER LIMITS FOR UNINSURED MOTORISTS COVERAGE (AA52OR) form will not generate
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AA52OR);
		//#V7 - Form number for Uninsured and Underinsured Motorist Disclosure State and Rejection of Coverage form DOES print on the Subsequent Renewal DEC page in the FORMS & ENDORSEMENT section.
		//35. (2R-20) Run the following job - aaaRenewalNoticeBillAsyncJob ->
		//Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(0));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(0), billGenDate, secondRenewalDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		//36. Navigate to the Billing tab ->
		//#V6: System archives the form "Insurance Renewal Bill" (AHRBXX 03 16) is available in the Policy E-folder under Renewal. Note:- Refer to form template and requirement for content validation
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHRBXX);
		//37. (2R) Do not make the renewal term payment.
		//38. (2R+1) Run the batch jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		//39. Navigate to Policy Consolidated View. -> Policy Status = Proposed
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//40. (2R+5) Run the batch jobs: policyLapsedRenewalProcessAsyncJob, aaaRenewalReminderGenerationAsyncJob, aaaDocGen
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//41. Retrieve the policy and navigate to Policy Consolidated View
		//42. Navigate to E-Folder
		//#V8 - Validate Expiration Notice(AH64XX) is generated for the policy in the Efolder
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH64XX);
	}
}