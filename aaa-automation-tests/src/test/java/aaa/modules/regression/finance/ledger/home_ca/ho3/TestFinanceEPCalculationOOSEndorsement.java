package aaa.modules.regression.finance.ledger.home_ca.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

public class TestFinanceEPCalculationOOSEndorsement extends FinanceOperations {
    /**
     * @author
     * Objectives : 
     * Preconditions:
     * 1. Create Annual Policy

     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-20277")
    public void pas20277_testFinanceEPCalculationOOSEndorsement(@Optional("CA") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime e1date = today.plusDays(62);
        LocalDateTime e2date = e1date.plusDays(61);
        LocalDateTime e3date = e2date.plusDays(124);

        LocalDateTime jobEndDate  = PolicySummaryPage.getExpirationDate().plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

        jobDate = runEPJobUntil(jobDate, e1date);

        //endorse();

        jobDate = runEPJobUntil(jobDate, e2date);

        //endorse();

        jobDate = runEPJobUntil(jobDate, e3date);

//        endorse();
//        rollOn();

        runEPJobUntil(jobDate, jobEndDate);
    }
}