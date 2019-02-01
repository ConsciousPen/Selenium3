package aaa.modules.regression.sales.home_ca.ho6.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.constants.HomeGranularityConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.modules.regression.sales.template.functional.TestHomeGranularityAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestHomeGranularity extends TestHomeGranularityAbstract {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO6; }

    @Override
    protected Tab getApplicantTab(){return new ApplicantTab();}

    @Override
    protected Tab getReportsTab(){return new ReportsTab();}

    @Override
    protected Tab getPremiumAndCoveragesQuoteTab(){return new PremiumsAndCoveragesQuoteTab();}

    private String zipCode = HomeGranularityConstants.EADS_MOCK_ZIPCODE_CA;
    private String address = HomeGranularityConstants.EADS_MOCK_ADDRESS_CA;
    private String censusBlock = HomeGranularityConstants.EADS_MOCK_CENSUS_BLOCK_CA;
    private String latitude = HomeGranularityConstants.EADS_MOCK_LATITUDE_CA;
    private String longitude = HomeGranularityConstants.EADS_MOCK_LONGITUDE_CA;

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
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-23235")
    public void pas23235_validateCensusBlockGroupAndLatLong(@Optional("CA") String state) {
        validateCensusBlockGroupAndLatLong();
    }

    /**
     * @name test: When lat/long is not captured from AVS (e.g. after validating address)
     * ThenCapture Census Block Group, Latitude and Longitude from EADS (e.g. after Calculating Premium)
     * @scenario
     * 1. Create Quote up to Applicant Tab and use Dwelling Address that is not in wire mock and Save
     * 2. Verify lat/long is null in the db
     * 3. Continue Quote up to Premium & Coverages Tab and Calculate Premium
     * 4. Verify lat/long and census block are in the db
     *
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-23927")
    public void pas23927_validateCensusBlockGroupAndLatLongFromEADS(@Optional("CA") String state) {
        validateCensusBlockGroupAndLatLongFromEADS(zipCode, address, censusBlock, latitude, longitude);
    }
}

