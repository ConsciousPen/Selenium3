package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.ViewRatingDetailsPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.helper.VinUploadCleanUpMethods;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import aaa.utils.StateList;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@StateList(states = Constants.States.CA)
public class TestVINUpload extends TestVINUploadTemplate {
    private static final String NEW_VIN = "AAAVB3CC0W0455583";
    private static final String NEW_VIN2 = "BBBVB3CCXW0455583";
    private static final String NEW_VIN3 = "CCCVB3CC4W9455583";
    private static final String NEW_VIN4 = "DDDVB3CC3W9455583";
    private static final String NEW_VIN5 = "EEEVB2CCXW9455583";
    private static final String NEW_VIN6 = "FFFVB2CC9W9455583";
    private static final String NEW_VIN7 = "MMXKN3DD3E0344488";
    private static final String NEW_VIN8 = "HHDDN3DD0E0344488";
    private static final String REFRESHABLE_VIN = "5TFUY5F19D9455583";
    private static final String GGGVB2CC8W9455583 = "GGGVB2CC8W9455583";

    private VehicleTab vehicleTab = new VehicleTab();
    private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * @author Lev Kazarnovskiy
     * <p>
     * PAS-533 Quote Refresh -Add New VIN
     * PAS-1406 Data Refresh
     * PAS-2714 New Liability Symbols
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#newVinAdded(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-533,PAS-1406")
    public void pas533_newVinAdded(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        newVinAdded(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()), NEW_VIN);
    }

    /**
     * @author Lev Kazarnovskiy
     * <p>
     * PAS-1406 Data Refresh
     * PAS-527 Renewal Refresh - Add New VIN & Update Existing
     * <p>
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527,PAS-1406")
    public void pas527_UpdatedVinRenewal(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        updatedVinRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get()), REFRESHABLE_VIN);

        // New file with original VIN data is needed for current test to reset original data (REFRESHABLE_VIN). Cleanup used in current method to avoid file Upload for not required tests
        VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(REFRESHABLE_VIN), DefaultVinVersions.DefaultVersions.CaliforniaChoice);

        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN_RESET_ORIGINAL.get()));
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2714 New Liability Symbols
     * <p>
     * See detailed steps in template file
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2714")
    public void pas2714_Endorsement(@Optional("CA") String state) {
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), GGGVB2CC8W9455583);

        endorsement(testData, GGGVB2CC8W9455583);
    }

    /**
     * @author Lev Kazarnovskiy
     * PAS-4253 Restrict VIN Refresh by Vehicle Type
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB#pas4253_restrictVehicleRefreshNB(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-4253")
    public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        pas4253_restrictVehicleRefreshNB(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get()), NEW_VIN3);
    }

    /**
     * @author Kiruthika Rajendran
     * <p>
     * PAS-18969 Restrict VIN Refresh by Vehicle Type
     * @name Restrict VIN Refresh by Vehicle Type.
     * @scenario 0. Create customer and bind the policy
     * 1. Go to the vehicle tab, enter vehicle info vin Stat Code with which vehicle should not be refreshed and bind the policy
     * 2. On Administration tab in Admin upload Excel files to update this VIN in the system
     * 4. Open application and quote
     * 5. Verify that VIN refresh is restricted and  was NOT updated and all fields are populated with previous info
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-18969")
    public void pas18969_testRestrictVehicleRefreshCAOnRenewal(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.STATCODE_VIN_REFERSH_RENEWAL.get());
        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "1D3EL55R45N699121")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2018")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "OTHER")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel()), "Other Make")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()), "Other Model")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel()), "Other Series")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel()), "Other Style")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "Cargo Van")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VALUE.getLabel()), "50000").resolveLinks();

        pas18969_restrictVehicleRefreshCAOnRenewal(testData, vinTableFile);
        // Check for the Vehicle information in View Rating Details
        ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo("2018");
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo("Other Make");
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo("Other Model");
        ViewRatingDetailsPage.buttonRatingDetailsOk.click();
        softly.close();
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh R
     * @name Test VINupload 'Add new VIN' scenario for Renewal.
     * @scenario 0. Retrieve active policy with (VIN matched)
     * 1. Generate automated renewal image (in data gather status) according to renewal timeline
     * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
     * 3. System rates renewal image according to renewal timeline
     * 4. Validate vehicle information in VRD
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
    public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN4);

        String policyNumber = openAppAndCreatePolicy(testData);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get()));
        /*
         * Automated Renewal R-Expiration Date
         */
        pas2716_AutomatedRenewal(policyNumber, policyExpirationDate, NEW_VIN4);
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh R-45
     * @name Test VINupload 'Add new VIN' scenario for Renewal.
     * @scenario 0. Retrieve active policy with (VIN matched)
     * 1. Generate automated renewal image (in data gather status) R-45
     * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
     * 3. System rates renewal image according to renewal timeline
     * 4. Validate vehicle information in VRD
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
    public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN5);

        String policyNumber = openAppAndCreatePolicy(testData);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN5.get()));
        /*
         * Automated Renewal R-45 Expiration Date
         */
        pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(45), NEW_VIN5);
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh R-35
     * @name Test VINupload 'Add new VIN' scenario for Renewal.
     * @scenario 0. Retrieve active policy with (VIN matched)
     * 1. Generate automated renewal image (in data gather status) R-35
     * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
     * 3. System rates renewal image according to renewal timeline
     * 4. Validate vehicle information in VRD
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
    public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN6);

        String policyNumber = openAppAndCreatePolicy(testData);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN6.get()));
        /*
         * Automated Renewal R-35 Expiration Date
         */
        pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(35), NEW_VIN6);
    }

    /**
     * @author Kiruthika Rajendran
     * <p>
     * PAS-12872 Update VIN Refresh Y/M/M/S/S Match to use VIN Stub
     * @name Y/M/M/S/S refreshed from VIN table VIN no match
     * @scenario 0. Create a customer and an auto CA quote with VIN no match
     * 1. Update Y/M/M/S/S
     * 2. Retrieve the created quote
     * 3. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN stub
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-12872")
    public void pas12872_VINRefreshNoMatchUnboundAutoCAQuote(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NO_MATCH_NEW_QUOTE.get());
        String vehYear = "2009";
        String vehMake = "HYUNDAI";
        String vehModel = "ACCENT";
        String vehSeries = "ACCENT SE";
        String vehBodyStyle = "HATCHBACK 2 DOOR";
        String expectedYear = "2009";
        String expectedMake = "HYUNDAI MOTOR";
        String expectedModel = "HYUNDAI ACCENT";

        pas12872_VINRefreshNoMatchUnboundAutoCAQuote(NEW_VIN7, vinTableFile, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, expectedYear, expectedMake, expectedModel);
    }

    /**
     * @author Kiruthika Rajendran
     * <p>
     * PAS-12872 Update VIN Refresh Y/M/M/S/S Match to use VIN Stub
     * @name VIN refresh no match at renewal timeline R-45
     * @scenario 0. Create a customer and an auto CA quote with VIN no match
     * 1. Update Y/M/M/S/S
     * 2. Generate automated renewal image R-45
     * 3. Retrieve the policy
     * 3. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN stub
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-12872")
    public void pas12872_VINRefreshNoMatchOnRenewalAutoCA(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NO_MATCH_ON_RENEWAL.get());
        String vehYear = "2017";
        String vehMake = "NISSAN";
        String vehModel = "ALTIMA";
        String vehSeries = "ALTIMA 3.5SL";
        String vehBodyStyle = "SEDAN";
        String expectedYear = "2017";
        String expectedMake = "NISSAN MOTOR";
        String expectedModel = "NISS ALTIMA";

        pas12872_VINRefreshNoMatchOnRenewalAutoCA(NEW_VIN8, vinTableFile, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, expectedYear, expectedMake, expectedModel);
    }

    @AfterClass(alwaysRun = true)
    protected void vinTablesCleaner() {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        List<String> listOfVinNumbers = Arrays.asList(NEW_VIN, NEW_VIN2, NEW_VIN3, NEW_VIN4, NEW_VIN5, NEW_VIN6, GGGVB2CC8W9455583);
        VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(listOfVinNumbers, DefaultVinVersions.DefaultVersions.CaliforniaChoice);

        DatabaseCleanHelper.cleanVehicleRefDataVinTable(GGGVB2CC8W9455583, DefaultVinVersions.DefaultVersions.CaliforniaChoice.get());

        DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "KMHCN35C%9", "SYMBOL_2000_CHOICE", "HYUNDAI");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("KMHCN35C%9", "HYUNDAI MOTOR");
        DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "1N4BL3AP%H", "SYMBOL_2000_CHOICE", "NISSAN");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("1N4BL3AP%H", "NISSAN MOTOR");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("1NXOP32E&3", "MDX");
    }
}
