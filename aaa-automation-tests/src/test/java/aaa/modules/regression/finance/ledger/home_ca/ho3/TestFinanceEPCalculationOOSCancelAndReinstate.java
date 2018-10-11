package aaa.modules.regression.finance.ledger.home_ca.ho3;

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
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

public class TestFinanceEPCalculationOOSCancelAndReinstate extends FinanceOperations {
    /**
     * @author Objectives :
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
    public void pas20277_testFinanceEPCalculationOOSCancelAndReinstate(@Optional("CA") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime txEffectiveDate = today.plusMonths(1);
        LocalDateTime eDate = today.plusDays(123);
        LocalDateTime cDate = eDate.plusDays(35);
        LocalDateTime rDate = cDate.plusDays(28);

        LocalDateTime jobEndDate = PolicySummaryPage.getExpirationDate().plusMonths(1);
        LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

        createEndorsement(1, "TestData_EndorsementRP");

        jobDate = runEPJobUntil(jobDate, eDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        createEndorsement(1, "TestData_EndorsementRP");

        jobDate = runEPJobUntil(jobDate, cDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
                .adjust("CancelActionTab|Cancellation effective date", txEffectiveDate.format(DateTimeUtils.MM_DD_YYYY)));

        jobDate = runEPJobUntil(jobDate, rDate);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData")
                .adjust("ReinstatementActionTab|Reinstate date", txEffectiveDate.format(DateTimeUtils.MM_DD_YYYY)));

        runEPJobUntil(jobDate, jobEndDate);
    }
}