package aaa.modules.bct.service;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class CancelPolicyTest extends BackwardCompatibilityBaseTest {

	/**
	 * @author Deloite
	 * @name View Cancelled policy status Home
	 * @scenario
	 * Naviagate to Billing Page and pay min due
	 * Naviagate to Policy consolidated view and verify the policy cancelled notice flag is removed
	 * Check:
	 * Min due or total due need to be left  in order to pay the amount
	 * Cancelled notice flag must be removed
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_012_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		verifyCancelNoticeIsAbsent(policyNumber);
	}

	/**
	 * @author Deloite
	 * @name View Cancelled Auto policy status
	 * @scenario
	 * Naviagate to Billing Page and pay min due
	 * Naviagate to Policy consolidated view and verify the policy cancelled notice flag is removed
	 * Check:
	 * Min due or total due need to be left  in order to pay the amount
	 * Cancelled notice flag must be removed
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_015_CancelPolicy(@Optional("CA") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		verifyCancelNoticeIsAbsent(policyNumber);
	}

	private void verifyCancelNoticeIsAbsent(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		BillingSummaryPage.openPolicy(1);

		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
	}

}
