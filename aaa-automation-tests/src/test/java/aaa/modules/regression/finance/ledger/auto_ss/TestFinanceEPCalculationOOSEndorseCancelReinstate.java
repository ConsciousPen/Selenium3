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

public class TestFinanceEPCalculationOOSEndorseCancelReinstate extends FinanceOperations {
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

        runEPJobUntil(jobDate, jobEndDate);
    }
}