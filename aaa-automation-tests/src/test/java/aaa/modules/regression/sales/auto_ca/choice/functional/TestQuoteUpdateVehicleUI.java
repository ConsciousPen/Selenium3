package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestQuoteUpdateVehicleUI extends AutoCaChoiceBaseTest {
    /**
     * PAS-535
     *
     * @author Lev Kazarnovskiy
     * @name UI update (Vehicle Info).
     * @scenario 3. Create customer and auto CA choice policy with 2 Vehicles with VIM match
     * 1. Create CA Select quote
     * 2. Add 2 vehicles (VIN match)
     * 3. Verify if symbol field is not displayed for both vehicles
     * 4. Calculate premium and bind policy
     * 5. Retrieve policy and open it in Inquiry mode
     * 6. Verify for each vehicle if symbol field is not displayed on Vehicle page
     * @details
     */
    @Test(groups = {Groups.MEDIUM, Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-535")
    public void TestCase2() {
        InquiryAssetList inquiryAssetList = new InquiryAssetList(new VehicleTab().getAssetList().getLocator(), AutoCaMetaData.VehicleTab.class);
        TestData td = getPolicyTD().resolveLinks();
        TestData vehicleData = getTestSpecificTD("TestCase_3").resolveLinks();
        VehicleTab vehicleTab = new VehicleTab();

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(td.adjust(vehicleData), VehicleTab.class, true);
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.COMP_COLL_SYMBOL)).isPresent(false);
        vehicleTab.submitTab();
        policy.getDefaultView().fillFromTo(td.adjust(vehicleData), AssignmentTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policy.policyInquiry().start();
        NavigationPage.toViewTab("Vehicle");
        assertThat(inquiryAssetList.getAsset(AutoCaMetaData.VehicleTab.COMP_COLL_SYMBOL)).isPresent(false); //VERIFY FIELDS
    }
}