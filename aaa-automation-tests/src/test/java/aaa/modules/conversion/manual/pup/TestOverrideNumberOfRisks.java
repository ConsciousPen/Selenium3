package aaa.modules.conversion.manual.pup;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.modules.conversion.manual.ConvPUPBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;

public class TestOverrideNumberOfRisks extends ConvPUPBaseTest {

    private UnderlyingRisksPropertyTab underlyingRisksPropertyTab = policy.getDefaultView().getTab(UnderlyingRisksPropertyTab.class);
    private UnderlyingRisksAutoTab underlyingRisksAutoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
    private UnderlyingRisksOtherVehiclesTab underlyingRisksOtherVehiclesTab = policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-7003")
    public void pas7003_TestOverrideNumberOfRisksNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        TestData tdPUP = getPolicyDefaultTD()


        // TESTING ONLY **********************
//                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
//                        PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel() + "[0]",
//                        PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(),
//                        PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel()),
//                        "NJH3926232091")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.AGENCY.getLabel()), "AAA South Jersey - 500017174")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.AGENCY_LOCATION.getLabel()), "Logan - 500017238 - Logan Twp")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.AGENT.getLabel()))
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.LEAD_SOURCE.getLabel()), "index=5");

        //SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, "700032262");

        // **************************************

        TestData tdPropertyTabAdditionalResidences = getTestSpecificTD("TestData_AdditionalResidences");
        TestData tdPropertyTabRentalProperties = getTestSpecificTD("TestData_RentalResidences");
        TestData tdAutoTabDrivers = getTestSpecificTD("TestData_Drivers");
        TestData tdAutoTabAutomobiles = getTestSpecificTD("TestData_Automobiles");
        TestData tdAutoTabAntiques = getTestSpecificTD("TestData_Antiques");
        TestData tdAutoTabMotorcycles = getTestSpecificTD("TestData_Motorcycles");
        TestData tdAutoTabMotorHomes = getTestSpecificTD("TestData_MotorHomes");
        TestData tdOtherVehiclesTabWatercraft = getTestSpecificTD("TestData_Watercraft");
        TestData tdOtherVehiclesTabATV = getTestSpecificTD("TestData_ATV");
        TestData tdOtherVehiclesTabSnowmobile = getTestSpecificTD("TestData_Snowmobile");
        TestData tdOtherVehiclesTabGolfCart = getTestSpecificTD("TestData_GolfCart");

        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksPropertyTab.class);

        // Create 6 Additional Residences
        tdPUP.adjust(UnderlyingRisksPropertyTab.class.getSimpleName(), tdPropertyTabAdditionalResidences);
        addAdditionalItems(underlyingRisksPropertyTab, 6, underlyingRisksPropertyTab.getAdditionalResidenciesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD), tdPUP);

        // Create 7 Rental Residences
        tdPUP.adjust(UnderlyingRisksPropertyTab.class.getSimpleName(), tdPropertyTabRentalProperties);
        addAdditionalItems(underlyingRisksPropertyTab, 6, underlyingRisksPropertyTab.getAdditionalResidenciesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD), tdPUP);

        underlyingRisksPropertyTab.submitTab();

        // Create 9 Automobiles
        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()), tdAutoTabDrivers);

        underlyingRisksAutoTab.fillTab(tdPUP);

        tdPUP.mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()))
                .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()), tdAutoTabAutomobiles);

        addAdditionalItems(underlyingRisksAutoTab, 8, underlyingRisksAutoTab.getAutomobilesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD), tdPUP);

        // Create 5 antique automobiles
        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()), tdAutoTabAntiques);

        addAdditionalItems(underlyingRisksAutoTab, 5, underlyingRisksAutoTab.getAutomobilesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD), tdPUP);

        // Add 5 Motorcycles
        tdPUP.mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()))
                .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTORCYCLES.getLabel()), tdAutoTabMotorcycles);
        underlyingRisksAutoTab.getMotorcyclesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.ADD_MOTORCYCLE).setValue("Yes");
        underlyingRisksAutoTab.fillTab(tdPUP);
        addAdditionalItems(underlyingRisksAutoTab, 4, underlyingRisksAutoTab.getMotorcyclesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.ADD), tdPUP);

        // Add 5 Motor Homes
        tdPUP.mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTORCYCLES.getLabel()))
                .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTOR_HOMES.getLabel()), tdAutoTabMotorHomes);
        underlyingRisksAutoTab.getMotorHomesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.ADD_MOTORE_HOME).setValue("Yes");
        underlyingRisksAutoTab.fillTab(tdPUP);
        addAdditionalItems(underlyingRisksAutoTab, 4, underlyingRisksAutoTab.getMotorHomesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.ADD), tdPUP);

        underlyingRisksAutoTab.submitTab();

        // Add 6 Watercraft
        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksOtherVehiclesTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.WATERCRAFT.getLabel()), tdOtherVehiclesTabWatercraft);
        underlyingRisksOtherVehiclesTab.getWatercraftAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT).setValue("Yes");
        underlyingRisksOtherVehiclesTab.fillTab(tdPUP);
        addAdditionalItems(underlyingRisksOtherVehiclesTab, 5, underlyingRisksOtherVehiclesTab.getWatercraftAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD), tdPUP);

        // Add 5 ATV's
        tdPUP.mask(TestData.makeKeyPath(TestData.makeKeyPath(UnderlyingRisksOtherVehiclesTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.WATERCRAFT.getLabel())))
                .adjust(TestData.makeKeyPath(UnderlyingRisksOtherVehiclesTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RECREATIONAL_VEHICLE.getLabel()), tdOtherVehiclesTabATV);
        underlyingRisksOtherVehiclesTab.getRecreationalVehicleAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD_RECREATIONAL_VEHICLE).setValue("Yes");
        underlyingRisksOtherVehiclesTab.fillTab(tdPUP);
        addAdditionalItems(underlyingRisksOtherVehiclesTab, 4, underlyingRisksOtherVehiclesTab.getRecreationalVehicleAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD), tdPUP);

        // Add 5 Snowmobiles
        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksOtherVehiclesTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RECREATIONAL_VEHICLE.getLabel()), tdOtherVehiclesTabSnowmobile);
        addAdditionalItems(underlyingRisksOtherVehiclesTab, 5, underlyingRisksOtherVehiclesTab.getRecreationalVehicleAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD), tdPUP);

        // Add 3 Golf Carts
        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksOtherVehiclesTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RECREATIONAL_VEHICLE.getLabel()), tdOtherVehiclesTabGolfCart);
        addAdditionalItems(underlyingRisksOtherVehiclesTab, 3, underlyingRisksOtherVehiclesTab.getRecreationalVehicleAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD), tdPUP);

        underlyingRisksOtherVehiclesTab.submitTab();
        policy.getDefaultView().fillFromTo(tdPUP, ClaimsTab.class, BindTab.class, true);
        bindTab.submitTab();

    }

    private void addAdditionalItems(Tab tab, int count, Button button, TestData td) {
        for (int i = 0; i < count; i++) {
            button.click();
            tab.fillTab(td);
        }
    }
}
