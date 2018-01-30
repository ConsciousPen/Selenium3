package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.ENROLL_IN_USAGE_BASED_INSURANCE;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.VIN;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

public class TestEnrollUsageBasedEndorsementCheck extends AutoSSBaseTest {
    private final Tab vehicleTab = new VehicleTab();

    /**
     * * @author Igor Garkusha
     * @name Enroll in Usage Based Insurance question Incorrectly defaults to "No" for a Replace Vehicle Transaction in PAS
     * @scenario 1. Create a policy has one or more vehicles enrolled in UBI (Enroll in Usage Based Insurance = Yes)
     * 2. User accesses the policy in 'Endorsement' mode
     * 3. There is at least one vehicle having  Enroll in Usage Based Insurance = Yes at the time of endorsement
     * 4. User clicks the 'Replace' option on a vehicle having  Enroll in Usage Based Insurance = Yes and Safety Score = “Unable to Score”
     * and clicks 'OK' to confirm the replace action -> vehicle has been replaced
     * 5. User has supplied Year(1996+), Make and Model for PPA or supplied a 17 digit VIN for a 1996+ PPA for the new vehicle
     * 6. Check that Enroll in Usage Based Insurance? is Yes
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3663")
    public void pas4247_testEnrollInUsageBasedInsuranceOnEndorsementAction(@Optional("") String state) {
        mainApp().open();

        createCustomerIndividual();
        createPolicy();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        RadioGroup enroll = vehicleTab.getAssetList().getAsset(ENROLL_IN_USAGE_BASED_INSURANCE.getLabel(), RadioGroup.class);
        enroll.setValue("Yes");
        VehicleTab.tableVehicleList.getRow(1).getCell(5).controls.links.get(2).click();
        Page.dialogConfirmation.confirm();
                vehicleTab.getAssetList().getAsset(VIN.getLabel(), TextBox.class).setValue("91N1K36Y991234567");
        assertThat(enroll.getValue()).isEqualTo("Yes");
    }
}
