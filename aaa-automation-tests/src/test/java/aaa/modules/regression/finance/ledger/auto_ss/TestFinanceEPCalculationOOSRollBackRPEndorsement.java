package aaa.modules.regression.finance.ledger.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSRollBackRPEndorsement extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS RollBack PR Endorsement
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today (txEffectiveDate = today + 1 month)
	 * 2. Create First Endorsement (Change some coverages) with date: Today +62 days (with txEffectiveDate -1)
	 * 3. Create Second Endorsement(Add one coverage, remove one coverage and add one coverage) with date: first endorsement +1 month (with txEffectiveDate -1)
	 * 4. Create Third Endorsement(Add one coverage, remove one coverage and add one coverage) with date: second endorsement +7 month (with txEffectiveDate -1)
	 * 4. Roll Back Endorsement with date: third endorsement +3 days (with txEffectiveDate from step 1)
	 * 5. Roll on Endorsement with available values (not current)
	 * 6. Verify Calculations
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21454")
	public void pas21454_testFinanceEPCalculationOOSRollBackRPEndorsement(@Optional("AZ") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime eDate = today.plusMonths(3);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, eDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(eDate);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(-1, "TestData_EndorsementRemoveCoverage");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		rollBackEndorsement(today);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.ROLL_BACK);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}