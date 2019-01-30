package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.constants.HomeGranularityConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestHomeGranularityAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestHomeGranularity extends TestHomeGranularityAbstract {

    @Override
    protected PolicyType getPolicyType(){return PolicyType.HOME_SS_HO3;}

    private String zipCode = HomeGranularityConstants.EADS_MOCK_ZIPCODE_AZ;
    private String address = HomeGranularityConstants.EADS_MOCK_ADDRESS_AZ;
    private String censusBlock = HomeGranularityConstants.EADS_MOCK_CENSUS_BLOCK_AZ;
    private String latitude = HomeGranularityConstants.EADS_MOCK_LATITUDE_AZ;
    private String longitude = HomeGranularityConstants.EADS_MOCK_LONGITUDE_AZ;

    /**
     * @name test: When lat/long is not captured from AVS (e.g. after validating address)
     * ThenCapture Census Block Group, Latitude and Longitude from EADS (e.g. after Calculating Premium)
     * @scenario
     * 1. Create Quote up to Applicant Tab and use Dwelling Address that is not in AVS mock sheet and Save
     * 2. Verify lat/long is null in the db
     * 3. Continue Quote up to Premium & Coverages Tab and Calculate Premium
     * 4. Verify lat/long and census block are in the db
     *
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-24030")
    public void pas24030_validateCensusBlockGroupAndLatLongFromEADS(@Optional("AZ") String state) {
        validateCensusBlockGroupAndLatLongFromEADS(zipCode, address, censusBlock, latitude, longitude);
    }

}
