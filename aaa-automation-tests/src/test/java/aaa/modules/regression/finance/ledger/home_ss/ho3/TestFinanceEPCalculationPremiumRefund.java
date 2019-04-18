package aaa.modules.regression.finance.ledger.home_ss.ho3;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationPremiumRefund extends FinanceOperations {
	/**
	 * @author Maksim Piatrouski
	 * Objectives : Premium Refund
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy (with pay-in-full payment) with Effective date today
	 * 2. Create Endorsement(Reduce or delete coverage) with date: first endorsement +3 months (with txEffectiveDate -1)
	 * 3. Generate Premium Refund
	 * 4. Verify Calculations
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21456")
	public void pas21456_testFinanceEPCalculationPremiumRefund(@Optional("AZ") String state) {

		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime eDate = today.plusMonths(3);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		jobDate = runEPJobUntil(jobDate, eDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(eDate);

		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement");

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar refundAmount = BillingSummaryPage.getTotalPaid();

		new BillingAccount().refund().perform(testDataManager.billingAccount.getTestData("Refund", "TestData_Check"), refundAmount);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		searchForPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)))
				.isEqualTo(LedgerHelper.getEndingActualPremium(policyNumber));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}
