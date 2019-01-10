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
import aaa.modules.regression.sales.template.functional.TestHomeGranularityAbstract;

@StateList(states = Constants.States.CA)
public class TestHomeGranularity extends TestHomeGranularityAbstract {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO3; }
    /**
     * @name test: Capture Census Block Group, Latitude and Longitude when address is validated
     * @scenario
     * 1. Create Customer
     * 2. Create Quote up to Premium and Coverages Tab
     * 3. Calculate premium then validate Census Block Group, Latitude and Longitude in the DB
     * 4. Open View Rating Details screen and verify Census Block ID is present and matches what is in the DB
     *
     * @details
     */

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-23235")
    //TODO Add Zip Code (possibly the county), lat/long (currently only showing 3 numbers after decimal)
    public void pas23235_validateCensusBlockGroupAndLatLong(@Optional("CA") String state) {
        pas23203_validateCensusBlockGroupAndLatLong("10 Danbrook Drive", "060290022002");
    }
}
