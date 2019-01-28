package aaa.modules.regression.finance.ledger.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;
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
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationFlatCancellation extends FinanceOperations {

	private ErrorTab errorTab = new ErrorTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Flat Cancelation
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today
	 * 2. Cancel Policy with date: Today +35 days (with txEffectiveDate -30)
	 * 3. Verify Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21444")
	public void pas21444_testFinanceEPCalculationFlatCancellation(@Optional("AZ") String state) {

		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime cancelDate = today.plusDays(35);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, cancelDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(cancelDate);

		searchForPolicy(policyNumber);

		cancelPolicy(-35, getPolicyType());

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		searchForPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.CANCEL);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}