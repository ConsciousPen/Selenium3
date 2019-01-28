package aaa.modules.regression.finance.ledger.home_ss.ho3;

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
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationCancelFlagTransactions extends FinanceOperations {

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel flag transactions
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Home SS Policy with Effective date today
	 * 2. Add Cancel Notice Flag
	 * 3. At P3 Delete Cancel Notice Flag
	 * 4. Verify Calculations
	 */
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20300")
	public void pas20300_testFinanceEPCalculationCancelFlagTransactions(@Optional("PA") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime cancelNoticeDate = today.plusDays(50);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		log.info("TEST: Add Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		jobDate = runEPJobUntil(jobDate, cancelNoticeDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(cancelNoticeDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		log.info("TEST: Remove Cancel Notice for Policy #" + policyNumber);
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}
