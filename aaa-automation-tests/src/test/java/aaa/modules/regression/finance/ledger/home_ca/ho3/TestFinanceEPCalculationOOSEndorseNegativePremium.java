package aaa.modules.regression.finance.ledger.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSEndorseNegativePremium extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : OOS Endorse (Negative Premium)
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Home CA Policy with Effective date today
	 * 2. Create first Endorsement (Decrease coverage) with date: Today +62 days (with txEffectiveDate -1)
	 * 3. Create Second Endorsement(Increase coverage) with date: first endorsement +61 days (with txEffectiveDate -1)
	 * 4. Create OOS Endorsement (Decrease coverage) with date: second endorsement + 89 dayst (with txEffectiveDate -120)
	 * 5. Roll on Endorsement with available values (not current)
	 * 6. Verify Calculations
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20308")
	public void pas20308_testFinanceEPCalculationOOSEndorseNegativePremium(@Optional("CA") String state) {

		String policyNumber = openAppAndCreatePolicy();

		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime e1date = today.plusDays(62);
		LocalDateTime e2date = e1date.plusDays(61);
		LocalDateTime e3date = e2date.plusDays(89);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		assertThat(new EndorsementTab().tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-60");

		jobDate = runEPJobUntil(jobDate, e1date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e1date);
		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement1");

		jobDate = runEPJobUntil(jobDate, e2date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e2date);
		searchForPolicy(policyNumber);
		createEndorsement(-1, "TestData_Endorsement2");

		jobDate = runEPJobUntil(jobDate, e3date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		TimeSetterUtil.getInstance().nextPhase(e3date);
		searchForPolicy(policyNumber);
		createEndorsement(-120, "TestData_Endorsement3");

		policy.rollOn().perform(false, false);

		runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		searchForPolicy(policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		BigDecimal issueEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Issue")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		BigDecimal endorsementEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Endorsement")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		BigDecimal backedOffEndorsementEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Backed Off Endorsement")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		BigDecimal oosEndorsementEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "OOS Endorsement")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		BigDecimal rolledOnEndorsementEndingPremium = LedgerHelper.toBigDecimal(PolicySummaryPage.tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, "Rolled On Endorsement")
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());

		assertThat(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)))
				.isEqualTo(new Dollar(rolledOnEndorsementEndingPremium));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.ENDORSE,
				TxType.OOS_ENDORSE, TxType.ROLL_ON);

		List<TxWithTermPremium> txsWithPremiums = createTxsWithPremiums(policyNumber, txTypes);
		txsWithPremiums.get(0).setActualPremium(issueEndingPremium);
		txsWithPremiums.get(1).setActualPremium(endorsementEndingPremium);
		txsWithPremiums.get(2).setActualPremium(backedOffEndorsementEndingPremium);
		txsWithPremiums.get(3).setActualPremium(oosEndorsementEndingPremium);
		txsWithPremiums.get(4).setActualPremium(rolledOnEndorsementEndingPremium);
		validateEPCalculationsFromTransactions(policyNumber, txsWithPremiums, today.toLocalDate(), expirationDate.toLocalDate());
	}
}
