package aaa.modules.regression.finance.ledger.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSEndorseNegativePremium extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20308")
	public void pas20308_testFinanceEPCalculationOOSEndorseNegativePremium(@Optional("CA") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime e1date = today.plusDays(62);
		LocalDateTime e2date = e1date.plusDays(61);
		LocalDateTime e3date = e2date.plusDays(89);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

		jobDate = runEPJobUntil(jobDate, e1date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e1date);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(-1, "TestData_Endorsement1");

		jobDate = runEPJobUntil(jobDate, e2date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e2date);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(-1, "TestData_Endorsement2");

		jobDate = runEPJobUntil(jobDate, e3date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e3date);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(-120, "TestData_Endorsement3");

		policy.rollOn().perform(false, false);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(new Dollar(PolicySummaryPage.tableTransactionHistory.getRow(1)
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue()))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));
	}
}