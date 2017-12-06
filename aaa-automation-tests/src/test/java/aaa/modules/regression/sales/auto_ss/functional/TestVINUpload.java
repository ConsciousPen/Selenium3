package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestVINUpload extends AutoSSBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();

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
		String vin = "1FDEU15H7KL055795";

		TestData testData = getAdjustedTestData(vin);
		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		goUploadExcel(vinTableFile, controlTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		CustomAssert.enableSoftMode();

		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("AK".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).getValue())));
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
	 * Fills Non existing vehicle + e
	 * @param vin non -existing
	 * @return
	 */
	private TestData getAdjustedTestData(String vin) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vin);

		testData.adjust(ratingDetailReportsTab.getMetaKey(),  getAdjustedRatingDetailReportTab(testData)).resolveLinks();
		// End Rating DetailReports Tab
		return testData;
	}

	private TestData getAdjustedRatingDetailReportTab(TestData testData) {
		// Workaround for latest membership changes
		// Start of  Rating DetailReports Tab
		TestData addMemberSinceDialog = new SimpleDataProvider()
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click");
		TestData aaaMembershipReportRow = new SimpleDataProvider()
				.adjust("Action", "Add Member Since")
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), addMemberSinceDialog);
		// Adjust Rating details report tab
		return testData.getTestData(ratingDetailReportsTab.getMetaKey())
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);
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
		createAndRateRenewal(policyNumber);
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

		testData.adjust(ratingDetailReportsTab.getMetaKey(),  getAdjustedRatingDetailReportTab(testData)).resolveLinks();

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
		createAndRateRenewal(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();

		TestData secondVehicle = getPolicyTD().getTestData("VehicleTab")
				.adjust("Type", "index=1")
				.adjust("VIN", vinNumber);

		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber));
		listVehicleTab.add(secondVehicle);

		vehicleTab.getAssetList().fill(testData.adjust("VehicleTab", listVehicleTab));

		// Start PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("SS".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue())));

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
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * @name Restrict VIN Refresh by Vehicle Type.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, enter vehicle info vin Stat Code with which vehicle should not be refreshed and bind the policy
	 * 3. On Administration tab in Admin upload Excel files to update this VIN in the system
	 * 4. Open application and quote
	 * 5. Verify that VIN was NOT updated and all fields are populated with previous info
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefresh(@Optional("UT") String state) {

		String vinNumber = "1FDEU15H7KL055795";
		String vinTableFile = getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = getControlTableFile();

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber)
				.adjust(TestData.makeKeyPath("VehicleTab", "Type"), "Conversion Van")
				.adjust(TestData.makeKeyPath("VehicleTab", "Change Vehicle Confirmation"), "OK")
				.adjust(TestData.makeKeyPath("VehicleTab", "Stat Code"), "AV - Custom Van");

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("AV - Custom Van");
		});

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("AV - Custom Van");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Model");
		});
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
		TestData testData = getAdjustedTestData(vinNumber);

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
		policy.getDefaultView().fillUpTo(getPolicyTD().adjust(TestData.makeKeyPath("VehicleTab", "VIN"), vinNumber),PremiumAndCoveragesTab.class);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.verifyFieldHasNotValue(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "UT_SS");
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.MODEL.getLabel(), "UT_SS");

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("AC".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue())));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

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

	private void createAndRateRenewal(String policyNumber) {
		LocalDateTime policyExpDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpDate));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		PremiumAndCoveragesTab.calculatePremium();
	}

	private void findAndRateQuote(TestData testData, String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PremiumAndCoveragesTab.class, true);
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
	protected void vinTablesCleaner() {
		String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT', 'SYMBOL_2000_SS_TEST')";
		try {
			String vehicleRefDataModelId = DBService.get().getValue("SELECT DM.id FROM vehiclerefdatamodel DM " +
					"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
					"WHERE DV.version IN " + configNames).get();
			DBService.get().executeUpdate("DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN " + configNames);
			DBService.get().executeUpdate(String.format("DELETE FROM vehiclerefdatamodel WHERE id='%s'", vehicleRefDataModelId));
		} catch (NoSuchElementException e) {
			log.error("VINs with version names {} are not found in VIN table. VIN table part of DB cleaner was not executed", configNames);
		}
		DBService.get().executeUpdate("DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN " + configNames);
		DBService.get().executeUpdate("UPDATE vehiclerefdatavincontrol SET expirationdate='99999999'");
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
