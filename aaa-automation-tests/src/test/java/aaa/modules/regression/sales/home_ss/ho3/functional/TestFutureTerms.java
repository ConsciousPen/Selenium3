package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.RenewalHelper_HomeSS;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

public class TestFutureTerms extends HomeSSHO3BaseTest {
    /**
     * Handles moving a policy to any desired term.
     * @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9686")
    public void testMoveToAnyTerm(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyDefaultTD());

        RenewalHelper_HomeSS renewalHelper = new RenewalHelper_HomeSS(PolicySummaryPage.getExpirationDate(), 63l, 48l, true);
        mainApp().close();
        renewalHelper.moveToGivenTerm(1);
    }
}
