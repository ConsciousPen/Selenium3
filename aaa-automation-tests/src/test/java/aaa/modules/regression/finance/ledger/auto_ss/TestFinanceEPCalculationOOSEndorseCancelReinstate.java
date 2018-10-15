package aaa.modules.regression.finance.ledger.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
	public void pas20277_testFinanceEPCalculationOOSEndorseCancelReinstate(@Optional("AZ") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime txEffectiveDate = today.plusMonths(1);
		LocalDateTime e1Date = today.plusDays(62);
		LocalDateTime cDate = e1Date.plusMonths(1);
		LocalDateTime rDate = cDate.plusMonths(1);
		LocalDateTime e2Date = rDate.plusDays(5);

		LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

		jobDate = runEPJobUntil(jobDate, e1Date);
		TimeSetterUtil.getInstance().nextPhase(e1Date);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(-1, "TestData_EndorsementAPRemoveCoverage");

		jobDate = runEPJobUntil(jobDate, cDate);
		TimeSetterUtil.getInstance().nextPhase(cDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		cancelPolicy(-1);

		jobDate = runEPJobUntil(jobDate, rDate);
		TimeSetterUtil.getInstance().nextPhase(rDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		reinstatePolicy(-1);

		jobDate = runEPJobUntil(jobDate, e2Date);
		TimeSetterUtil.getInstance().nextPhase(e2Date);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createEndorsement(txEffectiveDate, "TestData_EndorsementAPRemoveCoverage");

		errorTab.overrideAllErrors();
		errorTab.buttonOverride.click();
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

		policy.rollOn().perform(false, false);

		runEPJobUntil(jobDate, jobEndDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(new Dollar(PolicySummaryPage.tableTransactionHistory.getRow(1)
				.getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue()))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));
	}
}