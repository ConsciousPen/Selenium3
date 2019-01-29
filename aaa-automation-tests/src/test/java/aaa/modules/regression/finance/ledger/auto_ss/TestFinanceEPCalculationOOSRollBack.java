package aaa.modules.regression.finance.ledger.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.product.LedgerHelper;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSRollBack extends FinanceOperations {

	private ErrorTab errorTab = new ErrorTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS RollBack
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
	@StateList(states = {Constants.States.WV, Constants.States.KY, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
	public void pas20277_testFinanceEPCalculationOOSRollBack(@Optional("AZ") String state) {

		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime txEffectiveDate = today.plusMonths(1);
		LocalDateTime e1date = today.plusDays(62);
		LocalDateTime e2date = e1date.plusMonths(1);
		LocalDateTime e3date = e2date.plusMonths(7);
		LocalDateTime rbDate = e3date.plusDays(3);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, e1date, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e1date);
		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement1");

		jobDate = runEPJobUntil(jobDate, e2date, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e2date);
		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement2");

		jobDate = runEPJobUntil(jobDate, e3date, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e3date);
		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement3");

		jobDate = runEPJobUntil(jobDate, rbDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(rbDate);

		searchForPolicy(policyNumber);
		rollBackEndorsement(txEffectiveDate);

		runEPJobUntil(jobDate, jobEndDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		searchForPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.ENDORSE,
				TxType.ENDORSE, TxType.ROLL_BACK);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}