package aaa.modules.regression.sales.home_ca.ho4.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestHomeGranularityAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestHomeGranularity extends TestHomeGranularityAbstract {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO4; }

    private TestData getTDAddressChange() {
        return getTestSpecificTD("TestData_ChangeAddress");
    }

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
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-23235")
    public void pas23235_validateCensusBlockGroupAndLatLong(@Optional("CA") String state) {
        validateCensusBlockGroupAndLatLong();
    }

    /**
     * @name test: When lat/long is not captured from AVS (e.g. after validating address)
     * ThenCapture Census Block Group, Latitude and Longitude from EADS (e.g. after Calculating Premium)
     * @scenario
     * 1. Create policy
     * 2. Verify lat/long is null in the db
     * 3. Continue Quote up to Premium & Coverages Tab and Calculate Premium
     * 4. Verify lat/long and census block are in the db
     *
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-23927")
    public void pas23927_validateCensusBlockGroupAndLatLongFromEADS(@Optional("CA") String state) {
        validateCensusBlockGroupAndLatLongFromEADS();
    }

    /**
     * @name test: ReCapture Latitude and Longitude (after validating address)Census Block Group(e.g. after Calculating Premium)
     * @scenario
     * 1. Create policy
     * 2. Initiate Renewal. Change address on Applicant Tab
     * 3. Validate address and continue up to Premium & Coverages Tab and Calculate Premium
     * 4. Verify lat/long and census block are refreshed in the db
     *
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-23218")
    public void pas23218_riskAddressChangeDuringRenewal(@Optional("CA") String state) {
        riskAddressChangeDuringRenewal(getTDAddressChange());
    }
}
