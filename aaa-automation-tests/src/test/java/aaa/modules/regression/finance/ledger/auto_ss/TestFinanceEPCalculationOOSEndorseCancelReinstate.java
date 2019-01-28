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
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

public class TestFinanceEPCalculationOOSEndorseCancelReinstate extends FinanceOperations {

	private ErrorTab errorTab = new ErrorTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS Cancel and Reinstate
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today
	 * 2. Create first Endorsement (change one coverage) with date: Today +62 days (with txEffectiveDate -1)
	 * 3. Cancel Policy with date: endorsement +1 month (with txEffectiveDate -1)
	 * 4. Reinstate Policy with date: cancel +1 month (with txEffectiveDate -1)
	 * 5. Create second Endorsement (Remove one coverage, Increase other coverage) with date: reinstate +5 days (with txEffectiveDate -1)
	 * 6. Roll on Endorsement, Cancellation, Reinstatement
	 * 7. Verify Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.WV, Constants.States.KY, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
	public void pas20277_testFinanceEPCalculationOOSEndorseCancelReinstate(@Optional("KY") String state) {

		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime txEffectiveDate = today.plusMonths(1);
		LocalDateTime e1Date = today.plusDays(62);
		LocalDateTime cDate = e1Date.plusMonths(1);
		LocalDateTime rDate = cDate.plusMonths(1);
		LocalDateTime e2Date = rDate.plusDays(5);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, e1Date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e1Date);

		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement1");

		jobDate = runEPJobUntil(jobDate, cDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(cDate);

		searchForPolicy(policyNumber);
		cancelPolicy(-1, getPolicyType());

		jobDate = runEPJobUntil(jobDate, rDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(rDate);

		searchForPolicy(policyNumber);
		reinstatePolicy(-1);

		jobDate = runEPJobUntil(jobDate, e2Date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e2Date);

		searchForPolicy(policyNumber);
		createEndorsement(txEffectiveDate, "TestData_Endorsement2");

		errorTab.overrideAllErrors();
		errorTab.submitTab();
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

		//Roll on Endorsement
		policy.rollOn().perform(false, false);
		//Roll on Cancellation
		policy.rollOn().perform(false, false);
		//Roll on Reinstatement
		policy.rollOn().perform(false, false);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		searchForPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.CANCEL, TxType.REINSTATE_LAPSE,
				TxType.OOS_ENDORSE, TxType.ROLL_ON, TxType.ROLL_ON_CANCEL, TxType.ROLL_ON_REINSTATE);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}