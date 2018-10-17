package aaa.modules.regression.finance.ledger.auto_ss;

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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFinanceEPCalculationMultipleOOSEndorsement extends FinanceOperations {
    private ErrorTab errorTab = new ErrorTab();
    /**
     * @author Reda Kazlauskiene
     * Objectives : Multiple OOS Endorse
     * Preconditions:
     * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
     * 1. Create Annual Auto SS Policy with Effective date today
     * 2. Create first Endorsement (Remove one coverage) with date: Today +32 days (with txEffectiveDate -1)
     * 3. Create Second Endorsement(Add one more coverage) with date: first endorsement +212 days (with txEffectiveDate -1)
     * 4. Create OOS Endorsement (Add one more coverage) with date: second endorsement + 3 days (with txEffectiveDate -36)
     * 5. Create Second OOS Endorsement with date: second endorsement + 32 dayst (with txEffectiveDate -279)
     * 5. Roll on Endorsement with available values (not current)
     * 6. Verify Calculations
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    @Parameters({"state"})
    @StateList(states = {Constants.States.WV, Constants.States.KY, Constants.States.NV})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
    public void pas20277_testFinanceEPCalculationMultipleOOSEndorsement(@Optional("NV") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime e1date = today.plusDays(32);
        LocalDateTime e2date = e1date.plusDays(212);
        LocalDateTime oose3date = e2date.plusDays(3);
        LocalDateTime oose4date = oose3date.plusDays(32);

        LocalDateTime jobEndDate  = PolicySummaryPage.getExpirationDate().plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

        jobDate = runEPJobUntil(jobDate, e1date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e1date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-1, "TestData_EndorsementRemoveCoverage");

        jobDate = runEPJobUntil(jobDate, e2date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e2date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-1, "TestData_EndorsementAddCoverage");

        jobDate = runEPJobUntil(jobDate, oose3date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(oose3date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-36, "TestData_EndorsementAddSecondCoverage");

        //180-042-2CL - Endorsement with an effective date more than 30 days prior to current date cannot be bound - rule 200011
        errorTab.overrideAllErrors();
        errorTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        policy.rollOn().perform(false, false);

        jobDate = runEPJobUntil(jobDate, oose4date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(oose4date);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        createEndorsement(-279, "TestData_Endorsement");

        //180-042-2CL - Endorsement with an effective date more than 30 days prior to current date cannot be bound - rule 200011
        errorTab.overrideAllErrors();
        errorTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

        policy.rollOn().perform(false, false);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        policy.rollOn().perform(false, false);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        policy.rollOn().perform(false, false);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonTransactionHistory.click();

        assertThat(new Dollar(PolicySummaryPage.tableTransactionHistory.getRow(1)
                .getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue()))
                .isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));
    }
}