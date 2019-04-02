package aaa.modules.regression.finance.billing.home_ss.ho4;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.temporal.TemporalAdjusters;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Cell;

public class TestFinancePolicyEscheatmentCheckRestrictReversals extends FinanceOperations {

	/**
	 * @author Vilnis Liepins
	 * Objectives : Reverse action is hidden in the next calendar month, following the month in which Escheatment occurs.
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * TC Steps:
	 * 1. Navigate to BA page and check that Reverse action exists on Escheatment transaction date
	 * 2. Move time forward for a month and check that Reverse action does not exist in Escheatment transaction
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-25635")
	public void pas25635_testFinancePolicyEscheatmentCheckRestrictReversals(@Optional("VA") String state) {
		String policyNumber = createEscheatmentTransaction();

		// 1. Check that Reverse action exists on Escheatment transaction date
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).contains("Reverse");

		// 2. Move time forward to the end of month and check that Reverse action is exist
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().with(TemporalAdjusters.lastDayOfMonth()));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		assertThat(escheatmentActions.getValue()).contains("Reverse");

		// 3. Move time forward for a month and check that Reverse action does not exist
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		assertThat(escheatmentActions.getValue()).doesNotContain("Reverse");
	}
}
