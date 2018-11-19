package aaa.modules.regression.finance.ledger.home_ca.ho3;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
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

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFinanceEPCalculationEPWriteOff extends FinanceOperations {

	/**
	 * @author Maksim Paitrouski
	 * Objectives : EP Write-Off
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Monthly Home CA Policy with Effective date today
	 * 2. Issue first installment bill
	 * 3. Receive/apply installment payment(today + 1 month - 20 days)
	 * 4. Cancel (today + 3 months)
	 * 5. Generate EP Write-Off
	 * 6. Verify Calculations
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21457")
	public void pas21457_testFinanceEPCalculationEPWriteOff(@Optional("CA") String state) {
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime pDate = today.plusMonths(1).minusDays(20);
		LocalDateTime cDate = today.plusMonths(4);

		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime jobEndDate = expirationDate.plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime earnedPremiumWriteOff = getTimePoints().getEarnedPremiumWriteOff(cDate);

		jobDate = runEPJobUntil(jobDate, pDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(pDate);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		billingAccount.generateFutureStatement().perform();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), BillingSummaryPage.getMinimumDue());

		jobDate = runEPJobUntil(jobDate, cDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(cDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		cancelPolicy(cDate, getPolicyType());

		jobDate = runEPJobUntil(jobDate, earnedPremiumWriteOff, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		TimeSetterUtil.getInstance().nextPhase(earnedPremiumWriteOff);
		JobUtils.executeJob(Jobs.earnedPremiumWriteoffProcessingJob);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		BigDecimal issueEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Issue")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		BigDecimal endorsementEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Cancellation")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());

		assertThat(new Dollar(endorsementEndingPremium))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.CANCEL);
		List<TxWithTermPremium> txsWithPremiums = createTxsWithPremiums(policyNumber, txTypes);
		txsWithPremiums.get(0).setActualPremium(issueEndingPremium);
		txsWithPremiums.get(1).setActualPremium(endorsementEndingPremium);
		validateEPCalculationsFromTransactions(policyNumber, txsWithPremiums, today.toLocalDate(), expirationDate.toLocalDate());
	}
}