package aaa.modules.regression.finance.billing.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.billing.BillingBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestFinanceSmallBalanceWriteOffWhenPolicyUnderpaid extends BillingBaseTest {

	/**
	 * @author Reda Kazlauskiene
	 * TC Steps:
	 * 1. Create Quote
	 * 2. Issue Quote and pay premium: full - 5$
	 * 3. Run aaaRefundGenerationAsyncJob
	 * 4. Check Small Balance Write-off transaction created
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-24613")
	public void pas24613_testFinanceSmallBalanceWriteOffWhenPolicyUnderpaid(@Optional("WV") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumAndCoveragesTab|Payment Plan", BillingConstants.PaymentPlan.AUTO_ELEVEN_PAY).resolveLinks();
		String policyNumber = createPolicy(policyTD);

		SearchPage.openBilling(policyNumber);

		Dollar totalPayment = BillingSummaryPage.getTotalDue().subtract(new Dollar(5));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalPayment);
		assertThat(BillingSummaryPage.getTotalDue().toString()).isEqualTo("$5.00");

		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
					.getValue()).isEqualTo("Small Balance Write-off");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Amount")
					.getValue()).isEqualTo("($5.00)");
		});
	}
}
