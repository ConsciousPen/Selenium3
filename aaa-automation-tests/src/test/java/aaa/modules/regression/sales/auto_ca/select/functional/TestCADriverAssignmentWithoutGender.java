package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestCADriverAssignmentWithoutGenderTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestCADriverAssignmentWithoutGender extends TestCADriverAssignmentWithoutGenderTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-29418: Assignment Ranking Change - Select Only
     * @name Test CA Driver Assignment Without Gender
     * @scenario Test Steps:
     * 1. Create a Policy with 3 drivers
     * 2.
     * 3.
     * 4.
     * 5.
     * 6.
     * 7.
     * @details Clean Path. Expected Result:
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
    public void pas29418_DriverAssignmentRanking(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas29418_DriverAssignmentRanking();
    }


}
