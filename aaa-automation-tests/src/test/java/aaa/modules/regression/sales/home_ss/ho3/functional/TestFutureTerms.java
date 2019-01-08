package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.renewal.RenewalHelper_Home;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.renewal.RenewalHelper_Profile;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestFutureTerms extends HomeSSHO3BaseTest {
    final String STATE = "AZ"; // <-- Control here to ensure test and helper class are working with same state data.

    /**
     * Handles moving a policy to any desired term.
     * @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9686")
    public void testMoveToAnyTerm(@Optional(STATE) String state) {
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyDefaultTD());

        RenewalHelper_Home renewalHelper = new RenewalHelper_Home(STATE,  true);
        mainApp().close();
        renewalHelper.moveToGivenTerm(1, RenewalHelper_Profile.Membership, false);

        mainApp().open();
        SearchPage.openPolicy(renewalHelper.getCreatedPolicyNumber());
    }
}