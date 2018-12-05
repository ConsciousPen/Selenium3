package aaa.modules.regression.finance.ledger.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationOOSEndorsement extends FinanceOperations {
    private ErrorTab errorTab = new ErrorTab();
    /**
     * @author Reda Kazlauskiene
     * Objectives : OOS Endorse
     * Preconditions:
     * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
     * 1. Create Annual Auto SS Policy with Effective date today
     * 2. Create first Endorsement (Remove one coverage, Increase other coverage) with date: Today +62 days (with txEffectiveDate -1)
     * 3. Create Second Endorsement(Add one more coverage) with date: first endorsement +61 days (with txEffectiveDate -1)
     * 4. Create OOS Endorsement (Add one more coverage) with date: second endorsement + 64 dayst (with txEffectiveDate -95)
     * 5. Roll on Endorsement with available values (not current)
     * 6. Verify Calculations
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    @Parameters({"state"})
	@StateList(states = {Constants.States.WV, Constants.States.KY, Constants.States.AZ, Constants.States.NJ})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
	public void pas20277_testFinanceEPCalculationOOSEndorsement(@Optional("AZ") String state) {

       String policyNumber = openAppAndCreatePolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime e1date = today.plusDays(62);
        LocalDateTime e2date = e1date.plusDays(61);
        LocalDateTime e3date = e2date.plusDays(64);

        LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime jobEndDate  = expirationDate.plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

        jobDate = runEPJobUntil(jobDate, e1date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e1date);
        searchForPolicy(policyNumber);
        createEndorsement(-1, "TestData_EndorsementAPRemoveCoverage");

        jobDate = runEPJobUntil(jobDate, e2date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e2date);
        searchForPolicy(policyNumber);
        createEndorsement(-1, "TestData_EndorsementAddCoverage");

        jobDate = runEPJobUntil(jobDate, e3date, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        TimeSetterUtil.getInstance().nextPhase(e3date);
        searchForPolicy(policyNumber);
        createEndorsement(-95, "TestData_EndorsementAddSecondCoverage");

        //180-042-2CL - Endorsement with an effective date more than 30 days prior to current date cannot be bound - rule 200011
        errorTab.overrideAllErrors();
        errorTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

        policy.rollOn().perform(false, false);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        runEPJobUntil(jobDate, jobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        searchForPolicy(policyNumber);
        PolicySummaryPage.buttonTransactionHistory.click();

        assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
                .isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

        List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.ENDORSE, TxType.ENDORSE, TxType.OOS_ENDORSE, TxType.ROLL_ON);
        validateEPCalculations(policyNumber, txTypes, today, expirationDate);
    }
}