package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.ManualRenewActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author tsaltsevich
 * @name TestPolicyReinstatementAdditionalEndorsements ("CL-CP-101")
 *
 * Test Case Description:
 * 1. Generate Cancellation Notice due to Nonpayment of Premium on the state specific number of days after bill due date (Cancellation Notice generation date)
 * 2. Generate Lapse Notice upon cancellation effective for nonpayment and policy Is eligible for reinstatement
 * 3. Generate Cancellation Notice Withdrawn when payment is sufficient (on a Non-Payment Cancel) to lift pending cancel status
 * 4. Process Renewal when DNR is removed
 * 5. Set DNR at Renewal (Current policy is expiring in next R-X (Renewal
 *  Image Creation for authorized users) to R-X (Last Day of DNR
 *  setting) days"
 * 6. Generate Non-Renewal letter via central print (Underwriting NonRenewal Letter HSU07XX)
 * 7. Generate Non-Renewal Notice XX days prior to the Renewal Effective Date if the policy is set for non-renewal
 * 8. DNR has been set on the policy any time prior to R-X (Last Day of DNR setting)
 * 9. Authorized User wants to manually renew the policy without lapse when the status on the Renewal Image is Proposed i,e, from R+0 to R+15.
 *
 **/

public class TestPolicyCancellationManualRenew extends PolicyBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyCancelNoticeAddDelete(@Optional("CA") String state) {
		List<LocalDateTime> installmentDueDates;

		//DD0
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.MONTHLY_STANDARD)
				.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
						HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN_AT_RENEWAL.getLabel()), BillingConstants.PaymentPlan.MONTHLY_STANDARD_RENEWAL).resolveLinks());
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		//DD1-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab\n"
				+ "Type = 'Bill'\n"
				+ "Date = Installment due date");
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(1)).verifyPresent();

		//DD1
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		log.info("TEST: #L System records the Payment transaction in the 'Payment & Other transaction' section");
		new BillingPaymentsAndTransactionsVerifier().setEffectiveDate(getTimePoints().getBillDueDate(installmentDueDates.get(1)))
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setAmount(minDue.negate()).verifyPresent();

		//DD2-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		//DD3-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		//DD3+1
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDates.get(2)));
		JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		mainApp().open();
		log.info("TEST: #V1 'Cancel Notice' is set on the policy");
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		log.info("TEST: #V2 Cancellation Notice Document (NonPayment) AH34XX 0316  is archived in Fastlane and available in the Billing E-folder  under Cancellation");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH34XX);
		//Make Payment for the outstanding amount still due by clicking on "Accept Payment" link on the Billing tab.
		SearchPage.openBilling(policyNum);
		minDue = new Dollar(BillingSummaryPage.getMinimumDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		log.info("TEST: #L Cancel notice is removed from the policy");
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
		log.info("TEST: #V3 Cancellation Notice Withdrawn AHCWXX is archived and available in the Billing E-folder under Cancellation & Rescission & Reinstatement folder");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHCWXX);

		//DD4-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(4)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		//DD5-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(5)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		//DD5
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDates.get(4)));
		JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);
		log.info("TEST: #L 'Cancel Notice' is set on the policy");
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		//DD6-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(6)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		log.info("TEST: #L Bill is NOT generated");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL)
				.setDueDate(getTimePoints().getBillGenerationDate(installmentDueDates.get(6)))
				.verifyPresent(false);

		//Cancellation DD
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDueDates.get(4)));
		JobUtils.executeJob(BatchJob.aaaCancellationConfirmationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		log.info("TEST: #V8 Lapse Notice AH67XX is archived in Fastlane and available in the Billing E-folder under Cancellation");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH67XX);
		log.info("TEST: #L Status of the policy is 'Policy Cancelled'");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_CANCELLED).verify(1);

		//CDD+5
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDueDates.get(4)).plusDays(5));
		//Apply payment against the 'Minimum Amount Due' on the billing account through 'Accept payment' manually. (Cancellation Notice Minimum Due)
		mainApp().open();
		SearchPage.openBilling(policyNum);
		minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE,
				BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		log.info("TEST: #L SBilling triggers PAS to reinstate the policy and updates the status of policy as ‘Active’");
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		log.info("TEST: #L DD6 bill is skipped");
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(6)).verifyPresent(false);

		//DD7-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(7)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab\n"
				+ " Type = 'Bill'\n"
				+ " Date = Installment due date");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(7)).verifyPresent();
		//Apply payment against the 'Minimum Amount Due' on the billing account through 'Accept payment' manually.
		minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(7), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//Select 'Do Not Renew' option from the Move to dropdown to set the DNR flag.
		SearchPage.openPolicy(policyNum);
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		log.info("TEST: #L DNR is set on the policy");
		PolicySummaryPage.verifyDoNotRenewFlagPresent();

		//DD8-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(8)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab\n"
				+ " Type = 'Bill'\n"
				+ " Date = Installment due date");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(8)).verifyPresent();

		//DD8
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(8)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		//Apply payment against the 'Minimum Amount Due' on the billing account through 'Accept payment' manually.
		minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(8), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		//DD9-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(9)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab\n"
				+ " Type = 'Bill'\n"
				+ " Date = Installment due date");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(9)).verifyPresent();
		//Apply payment against the 'Minimum Amount Due' on the billing account through 'Accept payment' manually.
		minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(9), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		//R-83
		LocalDateTime renewalDate = installmentDueDates.get(0).plusYears(1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		log.info("TEST: Make sure renewal is not generated");
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled(false);

		//DD10-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(10)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab\n"
				+ " Type = 'Bill'\n"
				+ " Date = Installment due date");
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(10)).verifyPresent();
		//Apply payment against the 'Minimum Amount Due' on the billing account through 'Accept payment' manually.
		minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(10), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		//R-57
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		log.info("TEST: #L DNR flag is removed");
		PolicySummaryPage.verifyDoNotRenewFlagNotPresent();
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		log.info("TEST: #V Renewal image is created");
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		//Select 'Do Not Renew', click on the 'Reason' LOV. Select 'Substantial Increase in Hazard'.
		SearchPage.openPolicy(policyNum);
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData")
				.adjust(TestData.makeKeyPath("DoNotRenewActionTab", "Reason"), "Substantial Increase in Hazard (requires user to enter supporting data)"));
		log.info("TEST: #V Current Policy is updated with DNR");
		PolicySummaryPage.verifyDoNotRenewFlagPresent();
		log.info("TEST: #V Transaction history will have the DNR action");
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Do Not Renew Flag");

		//R-48
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.policyDoNotRenewAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		log.info("TEST: #V The Policy still has DNR flag set");
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.verifyDoNotRenewFlagPresent();
		log.info("TEST: #V The underwriting letter ‘HSU07 CA Non-Renewal’ is generated at the Renewal Offer Generation date along with Non Renewal Notice");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.HSU07CA);
		log.info("TEST: #V  Non-Renewal Notice WU65CA is archived in Fastlane and available in the Policy E-folder Under Cancellation");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.WU65CA);

		//R-10
		TimeSetterUtil.getInstance().nextPhase(renewalDate.minusDays(10));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		log.info("TEST: #L DNR flag is removed");
		PolicySummaryPage.verifyDoNotRenewFlagNotPresent();
		policy.renew().performAndFill(getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks());
		assertThat(NotesAndAlertsSummaryPage.alert).valueContains("This Policy is Pending Renewal");
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		//R
		TimeSetterUtil.getInstance().nextPhase(renewalDate);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_EXPIRED);

		//R+9
		TimeSetterUtil.getInstance().nextPhase(renewalDate.plusDays(9));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Manually Renew Policy");
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();
		//The renewal lapse date will be displayed which is the expiration date of the current term.
		ManualRenewActionTab manualRenewActionTab = new ManualRenewActionTab();

		assertThat(manualRenewActionTab.getAssetList().getAsset(HomeCaMetaData.ManualRenewActionTab.RENEWAL_DATE).getValue())
				.isEqualTo(renewalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		manualRenewActionTab.fillTab(getTestSpecificTD("TestData"));
		manualRenewActionTab.submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: #V The 'Type' is displayed as 'Anniversary Renewal' in the transaction history");
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Anniversary Renewal");
		log.info("TEST: #V  Effective date is displayed as the 'Revised Renewal Date' (which is same as Expiration date and Renewal Lapse Date)");
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Effective Date"))
				.hasValue(renewalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
	}
}
