package aaa.modules.regression.sales.home_ca.ho6.functional;

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
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO6; }
    /**
     * @name test: Capture Census Block Group, Latitude and Longitude when address is validated
     * @scenario
     * 1. Create Customer
     * 2. Initiate Policy creation
     * 3. Validate  Census Block Group, Latitude and Longitude
     *
     * @details
     */

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-23235")
    public void pas23235_validateCensusBlockGroupAndLatLong(@Optional("CA") String state) {
        pas23203_validateCensusBlockGroupAndLatLong("13.637", "-102.176", "060290022002");
    }
}

