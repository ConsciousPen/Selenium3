package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.template.functional.EndorsementWithPendingAutoSelectCompanion;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestEndorsementWithAutoSelectCompanion extends HomeCaHO3BaseTest {

    private EndorsementWithPendingAutoSelectCompanion template = new EndorsementWithPendingAutoSelectCompanion();

    /**
     * @author Dominykas Razgunas
     * @name Test Endorsement Calculate Premium with pending Auto Select Companion
     * @scenario
     * 1. Create customer
     * 2. Create Auto Select policy with an effective date today+2weeks
     * 3. Initiate Property policy with an effective date today
     * 4. Add the above create Auto companion policy on the Applicant Tab
     * 5. Issue Policy
     * 6. Endorse Policy with effective date today+1week
     * 7. Calculate Premium
     * 8. Assert P&C Page. (The Error was not thrown)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-8786")
    public void pas8786_TestEndorsementRateWithPendingAutoSelectCompanion(@Optional("CA") String state) {

        template.pas8786_TestEndorsementRateWithPendingAutoSelectCompanion(getPolicyType());

    }
}