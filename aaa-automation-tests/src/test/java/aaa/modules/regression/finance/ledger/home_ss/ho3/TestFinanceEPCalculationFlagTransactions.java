package aaa.modules.regression.finance.ledger.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
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
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationFlagTransactions extends FinanceOperations {
    private ErrorTab errorTab = new ErrorTab();
    /**
     * @author Reda Kazlauskiene
     * Objectives : Flag Transactions
     * Preconditions:
     * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
     * 1. Create Annual Auto SS Policy with Effective date today
     * 2. Create first Endorsement (Remove one coverage, Increase other coverage) with date: Today +62 days (with txEffectiveDate -1)
     * 3. Add Manual Renew Flag
     * 4. Create Second Endorsement(Add one more coverage) with date: first endorsement +61 days (with txEffectiveDate -1)
     * 5. Delete Manual Renew Flag
     * 6. Add Do Not Renew Flag
     * 7. Create OOS Endorsement (Add one more coverage) with date: second endorsement + 64 dayst (with txEffectiveDate -95)
     * 8. Roll on Endorsement with available values (not current)
     * 9. Remove Do not renew Flag
     * 10. Verify Calculations
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

    @Parameters({"state"})
    @StateList(states = {Constants.States.AZ})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20300")
    public void pas20300_testFinanceEPCalculationFlagTransactions(@Optional("AZ") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime e1date = today.plusDays(62);
        LocalDateTime e2date = e1date.plusDays(61);
        LocalDateTime e3date = e2date.plusDays(64);
        LocalDateTime manualRenewDate = e1date.plusMonths(1).withDayOfMonth(1);
        LocalDateTime doNotRenewDate = e2date.plusMonths(1).withDayOfMonth(1);

        LocalDateTime jobEndDate  = PolicySummaryPage.getExpirationDate().plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);
        LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

        jobDate = runEPJobUntil(jobDate, e1date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e1date);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-1, "TestData_EndorsementAPRemoveCoverage");

        jobDate = runEPJobUntil(jobDate, manualRenewDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        log.info("TEST: Add Manual Renew for Policy #" + policyNumber);
        policy.manualRenew().perform(getPolicyTD("ManualRenew", "TestData"));
        assertThat(PolicySummaryPage.labelManualRenew).isPresent();

        jobDate = runEPJobUntil(jobDate, e2date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e2date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-1, "TestData_EndorsementAddCoverage");

        log.info("TEST: Remove Manual Renew for Policy #" + policyNumber);
        policy.removeManualRenew().perform(new SimpleDataProvider());
        assertThat(PolicySummaryPage.labelManualRenew).isPresent(false);

        jobDate = runEPJobUntil(jobDate, doNotRenewDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        //Set Do not Renew Flag
        log.info("TEST: Add Do Not Renew for Policy #" + policyNumber);
        policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
        assertThat(PolicySummaryPage.labelDoNotRenew).isPresent();

        jobDate = runEPJobUntil(jobDate, e3date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e3date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-95, "TestData_EndorsementAddSecondCoverage");

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

        policy.rollOn().perform(false, false);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        //Remove Flag
        log.info("TEST: Remove Do not Renew for Policy #" + policyNumber);
        policy.removeDoNotRenew().perform(new SimpleDataProvider());
        assertThat(PolicySummaryPage.labelDoNotRenew).isPresent(false);

        runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonTransactionHistory.click();

        assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
                .isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

        List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.ENDORSE,
                TxType.OOS_ENDORSE, TxType.ROLL_ON);
        validateEPCalculations(policyNumber, txTypes, today, expirationDate);
    }
}
