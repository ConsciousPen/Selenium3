package aaa.modules.regression.sales.home_ca.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.EndorsementWithPendingAutoSelectCompanionTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestEndorsementWithAutoSelectCompanion extends EndorsementWithPendingAutoSelectCompanionTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO4;
    }

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
     * 7. Navigate to Applicant Tab
     * 8. Navigate to Reports Tab
     * 9. Error is not thrown and you are able to proceed
     * 10. Navigate to P&C
     * 11. Calculate Premium
     * 12. Assert P&C Page. (The Error was not thrown)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-8786")
    public void pas8786_TestEndorsementRateWithPendingAutoSelectCompanion(@Optional("CA") String state) {

        pas8786_TestEndorsementRateWithPendingAutoSelectCompanion();

    }
}