package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestCaptureLatitudeLongitudeAbstract;

@StateList(states = Constants.States.CA)
public class TestCaptureLatitudeLongitude extends TestCaptureLatitudeLongitudeAbstract {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO3; }
    /**
     * @name test: Capture Latitude and Longitute when address is validated
     * @scenario
     * 1. Create Customer
     * 2. Initiate Policy creation
     * 3. Validate  Latitude and Longitute
     *
     * @details
     */

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-23203")
    public void pas23203_validateIsLatLongPresent(@Optional("CA") String state) {
        pas23203_validateLatLong("90048", "10 Danbrook Drive", true);
    }
}
