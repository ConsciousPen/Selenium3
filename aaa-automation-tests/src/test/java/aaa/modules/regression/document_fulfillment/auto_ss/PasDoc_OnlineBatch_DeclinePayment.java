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
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class PasDoc_OnlineBatch_DeclinePayment extends AutoSSBaseTest {

	IBillingAccount billing = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario39(@Optional("") String state) {
		List<LocalDateTime> installmentDueDates;
		IBillingAccount billing = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;
		TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
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
			String amount;
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(dueDate)));
			JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(dueDate)));
			JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

			if (dueDate == 2) {
				declinePayment(policyNum, 2 - 1);
				declinePayment(policyNum, dueDate);
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);
			}
			//Scenario 41.2
			if (dueDate == 3) {
				TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(installmentDueDates.get(3)));
				declinePayment(policyNum, dueDate);
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5003);
			}
			//Scenario 42.1
			if (dueDate == 4) {
				declinePayment(policyNum, 4);
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5000);
			}
			//Scenario 42.2
			if (dueDate == 5) {
				TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(installmentDueDates.get(5)));
				declinePayment(policyNum, dueDate);
				PasDocImpl.verifyDocumentsGenerated(policyNum, _60_5000);
			}
		}
	}

	private void declinePayment(String policyNumber, int indexDD) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			mainApp().open();
			SearchPage.openBilling(policyNumber);
		}
		List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
		String amount = "(" + BillingHelper.getBillDueAmount(installmentDueDates.get(indexDD), "Bill") + ")";
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), amount);
	}

	private void verifyPaymentDeclined(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment")
				.setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}
}
