package aaa.modules.regression.service.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelperCA;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static aaa.main.metadata.policy.AutoCaMetaData.VehicleTab.*;
import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;


public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelperCA {

    private aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab vehicleTab = new VehicleTab();

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

