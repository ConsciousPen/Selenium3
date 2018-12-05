package aaa.modules.regression.finance.ledger.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFinanceEPCalculationSmallBalanceWriteOff extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maksim Paitrouski
	 * Objectives : Small Balance Write-Off
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Monthly Auto SS Policy with Effective date today
	 * 2. Receive pay-in-full payment-$5 on date (today + 1 month - 20 days)
	 * 3. Run Small Balance Write-Off
	 * 4. Verify Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21458")
	public void pas21458_testFinanceEPCalculationSmallBalanceWriteOff(@Optional("AZ") String state) {
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumAndCoveragesTab|Payment Plan", "Eleven Pay - Standard").resolveLinks();

		String policyNumber = openAppAndCreatePolicy(policyTD);
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime pDate = today.plusMonths(10).minusDays(20);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), BillingSummaryPage.getTotalDue().add(-5));

		jobDate = runEPJobUntil(jobDate, pDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(pDate);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		billingAccount.generateFutureStatement().perform();

		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
				.getCell("Subtype/Reason").getValue()).isEqualTo("Small Balance Write-off");

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber, "Policy Active");
		PolicySummaryPage.buttonTransactionHistory.click();

		BigDecimal issueEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Issue")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());

		assertThat(new Dollar(issueEndingPremium))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE);
		List<TxWithTermPremium> txsWithPremiums = createTxsWithPremiums(policyNumber, txTypes);
		txsWithPremiums.get(0).setActualPremium(issueEndingPremium);
		validateEPCalculationsFromTransactions(policyNumber, txsWithPremiums, today.toLocalDate(), expirationDate.toLocalDate());
	}
}