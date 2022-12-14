package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.constants.HomeGranularityConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
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

    @Override
    protected Tab getApplicantTab(){return new ApplicantTab();}

    @Override
    protected Tab getReportsTab(){return new ReportsTab();}

    @Override
    protected Tab getPremiumAndCoveragesQuoteTab(){return new PremiumsAndCoveragesQuoteTab();}

    private String zipCode = HomeGranularityConstants.EADS_MOCK_ZIPCODE_AZ;
    private String address = HomeGranularityConstants.EADS_MOCK_ADDRESS_AZ;
    private String censusBlock = HomeGranularityConstants.EADS_MOCK_CENSUS_BLOCK_AZ;
    private String latitude = HomeGranularityConstants.EADS_MOCK_LATITUDE_AZ;
    private String longitude = HomeGranularityConstants.EADS_MOCK_LONGITUDE_AZ;
    private String avsMockCensusBlock = HomeGranularityConstants.MOCK_CENSUS_BLOCK_AZ;
    private String avsMockLatitude = HomeGranularityConstants.MOCK_LATITUDE_AZ;
    private String avsMockLongitude = HomeGranularityConstants.MOCK_LONGITUDE_AZ;

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
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-24138")
    public void pas24138_validateCensusBlockGroupAndLatLong(@Optional("AZ") String state) {
        validateCensusBlockGroupAndLatLong(avsMockCensusBlock, avsMockLatitude, avsMockLongitude);
    }

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
