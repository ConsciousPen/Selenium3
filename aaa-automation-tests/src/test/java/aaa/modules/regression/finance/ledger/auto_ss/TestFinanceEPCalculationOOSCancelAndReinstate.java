package aaa.modules.regression.finance.ledger.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
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
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSCancelAndReinstate extends FinanceOperations {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS Cancel and Reinstate
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today (txEffectiveDate = today +1 month)
	 * 2. Create Endorsement (Remove one coverage, Increase other coverage) with date: Today +123 days (with txEffectiveDate -1)
	 * 3. Cancel Policy with date: endorsement +35 days (effectiveDate = txEffectiveDate)
	 * 4. Reinstate Policy with date: cancel policy +1 day (effectiveDate = txEffectiveDate)
	 * 5. Verify Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.WV, Constants.States.KY, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
	public void pas20277_testFinanceEPCalculationOOSCancelAndReinstate(@Optional("AZ") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime txEffectiveDate = today.plusMonths(1);
		LocalDateTime eDate = today.plusDays(123);
		LocalDateTime cDate = eDate.plusDays(35);
		LocalDateTime rDate = cDate.plusMonths(1);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

		jobDate = runEPJobUntil(jobDate, eDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(eDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		createEndorsement(-1, "TestData_EndorsementAPRemoveCoverage");

		jobDate = runEPJobUntil(jobDate, cDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(cDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		cancelPolicy(txEffectiveDate);

		jobDate = runEPJobUntil(jobDate, rDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(rDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		reinstatePolicy(txEffectiveDate);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(new Dollar(PolicySummaryPage.tableTransactionHistory.getRow(1)
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue()))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));
	}
}