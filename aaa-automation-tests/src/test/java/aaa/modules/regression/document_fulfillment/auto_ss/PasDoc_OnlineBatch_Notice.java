package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

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
	 * <p>  Decline Payment(reason - Fee N oRestriction) and verify Document - 60_5001
	 * <p>  Update Billing Account - Add AutoPay
	 * <p>  Shift time with execution jobs: aaaBillingInvoiceAsyncTaskJob and aaaRecurringPaymentsProcessingJob
	 * <p>  Decline Payment(reason - Fee Restriction) and verify Document - 60_5003
	 * <p>  Run job policyUpdateStatus
	 * <p>  Decline Payment(reason - Fee Restriction) and verify Document - 60_5000
	 *
	 * @param state
	 * @author Denis Semenov
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario39(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");

		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(td);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

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
		for (int dueDate = 1; dueDate <= 5; dueDate++) {
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(dueDate)));
			JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(dueDate)));
			JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

			if (dueDate == 1) {
				declinePayment(policyNum, 1, "TestData_FeeRestriction");
			}
			if (dueDate == 2) {
				declinePayment(policyNum, 2, "TestData_FeeRestriction");
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);
			}
			//Scenario 41.2
			if (dueDate == 3) {
				TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(installmentDueDates.get(3)));
				declinePayment(policyNum, 3, "TestData_FeeRestriction");
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);
			}
		}
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario42(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		TestData policyWithMontlyPaymentPlan = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");

		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(policyWithMontlyPaymentPlan);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		new BillingAccount().update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

		declinePayment(policyNum, 1, "TestData_FeeRestriction");
		PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5000);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario42_1(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		TestData policyWithMontlyPaymentPlan = getPolicyTD()
				.adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(policyWithMontlyPaymentPlan);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		new BillingAccount().update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(installmentDueDates.get(1)));
		declinePayment(policyNum, 1, "TestData_FeeRestriction");
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
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario43(@Optional("") String state) {
		LocalDateTime renewalDate;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		renewalDate = PolicySummaryPage.getExpirationDate();

		//R-35
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		//R
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();

		//R+10
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getInsuranceRenewalReminderDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);

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

	private void declinePayment(String policyNumber, int dueDate, String declineReason) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			mainApp().open();
			SearchPage.openBilling(policyNumber);
		}
		List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
		String amount = "(" + BillingHelper.getBillDueAmount(installmentDueDates.get(dueDate), "Bill") + ")";
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", declineReason), amount);
	}

	private void verifyPaymentDeclined(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment")
				.setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}
}
