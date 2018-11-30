package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

@StateList(statesExcept = Constants.States.CA)
public class TestEnrollUsageBasedEndorsementCheck extends AutoSSBaseTest {

    /**
     * * @author Igor Garkusha, Josh Carpenter
     * @name Enroll in Usage Based Insurance question Incorrectly defaults to "No" when prior vehicle did not have UBI
     * @scenario
     * 1. Create Auto SS policy where vehicle has NOT enrolled in UBI (Enroll in Usage Based Insurance = No)
     * 2. Initiate endorsement
     * 3. Navigate to Vehicles tab
     * 4. User clicks the 'Replace' option on a vehicle having Enroll in Usage Based Insurance = 'No' and click 'Ok' on confirmation popup
     * 5. User has supplied Year(1996+), Make and Model for PPA or supplied a 17 digit VIN for a 1996+ PPA for the new vehicle
     * 6. Check that Enroll in Usage Based Insurance? is defaulted to 'No' for replacement vehicle
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4247, PPS-346")
    public void pas4247_testVehicleWithoutUBI(@Optional("") String state) {

        //Create customer & policy with UBI coverage = 'No' for vehicle
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        //Initiate endorsement, replace vehicle, and confirm UBI value
        replaceVehicleConfirmUBI("No");

    }

    /**
     * * @author Igor Garkusha, Josh Carpenter
     * @name Enroll in Usage Based Insurance question Incorrectly defaults to "Yes" when prior vehicle did have UBI
     * @scenario
     * 1. Create Auto SS policy where vehicle has enrolled in UBI (Enroll in Usage Based Insurance = Yes)
     * 2. Initiate endorsement
     * 3. Navigate to Vehicles tab
     * 4. User clicks the 'Replace' option on a vehicle having Enroll in Usage Based Insurance = 'Yes' and click 'Ok' on confirmation popup
     * 5. User has supplied Year(1996+), Make and Model for PPA or supplied a 17 digit VIN for a 1996+ PPA for the new vehicle
     * 6. Check that Enroll in Usage Based Insurance? is defaulted to 'Yes' for replacement vehicle
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4247, PPS-346")
    public void pas4247_testVehicleWithUBI(@Optional("") String state) {

        TestData td = getPolicyDefaultTD()
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), ENROLL_IN_USAGE_BASED_INSURANCE.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), GET_VEHICLE_DETAILS.getLabel()), "click")
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), VEHICLE_ELIGIBILITY_RESPONCE.getLabel()), "Vehicle Eligible")
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), GRANT_PATRITIPATION_DISCOUNT.getLabel()), "click")
                .adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
                        AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel()), "Physically Signed");

        //Create customer & policy with UBI coverage = 'Yes' for vehicle
        mainApp().open();
        createCustomerIndividual();
        createPolicy(td);

        //Initiate endorsement, replace vehicle, and confirm UBI value
        replaceVehicleConfirmUBI("Yes");

    }

    private void replaceVehicleConfirmUBI(String ubiValue) {
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        VehicleTab.tableVehicleList.getRow(1).getCell(5).controls.links.get(2).click();
        Page.dialogConfirmation.confirm();
        new VehicleTab().getAssetList().getAsset(VIN.getLabel(), TextBox.class).setValue("1G1JC124627237595");
        assertThat(new VehicleTab().getAssetList().getAsset(ENROLL_IN_USAGE_BASED_INSURANCE.getLabel(), RadioGroup.class)).hasValue(ubiValue);
    }
}
