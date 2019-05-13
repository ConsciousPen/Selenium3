package aaa.modules.regression.service.auto_ca.choice.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelperCA;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;


public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelperCA {

	private VehicleTab vehicleTab = new VehicleTab();

	@Override
	protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }


    /**
     * @author Chaitanya Boyapati
     * @name Add Vehicle - check Anti-Theft Drop Down Values
     * @scenario 1. Create policy.
     * 2. Create endorsement.
     * 3. Add new vehicle.
     * 4. Hit MetaData service, check the values there.
     * 5. Validate the Vehicle type cd attribute Names as "antiTheft", "Distance Oneway", "Odometer Reading", "Declared Annual Miles" and Usage"
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @StateList(states = {Constants.States.CA})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-25263"})
    public void pas25263_addVehicleMetadataCheck(@Optional("CA") String state) {

        assertSoftly(softly -> pas25263_addVehicleMetadataCheckBody(softly));
    }

}

