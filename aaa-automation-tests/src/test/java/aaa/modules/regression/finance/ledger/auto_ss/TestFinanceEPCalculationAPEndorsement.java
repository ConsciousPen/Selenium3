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
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationAPEndorsement extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS RollBack AP Endorsement
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today (txEffectiveDate = today + 1 month)
	 * 2. Create Endorsement (Add/increase coverage) with date: Today + 3 months (with txEffectiveDate -1)
	 * 3. Check Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21446")
	public void pas21446_testFinanceEPCalculationAPEndorsement(@Optional("AZ") String state) {
		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime eDate = today.plusMonths(3);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, eDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(eDate);

		searchForPolicy(policyNumber);

		createEndorsement(-1, "TestData_EndorsementAddCoverage");

		runEPJobUntil(jobDate, jobEndDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);

		searchForPolicy(policyNumber);

		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}