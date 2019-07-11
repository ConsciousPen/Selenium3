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
     * @name Test CA Driver Assignment Without Gender: Ranking steps 1-4 with Tie Breakers
     * @scenario Test Steps: NOTE: Test will only pass after 11/1/19 policy date
     * 1. Create a quote with 7 drivers:
     *      1. Female, Single, 4 tyde, Xdsrp - primary driver tie breaker
     *      2. Male, Single, 4 tyde, Xdsrp - (not) primary driver tie breaker
     *      3. Female, Married, 3 tyde - tyde tie breaker
     *      4. Male, Married, 4 tyde - tyde tie breaker
     *      5. Male, (Married), 15 tyde, 6 dsrp - dsrp tie breaker
     *      6. Female, (Married), 15 tyde, 4 dsrp - dsrp tie breaker
     *      7. Female, Single, 5 tyde, 0 dsrp  - tyde tie breaker
     *      **tyde = total years driving experience **dsrp = driver safety record points
     * 2. Add 7 identical vehicles (this will make it so the vehicle details do no affect assignment)
     * 3. Calculate premium and navigate to the Assignment tab.
     * 4. Verify that the System Rated drivers are all assigned as mentioned above
     * @details Clean Path. Expected Result: System Rated Drivers are listed in the above order as Gender is NOT an assignment factor anymore
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
    public void pas29418_DriverAssignmentRanking1_4(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas29418_DriverAssignmentRanking("TestData_DriverTab_Assignment_1_4");
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-29418: Assignment Ranking Change - Select Only
     * @name Test CA Driver Assignment Without Gender: Ranking steps 5-9, no tie breakers
     * @scenario Test Steps: NOTE: Test will only pass after 11/1/19 policy date
     * 1. Create a quote with 7 drivers:
     *      1. Female, Single, 4 tyde, Xdsrp - primary driver
     *      2. Male, Single, 6 tyde, 0 dsrp
     *      3. Female, Married, 7 tyde, 0 dsrp
     *      4. Male, (Widowed), 15 tyde, 2 dsrp
     *      5. Female, Single, 12 tyde, 0 dsrp
     *      6. Female, Married, 11 tyde, 0dsrp - tyde tie breaker
     *      7. Male, (Widowed), 12 tyde, 0dsrp - tyde tie breaker
     *      **tyde = total years driving experience **dsrp = driver safety record points
     * 2. Add 7 identical vehicles (this will make it so the vehicle details do no affect assignment)
     * 3. Calculate premium and navigate to the Assignment tab.
     * 4. Verify that the System Rated drivers are all assigned as mentioned above
     * @details Clean Path. Expected Result: System Rated Drivers are listed in the above order as Gender is NOT an assignment factor anymore
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
    public void pas29418_DriverAssignmentRanking5_9(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas29418_DriverAssignmentRanking("TestData_DriverTab_Assignment_5_9");
    }

}
