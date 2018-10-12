package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.auto_ca.select.TestPolicyCreationBig;
import aaa.modules.regression.sales.template.functional.TestAddVehicleAfterRenewalTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestAddVehicleAfterRenewal extends TestAddVehicleAfterRenewalTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Josh Carpenter
     * @name Test there is no error when adding a vehicle to a policy during renewal for CA Select
     * @scenario
     * 1.  Create Customer
     * 2.  Create CA Select policy with 3 vehicles
     * 3.  Advance time to R-35
     * 4.  Create renewal image and propose
     * 5.  Navigate to billing tab and pay total amount due
     * 6.  Initiate endorsement with transaction eff. date as renewal eff. date
     * 7.  Navigate to Vehicle tab and remove 2nd vehicle
     * 8.  Bind the endorsement
     * 9.  Advance time to R and run policyStatusUpdateJob
     * 10. Validate renewal is active
     * 11. Initiate another endorsement that is one week into the renewal term
     * 12. Navigate to vehicle tab and add a vehicle
     * 13. Navigate to Assignment tab and assign the new vehicle to a driver
     * 14. Navigate to P & C tab and calculate premium
     * 15. Validate no error message is displayed
     * 16. Bind policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-20217")
    public void pas20217_testAddVehicleAfterRenewal(@Optional("CA") String state) {
        List<TestData> tdVehicles = testDataManager.getDefault(TestPolicyCreationBig.class).getTestData("TestData").getTestDataList(AutoCaMetaData.VehicleTab.class.getSimpleName());
        testAddVehicleAfterRenewal(tdVehicles);

    }

}
