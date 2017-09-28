package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

public class TestQuoteUpdateVehicleUI extends AutoCaSelectBaseTest {
    /**
     * PAS-535
     * @author Lev Kazarnovskiy
     * @name UI update (Vehicle Info).
     * @scenario 1. Create customer and auto CA selects policy with 2 Vehicles
     * 1. Create CA Select quote
     * 2. Add 2 vehicles (VIN doesn't match)
     * 3. Manually fill vehicle detail fields
     * 4. Enter MSRP(value) and verify if symbol field is not displayed for both vehicles
     * 5. Calculate premium and bind policy
     * 6. Retrieve policy and open it in Inquiry mode
     * 7. Verify for each vehicle if symbol field is not displayed on Vehicle page
     * 8. Generate renewal image and verify vehicle page in Inquiry mode if symbol field is not displayed for both vehicles
     * 9. Create new renewal version and add 3rd vehicle (VIN doesn't match)
     * 10. Manually fill vehicle detail fields
     * 11. Enter MSRP(value) and verify if symbol field is not displayed
     * 12. Calculate premium and save renewal image
     * @details
     */
    @Test(groups = {Groups.MEDIUM, Groups.REGRESSION})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void TestCase1() {
        InquiryAssetList inquiryAssetList = new InquiryAssetList(new VehicleTab().getAssetList().getLocator(), AutoCaMetaData.VehicleTab.class);
        TestData td = getPolicyTD();
        TestData vehicleData = getTestSpecificTD("TestCase_1").resolveLinks();
        TestData renewalVehicleInfo = getTestSpecificTD("TestCase_1_Renewal").resolveLinks();
        VehicleTab vehicleTab = new VehicleTab();

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(td.adjust(vehicleData), VehicleTab.class, true);

        // verify if symbol field is not displayed for both vehicle
        vehicleTab.verifyFieldIsNotDisplayed("Comp/Coll Symbol");

        vehicleTab.submitTab();
        policy.getDefaultView().fillFromTo(td.adjust(vehicleData), AssignmentTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policy.policyInquiry().start();
        NavigationPage.toViewTab("Vehicle");
        //verify comp/coll symbols
        inquiryAssetList.getAsset(AutoCaMetaData.VehicleTab.COBP_COLL_SYMBOL.getLabel(), StaticElement.class).
                verify.present(false);
        Tab.buttonCancel.click();

        policy.renew().start();
        Tab.buttonSaveAndExit.click();
        PolicySummaryPage.buttonRenewals.click();
        policy.policyInquiry().start();
        NavigationPage.toViewTab("Vehicle");
        // verify coll/comp symbols
        inquiryAssetList.getAsset(AutoCaMetaData.VehicleTab.COBP_COLL_SYMBOL.getLabel(), StaticElement.class).
                verify.present(false);
        Tab.buttonCancel.click();

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
//        Tab.buttonOk.click();
//        PolicySummaryPage.dialogConfirmation.confirm();
        NavigationPage.toViewTab("Vehicle");
        VehicleTab.buttonAddVehicle.click();
        vehicleTab.fillTab(renewalVehicleInfo);
        //verify coll/comp symbols
        vehicleTab.verifyFieldIsNotDisplayed("Comp/Coll Symbol");

        vehicleTab.submitTab();
        policy.getDefaultView().fillFromTo(td.adjust(renewalVehicleInfo), AssignmentTab.class, PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.buttonSaveAndExit.click();
    }
}