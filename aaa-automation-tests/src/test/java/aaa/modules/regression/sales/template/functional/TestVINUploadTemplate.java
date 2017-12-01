package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.testng.annotations.AfterMethod;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;

public class TestVINUploadTemplate extends PolicyBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-533 -Quote Refresh -Add New VIN
	 * PAS-1406 - Data Refresh
	 * PAS-1487 VIN No Match to Match but Year Doesn't Match
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-2714 Correct VIN Data (VIN Matched - Liability Symbols)
	 *
	 * @name Test VINupload 'Add new VIN' scenario for NB.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto CA quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and fill all mandatory info
	 * 3. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 4. Open application and quote, calculate premium for it
	 * 5. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
	 * @details
	 */
	public void newVinAdded(String controlTableFile, String vinTableFile, String vinNumber) {

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//open Admin application and navigate to Administration tab
		uploadFiles(controlTableFile, vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		CustomAssert.enableSoftMode();
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("K".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).getValue())));
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 NB
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.MODEL.getLabel(), "Gt");
		vehicleTab.verifyFieldIsNotDisplayed(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel());
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		log.info("{} Quote# {} was successfully saved 'Add new VIN scenario' for NB is passed for VIN UPLOAD tests", getPolicyType(), quoteNumber);
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-1487  No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
	 *
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto CA quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN and issue the quote
	 * 3. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 4. Open application and policy
	 * 5. Initiate Renewal for policy
	 * 6. Verify that VIN was uploaded and all fields are populated
	 * @details
	 */
	public void newVinAddedRenewal(String controlTableFile, String vinTableFile, String vinNumber) {

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		uploadFiles(controlTableFile, vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		renewalCreationSteps(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		CustomAssert.enableSoftMode();
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.MODEL.getLabel(), "Gt");
		vehicleTab.verifyFieldIsNotDisplayed(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel());
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel(), "TEST");
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.YEAR.getLabel(), "2005");
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		VehicleTab.buttonSaveAndExit.click();

		verifyActivitiesAndUserNotes(vinNumber);

		log.info("{}. Renewal image for policy {} was successfully saved 'Add new VIN scenario' for Renewal is passed for VIN UPLOAD tests", getPolicyType(), policyNumber);
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh
	 * PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1487  No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
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
	public void updatedVinRenewal(String controlTableFile, String vinTableFile, String vinNumber) {
		TestData firstVehicle = getPolicyTD().getTestData("VehicleTab");
		TestData secondVehicle = getPolicyTD().getTestData("VehicleTab").ksam("VIN", "Primary Use")
				.adjust("VIN", vinNumber)
				.adjust("Primary Use", "Pleasure (recreational driving only)").resolveLinks();

		List<TestData> testDataVehicleTab = new ArrayList<>();
		testDataVehicleTab.add(firstVehicle);
		testDataVehicleTab.add(secondVehicle);
		TestData adjustedVehicleTab = new SimpleDataProvider().adjust("VehicleTab", testDataVehicleTab);
		TestData testData = getPolicyDefaultTD().adjust("VehicleTab", adjustedVehicleTab).resolveLinks();

		//TestData testData = getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber).resolveLinks();

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		uploadFiles(controlTableFile, vinTableFile);

		//Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		renewalCreationSteps(policyNumber);

		// Start PAS-2714 Renewal Update Vehicle
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("T".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue())));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 Renewal Update Vehicle

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		//Verify that fields are updated
		CustomAssert.enableSoftMode();
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		vehicleTab.verifyFieldHasNotValue(AutoCaMetaData.VehicleTab.MAKE.getLabel(), oldModelValue);
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.MODEL.getLabel(), "TEST");
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel(), "TEST");
		// PAS-1487  No Match to Match but Year Doesn't Match
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.YEAR.getLabel(), "2005");

		VehicleTab.buttonSaveAndExit.click();

		verifyActivitiesAndUserNotes(vinNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
		log.info("{}. Renewal image for policy {} was successfully created. \n'Update VIN scenario' is passed for VIN UPLOAD tests, Renewal Refresh works fine for VINUpdate", getPolicyType(), PolicySummaryPage.labelPolicyNumber
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
	public void endorsement(String controlTableFile, String vinTableFile, String vinNumber) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber);


		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);

		uploadFiles(vinTableFile, controlTableFile);

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

	private void uploadFiles(String controlTableFile, String vinTableFile) {
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

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
	protected void vin_db_cleaner() {
		String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST'"
				+ ", 'SYMBOL_2000_CH_ENTRY_DATE', 'SYMBOL_2000_CA_SELECT_ENTRY_DATE')";
		try {
			String vehicleRefDatamodelId = DBService.get().getValue("SELECT DM.id FROM vehiclerefdatamodel DM " +
					"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
					"WHERE DV.version IN " + configNames).get();
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN " + configNames);
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatamodel WHERE id='" + vehicleRefDatamodelId + "'");
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN " + configNames);
			DBService.get().executeUpdate("UPDATE vehiclerefdatavincontrol SET expirationdate='99999999'");
		} catch (NoSuchElementException e) {
			log.error("Configurations with names {} are not present in DB, after method have'n been executed fully", configNames);
		}
	}
}
