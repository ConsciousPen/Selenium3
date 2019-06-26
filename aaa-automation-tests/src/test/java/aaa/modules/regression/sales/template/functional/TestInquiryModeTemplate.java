package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.CustomSoftAssertions;

public abstract class TestInquiryModeTemplate extends PolicyBaseTest {

    /**
     * @author Chathura Deniyawatta
     * @name Test Add Insured/Add Driver/Add Vehicle buttons are not available in Inquiry mode
     * @scenario
     * 1. Create a Customer
     * 2. Create new Policy
     * 3. Initiate an inquiry
     * 4. Verify Add Insured/Add Driver/Add Vehicle buttons are not available
     * @details
     */
    protected void testInquiryMode(){
        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        PolicySummaryPage.getExpirationDate();

        policy.policyInquiry().start();
        verifyAddButtonsNotAvailable();
    }

    private void verifyAddButtonsNotAvailable(){
        CustomSoftAssertions.assertSoftly(softly -> {
            GeneralTab generalTab = new GeneralTab();
            softly.assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED).isPresent()).
                    isFalse();

            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            DriverTab driverTab = new DriverTab();
            softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.ADD_DRIVER).isPresent()).
                    isFalse();

            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
            VehicleTab vehicleTab = new VehicleTab();
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ADD_VEHICLE).isPresent()).
                    isFalse();
        });
    }
}
