package aaa.modules.regression.conversions.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestOverrideNumberOfRisks extends ConvPUPBaseTest {

    private UnderlyingRisksPropertyTab underlyingRisksPropertyTab = policy.getDefaultView().getTab(UnderlyingRisksPropertyTab.class);
    private UnderlyingRisksAutoTab underlyingRisksAutoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
    private UnderlyingRisksOtherVehiclesTab underlyingRisksOtherVehiclesTab = policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

    /**
     * @author Josh Carpenter
     * @name Verify rules can be overridden with max number of exposures/risks for NB
     * @scenario
     * 1. Create customer
     * 2. Initiate NB policy
     * 3. Fill Underlying Risk Tabs with:
     *    a. Six (6) or more additional residences
     *    b. Seven (7) or more rental residences
     *    c. Nine (9) or more automobiles
     *    d. Five (5) or more motorcycles
     *    e. Five (5) or more antique automobiles
     *    f. Five (5) or more recreational vehicles (RVs)
     *    g. Six (6) or more watercraft
     *    h. Five (5) or more snowmobiles
     *    i. Five (5) or more all-terrain vehicles (ATVs)
     *    g. Three (3) or more golf carts
 *    4. Finish policy and attempt to bind
     *5. Verify errors can be overridden
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-7003")
    public void pas7003_TestOverrideNumberOfRisksNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Gather test data
        TestData tdPUP = getPolicyDefaultTD();

        // Initiate NB policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksPropertyTab.class);

        // Fill Underlying Risks Tabs
        fillUnderlyingRisksPropertyTab(tdPUP);
        fillUnderlyingRisksAutoTab(tdPUP);
        fillUnderlyingRisksOtherVehiclesTab(tdPUP);

        // Attempt to bind policy
        policy.getDefaultView().fillFromTo(tdPUP, ClaimsTab.class, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsOverrideAndBind(tdPUP,
                ErrorEnum.Errors.AAA_PUP_UWApp_AddRes_OtherThanPrimary,
                ErrorEnum.Errors.AAA_PUP_UWApp_AddRes_Rental,
                ErrorEnum.Errors.AAA_PUP_UWApp_Auto_Antique,
                ErrorEnum.Errors.AAA_PUP_UWApp_Auto_PPA,
                ErrorEnum.Errors.AAA_PUP_UWApp_Motorcycle,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_ATV,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_GC,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_Snowmobile,
                ErrorEnum.Errors.AAA_PUP_UWApp_Watercraft);
    }

    /**
     * @author Josh Carpenter
     * @name Verify rules can be overridden with max number of exposures/risks for conversion
     * @scenario
     * 1. Create customer
     * 2. Initiate conversion policy
     * 3. Fill Underlying Risk Tabs with:
     *    a. Six (6) or more additional residences
     *    b. Seven (7) or more rental residences
     *    c. Nine (9) or more automobiles
     *    d. Five (5) or more motorcycles
     *    e. Five (5) or more antique automobiles
     *    f. Five (5) or more recreational vehicles (RVs)
     *    g. Six (6) or more watercraft
     *    h. Five (5) or more snowmobiles
     *    i. Five (5) or more all-terrain vehicles (ATVs)
     *    g. Three (3) or more golf carts
     *    4. Finish policy and attempt to bind
     *5. Verify errors can be overridden
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-7003")
    public void pas7003_TestOverrideNumberOfRisksConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Gather test data
        TestData tdPUP = getConversionPolicyDefaultTD();

        // Initiate Conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksPropertyTab.class);

        // Fill Underlying Risks Tabs
        fillUnderlyingRisksPropertyTab(tdPUP);
        fillUnderlyingRisksAutoTab(tdPUP);
        fillUnderlyingRisksOtherVehiclesTab(tdPUP);

        // Attempt to bind policy
        policy.getDefaultView().fillFromTo(tdPUP, ClaimsTab.class, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsAndOverride(
                ErrorEnum.Errors.AAA_PUP_UWApp_AddRes_OtherThanPrimary,
                ErrorEnum.Errors.AAA_PUP_UWApp_AddRes_Rental,
                ErrorEnum.Errors.AAA_PUP_UWApp_Auto_Antique,
                ErrorEnum.Errors.AAA_PUP_UWApp_Auto_PPA,
                ErrorEnum.Errors.AAA_PUP_UWApp_Motorcycle,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_ATV,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_GC,
                ErrorEnum.Errors.AAA_PUP_UWApp_RecreationalVeh_Snowmobile,
                ErrorEnum.Errors.AAA_PUP_UWApp_Watercraft);
    }

    /**
     * Fills the UnderlyingRisksPropertyTab with:
     * 1. Six (6) Additional Residences
     * 2. Seven (7) Rental Properties
     * @param tdPUP The PUP test data being used
     */
    private void fillUnderlyingRisksPropertyTab(TestData tdPUP) {
        TestData tdPropertyTabAdditionalResidences = getTestSpecificTD("TestData_AdditionalResidences");
        TestData tdPropertyTabRentalProperties = getTestSpecificTD("TestData_RentalResidences");

        // Create 6 Additional Residences
        tdPUP.adjust(UnderlyingRisksPropertyTab.class.getSimpleName(), tdPropertyTabAdditionalResidences);
        addAdditionalItems(underlyingRisksPropertyTab, 6, underlyingRisksPropertyTab.getAdditionalResidenciesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD), tdPUP);

        // Create 7 Rental Residences
        tdPUP.adjust(UnderlyingRisksPropertyTab.class.getSimpleName(), tdPropertyTabRentalProperties);
        addAdditionalItems(underlyingRisksPropertyTab, 7, underlyingRisksPropertyTab.getAdditionalResidenciesAssetList()
                .getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD), tdPUP);

        underlyingRisksPropertyTab.submitTab();
    }

    /**
     * Fills the underlyingRisksAutoTab with:
     * 1. Nine (9) Automobiles
     * 2. Five (5) Antique Automobiles
     * 3. Five (5) Motorcycles
     * 4. Five (5) Motor Homes
     * @param tdPUP The PUP test data being used
     */
    private void fillUnderlyingRisksAutoTab(TestData tdPUP) {
        TestData tdAutoTabDrivers = getTestSpecificTD("TestData_Drivers");
        TestData tdAutoTabAutomobiles = getTestSpecificTD("TestData_Automobiles");
        TestData tdAutoTabAntiques = getTestSpecificTD("TestData_Antiques");
        TestData tdAutoTabMotorcycles = getTestSpecificTD("TestData_Motorcycles");
        TestData tdAutoTabMotorHomes = getTestSpecificTD("TestData_MotorHomes");


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
    }

    private void fillUnderlyingRisksOtherVehiclesTab(TestData tdPUP) {
        TestData tdOtherVehiclesTabWatercraft = getTestSpecificTD("TestData_Watercraft");
        TestData tdOtherVehiclesTabATV = getTestSpecificTD("TestData_ATV");
        TestData tdOtherVehiclesTabSnowmobile = getTestSpecificTD("TestData_Snowmobile");
        TestData tdOtherVehiclesTabGolfCart = getTestSpecificTD("TestData_GolfCart");

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
    }

    /**
     * Private method used to iteratively add multiple items to a section
     * @param tab The tab object being added to
     * @param count The number of objects to add
     * @param button The 'Add' button asset for the specific section
     * @param td the test data being used that contains the item being added
     */
    private void addAdditionalItems(Tab tab, int count, Button button, TestData td) {
        for (int i = 0; i < count; i++) {
            button.click();
            tab.fillTab(td);
        }
    }
}
