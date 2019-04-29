package aaa.modules.regression.finance.billing.home_ss.ho3;

import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.billing.BillingBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestFinanceSmallBalanceWriteOffOnLastInstallmentBillUnderpaidByFeeAmount extends BillingBaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives:  For a new business policy, make an installment payment for an
	 * amount over the ‘Pay in Full’ amount where the installment payment is the last installment.
	 * TC Steps:
	 * 1. Create Policy
	 * 2. Make all installment payments (last payment =  full amount - double fee)
	 * 3. Run aaaRefundGenerationAsyncJob (date = last installmet payment + 1d)
	 * 4. Check
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.WV})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22285")
	public void pas22285_testFinanceSmallBalanceWriteOffOnLastInstallmentBill_Underpaid(@Optional("WV") String state) {
		List<LocalDateTime> installmentDueDates;

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment plan", BillingConstants.PaymentPlan.QUARTERLY).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		SearchPage.openBilling(policyNumber);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		makeInstallmentPayment(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)), policyNumber, 0);
		makeInstallmentPayment(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)), policyNumber, 0);

		Dollar fee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Fee")
				.getCell("Amount").getValue());
		makeInstallmentPayment(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)), policyNumber, fee.multiply(2).negate());

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRefundDate(installmentDueDates.get(3)));
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell("Min. Due")
					.getValue()).isEqualTo("$10.00");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
					.getValue()).isEqualTo("Manual Payment");
		});
	}
}
