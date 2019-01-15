package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.RatingDetailsViewPage;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = Constants.States.CA)
public class TestVINUpload extends TestVINUploadTemplate {

    private static final String NEW_VIN = "AAANK3CC0F0455583"; // Refreshable_CA_SELECT
    private static final String NEW_VIN2 = "BBBNK3CCXF0455583"; // New2VIN_CA_SELECT
    private static final String NEW_VIN_ADDED = "CCCNK3CC4F9455583"; // New6VIN_CA_SELECT // New3VIN_CA_SELECT
    private static final String NEW_VIN4 = "DDDNK3CC3F9455583"; // New4VIN_CA_SELECT
    private static final String NEW_VIN5 = "EEENK2CCXF9455583"; // New5VIN_CA_SELECT
    private static final String NEW_VIN6 = "FFFNK2CC9F9455583"; // New Vin Added
    private static final String NEW_VIN7 = "GGGNK2CC8F9455583"; // New7VIN_CA_SELECT
    private static final String REFRESHABLE_VIN = "4T1BE30K46U656311";
    private static final String HHHNK2CC7F9455583 = "HHHNK2CC7F9455583"; // New9VIN_CA_SELECT
    private static final String NEW_VIN8 = "ABXKN3DDXE0344466";
    private static final String NEW_VIN9 = "LLXKN3DD0E0344466";

    private VehicleTab vehicleTab = new VehicleTab();
    private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Lev Kazarnovskiy, Team Scorpions
     * PAS-533 Quote Refresh -Add New VIN
     * PAS-1406 ata Refresh
     * <p>
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#newVinAdded(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
    public void pas533_newVinAdded(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        newVinAdded(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()), NEW_VIN);
    }

    /**
     * @author Lev Kazarnovskiy
     * PAS-4253 Restrict VIN Refresh by Vehicle Type
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-4253")
    public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        pas4253_restrictVehicleRefreshNB(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get()), NEW_VIN2);
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
     * 5. Verify that VIN was NOT updated and all fields are populated with previous info
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-18969")
    public void pas18969_testRestrictVehicleRefreshCAOnRenewal(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.STATCODE_VIN_REFERSH_RENEWAL.get());
        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "1D3EL55R45N699121")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2017")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "OTHER")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel()), "Other Make")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()), "Other Model")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel()), "Other Series")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel()), "Other Style")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "Cargo Van")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VALUE.getLabel()), "40000").resolveLinks();

        pas18969_restrictVehicleRefreshCAOnRenewal(testData, vinTableFile);

        // Check for the Vehicle information in View Rating Details
        ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo("2017");
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo("Other Make");
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo("Other Model");
        RatingDetailsViewPage.buttonRatingDetailsOk.click();
        softly.close();
    }


    /**
     * @author Lev Kazarnovskiy
     * PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     * PAS-1406 - Data Refresh
     * <p>
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
    public void pas527_UpdatedVinRenewal(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        updatedVinRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get()), REFRESHABLE_VIN);


        // New file with original VIN data is needed for current test to reset original data (REFRESHABLE_VIN). Cleanup used in current method to avoid file Upload for not required tests
        VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(REFRESHABLE_VIN), DefaultVinVersions.DefaultVersions.CaliforniaSelect);

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
     * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2714")
    public void pas2714_Endorsement(@Optional("CA") String state) {
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), HHHNK2CC7F9455583).resolveLinks();

        endorsement(testData, HHHNK2CC7F9455583);
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
    public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN4);

        String policyNumber = openAppAndCreatePolicy(testData);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get()));
        /*
         * Automated Renewal R-Expiration Date
         */
        pas2716_AutomatedRenewal(policyNumber, policyExpirationDate, NEW_VIN4);
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh  R-45
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
    public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN5);

        String policyNumber = openAppAndCreatePolicy(testData);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        adminApp().open();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
        new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN5.get()));
        /*
         * Automated Renewal R-45 Expiration Date
         */
        pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(45), NEW_VIN5);
    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh  R-35
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
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

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void test(@Optional("CA") String state) {
        String methodName = "pas730_VehicleTypeRegular";
        StackTraceElement result = Arrays.stream(Thread.currentThread().getStackTrace()).filter(s -> s.getClassName().startsWith("aaa.modules")).reduce((a, b) -> b).orElse(null);
        if (result != null) {
            methodName = result.getClassName() + "." + result.getMethodName() + "_" + getState();
        }
        String pathToLogs = "/AAA/tcserver/pivotal-tc-server-developer-3.0.0.RELEASE/tomcat-7.0.55.A.RELEASE/logs/aaa.log";
        String log = RemoteHelper.get().getFileContent(pathToLogs);

    }

    /**
     * @author Viktor Petrenko
     * <p>
     * PAS-2716 Update VIN Refresh R-45
     * @name Test VINupload 'Add new VIN' scenario for Renewal.
     * @scenario 1. Create Auto policy with 2 vehicles
     * 2. Renewal term is inforce) R-45
     * 3. Add new VIN versions/VIN data for vehicle3 to be added during endorsement (see notes)e
     * 4. Initiate Prior Term (backdated) endorsement with effective date in previous term (for example R-5)
     * 5. Add new vehicle3
     * 6. Bind endorsement
     * 7. Roll on changes for renewal term with changes made in OOS endorsement
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
    public void pas2716_BackDatedEndorsement(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        // todo create files for ca
        String vinTableFile = "backdatedVinTable_CA_SS.xlsx";
        String controlTableFile = "backdatedControlTable_UT_SS.xlsx";

        TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN7).resolveLinks();

        // 1. Create Auto policy with 2 vehicles
        String policyNumber = openAppAndCreatePolicy(testData);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        adminApp().open();
        //todo create files for ca
        //vinMethods.uploadFiles(controlTableFile, vinTableFile);

        // 2. Renewal term is inforce) R-35
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
        // Add vehicle at renewal version
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        // Make sure refresh occurs
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE)).hasValue("BACKDATED_SS_MAKE");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL)).hasValue("Gt");
        // Add Vehicle to new renewal version
        TestData renewalVersionVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Private Passenger Auto")
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), "7MSRP15H5V1011111");

        List<TestData> renewalVerrsionVehicleTab = new ArrayList<>();
        renewalVerrsionVehicleTab.add(getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), NEW_VIN7).getTestData("VehicleTab"));
        renewalVerrsionVehicleTab.add(renewalVersionVehicle);

        TestData testDataRenewalVersion = getPolicyTD().adjust(vehicleTab.getMetaKey(), renewalVerrsionVehicleTab)
                .mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));

        vehicleTab.fillTab(testDataRenewalVersion);
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        new DocumentsAndBindTab().submitTab();
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-12872")
    public void pas12872_VINRefreshNoMatchUnboundAutoCAQuote(@Optional("CA") String state) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NO_MATCH_NEW_QUOTE.get());
        String vehYear = "2010";
        String vehMake = "VOLKSWAGEN";
        String vehModel = "JETTA";
        String vehSeries = "JETTA S";
        String vehBodyStyle = "SEDAN 4 DOOR";
        String expectedYear = "2010";
        String expectedMake = "VOLKSWAGEN AG";
        String expectedModel = "VOLK JETTA";

        pas12872_VINRefreshNoMatchUnboundAutoCAQuote(NEW_VIN8, vinTableFile, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, expectedYear, expectedMake, expectedModel);
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
     * 4. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN stub
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-12872")
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

        pas12872_VINRefreshNoMatchOnRenewalAutoCA(NEW_VIN9, vinTableFile, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, expectedYear, expectedMake, expectedModel);
    }

    @AfterClass(alwaysRun = true)
    protected void vinTablesCleaner() {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

        List<String> listOfVinIds = Arrays.asList(NEW_VIN, NEW_VIN2, NEW_VIN_ADDED, NEW_VIN4, NEW_VIN5, NEW_VIN6, NEW_VIN7, HHHNK2CC7F9455583);
        VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(listOfVinIds, DefaultVinVersions.DefaultVersions.CaliforniaSelect);

        DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "3VWHX7AJ%A", "SYMBOL_2000", "VOLKSWAGEN");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("3VWHX7AJ%A", "VOLKSWAGEN AG");
        DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "JTEDC3EH%B", "SYMBOL_2000", "TOYOTA");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("JTEDC3EH%B", "TOYOTA MOTOR");
        DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "1N4BL3AP%H", "SYMBOL_2000", "NISSAN");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("1N4BL3AP%H", "NISSAN MOTOR");
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("1D3EL55R&5", "MDX");
    }
}
