package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static aaa.main.enums.DocGenEnum.Documents.*;

public class PasDoc_OnlineBatch_Notice extends AutoSSBaseTest {

	private TestData tdBilling = testDataManager.billingAccount;
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private IBillingAccount billing = new BillingAccount();


	/**
	 * <b> Test PasDoc Scenarios - Decline Payment </b>
	 * <p>  Steps:
	 * <p>  Create policy with Monthly Payment Plan
	 * <p>  Accept Payment
	 * <p>  Decline Payment(reason - No Fee No oRestriction) and verify Document - 60_5002
	 * <p>  Accept Payment
	 * <p>  Decline Payment(reason - Fee No Restriction) and verify Document - 60_5001
	 * <p>  Update Billing Account - Add AutoPay
	 * <p>  Shift time with execution jobs: aaaBillingInvoiceAsyncTaskJob and aaaRecurringPaymentsProcessingJob
	 * <p>  Decline Payment(reason - Fee Restriction) and verify Document - 60_5003
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario39_41(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime dueDate1;
		LocalDateTime dueDate2;
		LocalDateTime dueDate3;
		LocalDateTime billDueDate;
		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusMonths(2).with(DateTimeUtils.nextWorkingDay));
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(td);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		dueDate1 = installmentDueDates.get(1);
		dueDate2 = installmentDueDates.get(2);
		dueDate3 = installmentDueDates.get(3);

		billing.acceptPayment().perform(check_payment, new Dollar(200));
		billing.acceptPayment().perform(check_payment, new Dollar(300));

		//Decline previous manual payment with reason "Fee + Restriction" (to get 60 5002)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), "($200.00)");
		verifyPaymentDeclined("200");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5002);

		//Scenario 40 Decline previous manual payment with reason "Fee + Restriction" (to get 60 5001)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), "($300.00)");
		verifyPaymentDeclined("300");
		DocGenHelper.verifyDocumentsGenerated(policyNum, _60_5001);

		//Scenario 41.1 Decline deposit payment with reason "Fee + No Restriction" (to get 605003)
		new BillingAccount().update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));
		//DD1
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate1));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		billDueDate = getTimePoints().getBillDueDate(dueDate1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dueDate1));
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		declineRecurringPayment(policyNum, billDueDate, "TestData_FeeRestriction");

		//DD2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate2));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		billDueDate = getTimePoints().getBillDueDate(dueDate2);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		declineRecurringPayment(policyNum, billDueDate, "TestData_FeeRestriction");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);

		//DD3 Scenario 41.2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate3));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		billDueDate = getTimePoints().getBillDueDate(dueDate3);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(dueDate3));

		mainApp().open();
		SearchPage.openBilling(policyNum);
		declineRecurringPayment(policyNum, billDueDate, "TestData_FeeRestriction");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);
	}

	/**
	 * <b> Test PasDoc Scenarios - NSF Notice </b>
	 * <p>  Steps:
	 * <p>  Create policy with Monthly Payment Plan
	 * <p>  Update Billing account - Add AutoPay
	 * <p>  Shift time to DD1-20 (Bill generation date) and run aaaBillingInvoiceAsyncTaskJob
	 * <p>  Shift time to DD1-20 (Bill Due date) and run aaaRecurringPaymentsProcessingJob
	 * <p>  Decline Payment(reason - Fee Restriction) and verify Document - 60_5000
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario42_1(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime dueDate1;
		LocalDateTime billDueDate;
		TestData policyWithMontlyPaymentPlan = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");

		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(policyWithMontlyPaymentPlan);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		dueDate1 = installmentDueDates.get(1);

		new BillingAccount().update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate1));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		billDueDate = getTimePoints().getBillDueDate(dueDate1);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		declineRecurringPayment(policyNum, billDueDate, "TestData_FeeRestriction");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5000);
	}

	/**
	 * <b> Test PasDoc Scenarios - NSF Notice </b>
	 * <p>  Steps:
	 * <p>  Create policy with Monthly Payment Plan
	 * <p>  Update Billing account - Add AutoPay
	 * <p>  Shift time to DD1-20 (Bill generation date) and run aaaBillingInvoiceAsyncTaskJob
	 * <p>  Shift time to DD1-20 (Bill Due date) and run aaaRecurringPaymentsProcessingJob
	 * <p>  Shift time to DD1+1 (Update Status)
	 * <p>  Decline Payment(reason - Fee Restriction) and verify Document - 60_5000
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario42_2(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime dueDate1;
		LocalDateTime billDueDate;
		TestData policyWithMontlyPaymentPlan = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(policyWithMontlyPaymentPlan);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		dueDate1 = installmentDueDates.get(1);
		new BillingAccount().update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate1));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		billDueDate = getTimePoints().getBillDueDate(dueDate1);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(dueDate1));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		declineRecurringPayment(policyNum, billDueDate, "TestData_FeeRestriction");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5000);
	}

	/**
	 * <b> Test PasDoc Scenarios - EXPIRATION NOTICE </b>
	 * <p>  Steps:
	 * <p>  Create policy
	 * <p>  Shift time to R-35 and execute both renewalOfferGeneration
	 * <p>  Shift time to R-20 and execute aaaRenewalNoticeBillAsyncJob
	 * <p>  Shift time to R and execute aaaDataUpdateJob
	 * <p>  Shift time to R+10 and execute aaaRenewalNoticeBillAsyncJob
	 * <p>  Verify document - AH64XX
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario43(@Optional("") String state) {
		LocalDateTime renewalDate;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		renewalDate = PolicySummaryPage.getExpirationDate();

		//R-35
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		//R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);

		//R
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		mainApp().open();

		//R+10
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getInsuranceRenewalReminderDate(renewalDate));
		JobUtils.executeJob(BatchJob.aaaRenewalReminderGenerationAsyncJob);

		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH64XX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Remove Cancel Notice </b>
	 * <p>  Steps:
	 * <p>  Create policy
	 * <p>  Cancel Notice
	 * <p>  Remove Cancel Notice
	 * <p>  Verify document - AHCWXX(true), AH35XX(false)
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario52(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AHCWXX);
		PasDocImpl.verifyDocumentsGenerated(false, policyNumber, AH35XX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Remove Cancel Notice </b>
	 * <p>  Steps:
	 * <p>  Create policy with Auto Pay
	 * <p>  Cancel Notice (Reason - UW)
	 * <p>  Remove Cancel Notice
	 * <p>  Verify document - AHCWXX(true), AH35XX(true)
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario53(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Select payment plan = Monthly and activate AutoPay
		TestData tdAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_EnabledAutoPay").resolveLinks());
		String policyNumber = createPolicy(tdAutoPay);

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AH35XX);

		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData").adjust(TestData
						.makeKeyPath("CancelNoticeActionTab", "Cancellation Reason"),
				"Underwriting - Fraudulent Misrepresentation"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AHCWXX);
	}

	private void declineRecurringPayment(String policyNumber, LocalDateTime installmentDueDate, String declineReason) {
		HashMap<String, String> map = new HashMap<>();
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, getTimePoints().getBillDueDate(installmentDueDate).format(DateTimeUtils.MM_DD_YYYY));
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.PAYMENT);
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT);
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.STATUS, BillingConstants.PaymentsAndOtherTransactionStatus.ISSUED);
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", declineReason), map);
	}

	private void verifyPaymentDeclined(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment")
				.setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}
}
