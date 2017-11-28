package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import java.util.NoSuchElementException;

public class TestVINUpload extends AutoSSBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
	 * PAS-1487 VIN No Match to Match but Year Doesn't Match
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
	 *
	 * @name Test VINupload 'Add new VIN' scenario for NB.
	 * @scenario 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and fill all mandatory info
	 * 3. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 4. Open application and quote, calculate premium for it
	 * 5. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-533,PAS-1487")
	public void testVINUpload_NewVINAdded(@Optional("UT") String state) {

		//		String vinNumber = "BBBKN3DD0E0344466";
		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote " + quoteNumber + " is successfully saved for further use");

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PremiumAndCoveragesTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		CustomAssert.enableSoftMode();
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "Gt");
		vehicleTab.verifyFieldIsNotDisplayed(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel());
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2005");
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		log.info("Quote " + quoteNumber + " was successfully saved " +
				"'Add new VIN scenario' for NB is passed for VIN UPLOAD tests");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-1487  No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
	 *
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and issue the quote
	 * 3. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 4. Open application and policy
	 * 5. Initiate Renewal for policy
	 * 6. Verify that VIN was uploaded and all fields are populated
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527")
	public void testVINUpload_NewVINAdded_Renewal(@Optional("UT") String state) {

		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy " + policyNumber + " is successfully saved for further use");

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		CustomAssert.enableSoftMode();
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "Gt");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "UT_SS");
		vehicleTab.verifyFieldIsNotDisplayed(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel());
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2005");
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		VehicleTab.buttonSaveAndExit.click();

		verifyActivitiesAndUserNotes(vinNumber);

		log.info("Renewal image for policy " + policyNumber + " was successfully saved " +
				"'Add new VIN scenario' for Renewal is passed for VIN UPLOAD tests");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1487  No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
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
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527")
	public void testVINUpload_UpdatedVIN_Renewal(@Optional("UT") String state) {

		String vinNumber = "1HGEM215140028445";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.UPDATED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy " + policyNumber + " is successfully saved for further use");

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);

		// Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		//Verify that fields are updated
		CustomAssert.enableSoftMode();
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		vehicleTab.verifyFieldHasNotValue(AutoSSMetaData.VehicleTab.MAKE.getLabel(), oldModelValue);
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "TEST");
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2005");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "TEST");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		VehicleTab.buttonSaveAndExit.click();

		verifyActivitiesAndUserNotes(vinNumber);

		log.info("Renewal image for policy " + PolicySummaryPage.labelPolicyNumber.getValue() + " was successfully created. \n" +
				"'Update VIN scenario' is passed for VIN UPLOAD tests, Renewal Refresh works fine for Update");
	}

	private void precondsTestVINUpload(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);
	}

	private void verifyActivitiesAndUserNotes(String vinNumber) {
		//method added for verification of PAS-544 - Activities and User Notes
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "VIN data has been updated for the following vehicle(s): " + vinNumber)
				.verify.present("PAS-544 - Activities and User Notes may be broken: VIN refresh record is missed in Activities and User Notes:");
	}

	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vin_db_cleaner() {
		String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST')";
		try {
			String VehiclerefdatamodelID = DBService.get().getValue("SELECT DM.id FROM vehiclerefdatamodel DM " +
					"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
					"WHERE DV.version IN " + configNames).get();
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN " + configNames);
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatamodel WHERE id='" + VehiclerefdatamodelID + "'");
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN " + configNames);
			DBService.get().executeUpdate("UPDATE vehiclerefdatavincontrol SET EXPIRATIONDATE='99999999'");
		} catch (NoSuchElementException e) {
			log.error("Configurations with names " + configNames + " are not present in DB, after method have'n been executed fully");
		}
	}

	protected String getControlTableFile() {
		String defaultControlFileName = "controlTable_%s_SS.xlsx";
		return String.format(defaultControlFileName, getState());
	}

	protected String getSpecificUploadFile(String type) {
		String defaultAddedFileName = "upload%sVIN_%s_SS.xlsx";
		return String.format(defaultAddedFileName, type, getState());
	}

	protected enum UploadFilesTypes {
		UPDATED_VIN("Updated"),
		ADDED_VIN("Added");

		private String type;

		UploadFilesTypes(String type) {
			set(type);
		}

		private void set(String type) {
			this.type = type;
		}

		private String get() {
			return type;
		}
	}
}
