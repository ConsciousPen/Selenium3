package aaa.modules.regression.finance.billing.home_ca.ho3;

import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Cell;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFinanceCheckPermissionForReversal extends FinanceOperations {

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : verify Reverse action
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 7. Login with User (with L41 privileges)
	 * 7. Navigate to BA
	 * TC Steps:
	 * 1. Reverse action should exist
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-18992")
	public void pas18992_testFinancePolicyEscheatmentCheckReversals_L41(@Optional("CA") String state) {

		String policyNumber = createEscheatmentTransaction();

		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.L41);
		SearchPage.openBilling(policyNumber);

		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).contains("Reverse");
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : verify Reverse action
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 7. Login with User (with C32 privileges)
	 * 7. Navigate to BA
	 * TC Steps:
	 * 1. Reverse action shouldn't exist
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-18992")
	public void pas18992_testFinancePolicyEscheatmentCheckReversals_C32(@Optional("CA") String state) {

		String policyNumber = createEscheatmentTransaction();

		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.C32);
		SearchPage.openBilling(policyNumber);

		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).doesNotContain("Reverse");
	}
}
