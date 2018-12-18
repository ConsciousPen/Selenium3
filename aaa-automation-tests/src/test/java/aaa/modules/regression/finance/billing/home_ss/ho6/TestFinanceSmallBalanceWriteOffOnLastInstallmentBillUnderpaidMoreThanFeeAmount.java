package aaa.modules.regression.finance.billing.home_ss.ho6;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.billing.BillingBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestFinanceSmallBalanceWriteOffOnLastInstallmentBillUnderpaidMoreThanFeeAmount extends BillingBaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives:  For a new business policy, make an installment payment for an
	 * amount over the ‘Pay in Full’ amount where the installment payment is the last installment.
	 * TC Steps:
	 * 1. Create Policy
	 * 2. Make all installment payments (last payment - add 1$)
	 * 3. Run aaaRefundGenerationAsyncJob (date = last installmet payment + 1d)
	 * 4. Check Small Balance Write-off transaction created
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22285")
	public void pas22285_testFinanceSmallBalanceWriteOffOnLastInstallmentBillUnderpaidMoreThanFeeAmount(@Optional("NJ") String state) {
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime pDate = today.plusMonths(3).minusDays(20);
		LocalDateTime p2Date = pDate.plusMonths(3);
		LocalDateTime p3Date = p2Date.plusMonths(3);
		LocalDateTime refundDate = p3Date.plusDays(1);

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment plan", BillingConstants.PaymentPlan.QUARTERLY).resolveLinks();
		String policyNumber = createPolicy(policyTD);

		makeInstallmentPayment(pDate, policyNumber, 0);
		makeInstallmentPayment(p2Date, policyNumber, 0);
		makeInstallmentPayment(p3Date, policyNumber, -15);

		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell("Min. Due")
					.getValue()).isEqualTo("$15.00");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
					.getValue()).isEqualTo("Manual Payment");
		});
	}
}
