package aaa.modules.regression.sales.template.functional;

import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.admin.pages.administration.UploadToVINTablePage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import toolkit.datax.TestData;
import toolkit.db.DBService;

import java.util.NoSuchElementException;

public class TestVINUploadTemplate extends PolicyBaseTest {

    VehicleTab vehicleTab = new VehicleTab();
    UploadToVINTablePage uploadToVINTablePage = new UploadToVINTablePage();
    UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
    PurchaseTab purchaseTab = new PurchaseTab();

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
     *
     * @name Test VINupload 'Add new VIN' scenario for NB.
     * @scenario 0. Create customer
     * 1. Initiate Auto CA quote creation
     * 2. Go to the vehicle tab, fill info with not existing VIN and fill all mandatory info
     * 3. On Administration tab in Admin upload Excel to add this VIN to the system
     * 4. Open application and quote, calculate premium for it
     * 5. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
     * @details
     */
    public void testVINUpload_NewVINAdded(String configExcelName, String uploadExcelName, String vinNumber) {

        TestData testData = getPolicyTD("DataGather", "TestData_UnmatchedVIN")
                .adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);

        //Verify that VIN which will be uploaded is not exist yet in the system
        vehicleTab.verifyFieldHasValue("VIN Matched", "No" );
        VehicleTab.buttonSaveAndExit.click();

        //save quote number to open it later
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Quote " + quoteNumber + " is successfully saved for further use");

        //open Admin application and navigate to Administration tab
        adminApp().switchPanel();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

        //Uploading of VinUpload info, then uploading of the updates for VIN_Control table
        uploadToVINTablePage.uploadExcel(uploadExcelName);
        uploadToVINTableTab.getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION).setValue(true);
        uploadToVINTablePage.uploadExcel(configExcelName);

        //Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
        mainApp().switchPanel();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
        QuoteSummaryPage.tableQuoteList.getRow(1).getCell("Quote #").controls.links.get(1).click();
        policy.dataGather().start();
        NavigationPage.toViewTab("Assignment");

        policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);

        NavigationPage.toViewTab("Vehicle");

        vehicleTab.verifyFieldHasValue("Model", "Gt");
        vehicleTab.verifyFieldIsNotDisplayed("Other Model");
        VehicleTab.buttonSaveAndExit.click();

        log.info(getPolicyType() + " Quote# " + quoteNumber + " was successfully saved " +
                "'Add new VIN scenario' for NB is passed for VIN UPLOAD tests");
    }

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * @name Test VINupload 'Add new VIN' scenario for Renewal.
     * @scenario 0. Create customer
     * 1. Initiate Auto CA quote creation
     * 2. Go to the vehicle tab, fill info with not existing VIN and issue the quote
     * 3. On Administration tab in Admin upload Excel to add this VIN to the system
     * 4. Open application and policy
     * 5. Initiate Renewal for policy
     * 6. Verify that VIN was uploaded and all fields are populated
     * @details
     */
    public void testVINUpload_NewVINAdded_Renewal(String configExcelName, String uploadExcelName, String vinNumber) {

        TestData testData = getPolicyTD("DataGather", "TestData_UnmatchedVIN")
                .adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);


        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);

        //Verify that VIN which will be uploaded is not exist yet in the system
        vehicleTab.verifyFieldHasValue("VIN Matched", "No" );
        vehicleTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        //save policy number to open it later
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Policy " + policyNumber + " is successfully saved for further use");

        //open Admin application and navigate to Administration tab
        adminApp().switchPanel();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

        //Uploading of VinUpload info, then uploading of the updates for VIN_Control table (configExcel)
        uploadToVINTablePage.uploadExcel(uploadExcelName);
        uploadToVINTableTab.getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION).setValue(true);
        uploadToVINTablePage.uploadExcel(configExcelName);

        //Go back to MainApp, initiate Renewal, verify if VIN value is applied
        mainApp().switchPanel();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
        PolicySummaryPage.tablePolicyList.getRow(1).getCell("Policy #").controls.links.get(1).click();
        policy.renew().start();
        NavigationPage.toViewTab("Vehicle");

        vehicleTab.verifyFieldHasValue("Model", "Gt");
        vehicleTab.verifyFieldIsNotDisplayed("Other Model");
        VehicleTab.buttonSaveAndExit.click();

        log.info(getPolicyType() + ". Renewal image for policy " + policyNumber + " was successfully saved " +
                "'Add new VIN scenario' for Renewal is passed for VIN UPLOAD tests");
    }

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * @name Test VINupload 'Update VIN' scenario.
     * @scenario 0. Create customer
     * 1. Initiate Auto SS quote creation
     * 2. Go to the vehicle tab, enter some existed VIN and bind the policy
     * 3. On Administration tab in Admin upload Excel files to update this VIN in the system
     * 4. Open application and quote
     * 5. Verify that VIN was updated successfully and all fields are populated properly
     * @details
     */
    public void testVINUpload_UpdatedVIN_Renewal(String configExcelName, String uploadExcelName, String vinNumber) {

        TestData testData = getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);

        //Verify that VIN which will be updated exists in the system, save value that will be updated
        vehicleTab.verifyFieldHasValue("VIN Matched", "Yes" );
        String oldModelValue = vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue();

        vehicleTab.submitTab();

        //Validation error occurs for used VIN for Choice product during policy issuing, so we need to override it
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)){
            policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, DriverActivityReportsTab.class, false);
            new ErrorTab().overrideAllErrors();
            new PremiumAndCoveragesTab().submitTab();
            policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class, true);
        } else {
            policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
        }
        purchaseTab.submitTab();

        //open Admin application and navigate to Administration tab
        adminApp().switchPanel();
        NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

        //Uploading of VinUpload info, then uploading of the updates for VIN_Control table
        uploadToVINTablePage.uploadExcel(uploadExcelName);
        uploadToVINTableTab.getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION).setValue(true);
        uploadToVINTablePage.uploadExcel(configExcelName);

        //Go back to MainApp, create Renewal image and verify if VIN was updated and new values are applied
        mainApp().switchPanel();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
        PolicySummaryPage.tablePolicyList.getRow(1).getCell("Policy #").controls.links.get(1).click();
        policy.renew().start();
        NavigationPage.toViewTab("Vehicle");

        //Verify that fields are updated
        vehicleTab.verifyFieldHasValue("VIN Matched", "Yes" );
        vehicleTab.verifyFieldHasNotValue("Make", oldModelValue);
        vehicleTab.verifyFieldHasValue("Model", "TEST");
        vehicleTab.verifyFieldHasValue("Body Style", "TEST");
        VehicleTab.buttonSaveAndExit.click();

        log.info(getPolicyType() + ". Renewal image for policy " + PolicySummaryPage.labelPolicyNumber.getValue() + " was successfully created. \n" +
                "'Update VIN scenario' is passed for VIN UPLOAD tests, Renewal Refresh works fine for VINUpdate");
    }

    /*
    Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
    each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
    tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.

    'SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
    files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
    this after method should be updated. But such updates are not supposed to be done.
    Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
     */
    @AfterMethod(alwaysRun = true)
    protected void vin_db_cleaner(){
        String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST')";
        try {
            String VehiclerefdatamodelID = DBService.get().getValue("SELECT DM.id FROM vehiclerefdatamodel DM " +
                    "join vehiclerefdatavin DV on DV.vehiclerefdatamodelid=DM.id " +
                    "WHERE DV.VERSION in " + configNames).get();
            DBService.get().executeUpdate("delete from Vehiclerefdatavin V Where V.VERSION in " + configNames);
            DBService.get().executeUpdate("delete from Vehiclerefdatamodel Where ID='" + VehiclerefdatamodelID + "'");
            DBService.get().executeUpdate("delete from VEHICLEREFDATAVINCONTROL VC Where VC.VERSION in " + configNames);
            DBService.get().executeUpdate("update VEHICLEREFDATAVINCONTROL set EXPIRATIONDATE='99999999'");
        } catch (NoSuchElementException e){
            log.error("Configurations with names " + configNames + " are not present in DB, after method have'n been executed fully");
        }
    }
}
