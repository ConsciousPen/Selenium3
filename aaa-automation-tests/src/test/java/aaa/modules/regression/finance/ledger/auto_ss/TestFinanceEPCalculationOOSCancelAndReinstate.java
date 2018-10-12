package aaa.modules.regression.finance.ledger.auto_ss;

import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

public class TestFinanceEPCalculationOOSCancelAndReinstate extends FinanceOperations {
    /**
     * @author Objectives :
     * Preconditions:
     * 1. Create Annual Policy
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
    public void pas20277_testFinanceEPCalculationOOSCancelAndReinstate(@Optional("AZ") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime txEffectiveDate = today.plusMonths(1);
        LocalDateTime eDate = today.plusDays(123);
        LocalDateTime cDate = eDate.plusDays(35);
        LocalDateTime rDate = cDate.plusMonths(1);

        LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

        jobDate = runEPJobUntil(jobDate, eDate);
        TimeSetterUtil.getInstance().nextPhase(eDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        createEndorsement(-1, "TestData_EndorsementAPRemoveCoverage");

        jobDate = runEPJobUntil(jobDate, cDate);
        TimeSetterUtil.getInstance().nextPhase(cDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        cancelPolicy(txEffectiveDate);

        jobDate = runEPJobUntil(jobDate, rDate);
        TimeSetterUtil.getInstance().nextPhase(rDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        reinstatePolicy(txEffectiveDate);

        runEPJobUntil(jobDate, jobEndDate);
    }
}