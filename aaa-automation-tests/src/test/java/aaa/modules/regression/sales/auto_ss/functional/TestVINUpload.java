package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestVINUpload extends AutoSSBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 *
	 * PAS-533 Quote Refresh -Add New VIN
	 * PAS-1406 Data Refresh
	 * PAS-1487 VIN No Match to Match but Year Doesn't Match
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
	 * PAS-2714 New Liability Symbols
	 *
	 * @name Test VINupload 'Add new VIN' scenario for NB.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and fill all mandatory info
	 * 3. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 4. Open application and quote, calculate premium for it
	 * 5. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-533,PAS-1487,PAS-1551,PAS-2714,PAS-6455")
	public void pas533_newVinAdded(@Optional("UT") String state) {

		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), "1FDEU15H7KL055795");

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		goUploadExcel(vinTableFile, controlTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PremiumAndCoveragesTab.class, true);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		CustomAssert.enableSoftMode();

		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("C".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).getValue())));
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 NB
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "Gt");
		vehicleTab.verifyFieldIsNotDisplayed(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel());
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2005");
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		log.info("Quote {} was successfully saved 'Add new VIN scenario' for NB is passed for VIN UPLOAD tests", quoteNumber);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 Data Refresh
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-1487 No Match to Match but Year Doesn't Match
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527,PAS-544,PAS-1406,PAS-1487,PAS-1551")
	public void pas527_NewVinAddedRenewal(@Optional("UT") String state) {

		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Policy {} is successfully saved for further use", policyNumber);

		goUploadExcel(vinTableFile, controlTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		renewalCreationSteps(policyNumber);
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

		log.info("Renewal image for policy {} was successfully saved 'Add new VIN scenario' for Renewal is passed for VIN UPLOAD tests", policyNumber);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 Data Refresh
	 * PAS-1487 No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
	 *
	 * @name Test VINupload 'Update VIN' scenario.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, enter some existed VIN and bind the policy
	 * 3. On Administration tab in Admin upload Excel files to update this VIN in the system
	 * 4. Open application and quote
	 * 5. Verify that VIN was updated successfully and all fields are populated properly
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527,PAS-544,PAS-1406,PAS-1487")
	public void pas527_updatedVinRenewal(@Optional("UT") String state) {

		String vinNumber = "1HGEM215140028445";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.UPDATED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		goUploadExcel(vinTableFile, controlTableFile);

		// Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		renewalCreationSteps(policyNumber);
		// Start PAS-2714 Renewal Update Vehicle
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("C".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue())));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 Renewal Update Vehicle

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

		log.info("Renewal image for policy {} was successfully created. \n'Update VIN scenario' is passed for VIN UPLOAD tests, Renewal Refresh works fine for Update", PolicySummaryPage.labelPolicyNumber
				.getValue());
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-2714 New liability symbols
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and issue the quote
	 * 3. Upload new data
	 * 4. Make Endorsement
	 * 5. Check that old vehicle was not changed
	 * 6. Add new vehicle with same vin
	 * 7. Check that data was retrieved from db
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2714")
	public void pas2714_Endorsement(@Optional("UT") String state) {

		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);

		goUploadExcel(vinTableFile, controlTableFile);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.verifyFieldHasNotValue(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "Other Make");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "Model");

		VehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().fill(testData.getTestData("VehicleTab"));

		vehicleTab.verifyFieldHasNotValue(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "UT_SS");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "UT_SS");

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("C".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue())));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}


	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and issue the quote
	 * 3. Upload new data
	 * 4. Make Endorsement
	 * 5. Check that old vehicle was not changed
	 * 6. Add new vehicle with same vin
	 * 7. Check that data was retrieved from db
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomationRenewal(@Optional("UT") String state) {
		LocalDateTime policyExpirationDate;
		LocalDateTime policyEffectiveDate;
		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		//1. Retrieve active policy with 2 vehicles (VIN matched) created in previous build
		String policyNumber = createPolicy(testData);
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		//4. System rates renewal image according to renewal timeline
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			HttpStub.executeAllBatches();
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			mainApp().open();
			SearchPage.openPolicy(policyNumber);

			PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
		//5. Validate vehicle information in VRD and DB
	}

	/**
	 * Manual Renewal after R-45
	 */
		//1. Retrieve active policy with 2 vehicles (VIN matched) created in previous build
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		//4. Calculate renewal image premium manually after R-45
		//5. Validate vehicle information in VRD and DB

	/**
	 * Manual Renewal after R-45
	 */
		//1. Retrieve active policy with 2 vehicles (VIN matched) created in previous build
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		//4. System does NOT rate renewal automatically and Agent does NOT calculate premium manually
		//5. Renewal image is Proposed according to renewal timeline
		//6. Validate vehicle information in VRD and DB
	/**
	 * NB Refresh
	 */
		//1. Create quote with 2 vehicles (VIN matched)
		//2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		//3. Retrieve quote and navigate to P&C page
		//4. System calculates premium
		//5. Validate vehicle information in VRD and DB

	/**
	 * Entry date overlap between VIN versions
	 */
		//1. For VIN create VIN versions in Control/VIN Table with same product/state and with VALID set to "yes", entry dates that overlap
		//2. Initiate new quote and add vehicle with VIN created above
		//3. Validate vehicle information in VRD and DB
	/**
	 *
	 * Entry date overlap between VIN versions
	 */
		//1. Create Auto policy with 2 vehicles
		//2. Renewal term is inforce
		//3. Add new VIN versions/VIN data for VINs used above
		//4. Add new VIN versions/VIN data for vehicle3 to be added during endorsement (see notes)
		//5. Initiate Prior Term (backdated) endorsement with effective date in previous term (for example R-5)
		//6. Add new vehicle3
		//7. Bind endorsement
		//8. Roll on changes for renewal term with changes made in OOS endorsement

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 * @param controlTableFile
	 */
	private void goUploadExcel(String vinTableFile, String controlTableFile) {
		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);
	}

	private void precondsTestVINUpload(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	private void renewalCreationSteps(String policyNumber) {
		LocalDateTime policyExpDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpDate));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		PremiumAndCoveragesTab.calculatePremium();
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
		String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST','SYMBOL_2000_SS_ENTRY_DATE')";
		try {
			String vehicleRefDataModelId = DBService.get().getValue("SELECT DM.id FROM vehiclerefdatamodel DM " +
					"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
					"WHERE DV.version IN " + configNames).get();
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN " + configNames);
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatamodel WHERE id='" + vehicleRefDataModelId + "'");
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN " + configNames);
			DBService.get().executeUpdate("UPDATE vehiclerefdatavincontrol SET EXPIRATIONDATE='99999999'");
		} catch (NoSuchElementException e) {
			log.error("Configurations with names {} are not present in DB, after method have'n been executed fully", configNames);
		}
	}

	private String getControlTableFile() {
		String defaultControlFileName = "controlTable_%s_SS.xlsx";
		return String.format(defaultControlFileName, getState());
	}

	private String getSpecificUploadFile(String type) {
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
