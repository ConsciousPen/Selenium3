package aaa.modules.regression.finance.billing.home_ca.ho3;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestFinancePolicyEscheatmentCheckReversals extends PolicyBaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "Escheatment" not displayed in Transaction Subtype drop down section
	 * on the PAS UI billing page - the Other Transactions button under the payments and other transactions section
	 * TC Steps:
	 * 1. Create Policy
	 * 2. Navigate Billing - Other Transactions
	 * 3. Set Transaction Type = "Adjustment"
	 * 4. Check "Escheatment" not displayed in Transaction Subtype drop down
	 */

	OtherTransactionsActionTab otherTransactionsActionTab = new OtherTransactionsActionTab();
	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-18992")
	public void pas18992_testFinancePolicyEscheatmentCheckReversals() {

		//preconditions
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), new Dollar(25));

		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().perform();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(13));

		JobUtils.executeJob(Jobs.aaaEscheatmentProcessAsyncJob);

		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
	}
}
