package aaa.modules.regression.finance.billing.home_ss.ho4;

import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
	 * Objectives : check reverse rules of a systematically created escheatment transaction(s).
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 7. Check that Reverse action exists in Escheatment transaction
	 * 8. Turn time to the next month and check that Reverse action does not exist in Escheatment transaction
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-25635")
	public void pas25635_testFinancePolicyEscheatmentCheckRestrictReversals(@Optional("PA") String state) {
		String policyNumber = createEscheatmentTransaction();

		// Current date plusMonths(13) - Reverse action exists in Escheatment transaction
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).contains("Reverse");

		// Set date to the next month - Reverse action does not exist in Escheatment transaction
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(14));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		assertThat(escheatmentActions.getValue()).doesNotContain("Reverse");
	}
}
