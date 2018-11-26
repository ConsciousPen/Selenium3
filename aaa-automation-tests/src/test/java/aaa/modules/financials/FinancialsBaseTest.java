package aaa.modules.financials;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class FinancialsBaseTest extends FinancialsTestDataFactory {

    private static final String UNEARNED_INCOME_1015 = "1015";
    private static final String CHANGE_IN_UNEARNED_INCOME_1021 = "1021";

	protected String createFinancialPolicy() {
		return createFinancialPolicy(getPolicyTD());
	}

	protected String createFinancialPolicy(TestData td) {
		String policyNum = createPolicy(td);
		ALL_POLICIES.add(policyNum);
		return policyNum;
	}

	protected Dollar payAmountDue(){
		// Open Billing account and Pay min due for the renewal
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);

		// Open Policy Summary Page
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		return due;
	}

	public static void validateAccounts() {
		if (TimeSetterUtil.getInstance().getCurrentTime().getDayOfMonth() != 1) {
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(1));
		}
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		assertSoftly(softly -> softly.assertThat(DBService.get().getValue(FinancialsSQL.getTotalEntryAmtForAcct(UNEARNED_INCOME_1015)).get())
				.isEqualTo(DBService.get().getValue(FinancialsSQL.getTotalEntryAmtForAcct(CHANGE_IN_UNEARNED_INCOME_1021)).get()));
	}

}
