package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.By;
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
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Link;

public class TestVINUploadTemplate extends PolicyBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private MembershipTab membershipTab = new MembershipTab();

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
	public void newVinAdded(String vinTableFile, String vinNumber) {

		TestData testData = getTestDataWithSinceMembership(vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//open Admin application and navigate to Administration tab
		uploadFiles(getControlTableFile(), vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);

		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		CustomAssert.enableSoftMode();
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("C".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue())));
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 NB
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		CustomAssert.enableSoftMode();
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
	public void newVinAddedRenewal(String vinTableFile, String vinNumber) {

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber);

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
		uploadFiles(getControlTableFile(), vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber);
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
	public void updatedVinRenewal(String vinTableFile, String vinNumber) {
		TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey());
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey()).ksam(AutoCaMetaData.VehicleTab.VIN.getLabel(), AutoCaMetaData.VehicleTab.VIN.getLabel())
				.adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), vinNumber)
				.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)")
				.adjust(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), "20000").resolveLinks();
		// Build Vehicle Tab
		List<TestData> testDataVehicleTab = new ArrayList<>();
		testDataVehicleTab.add(firstVehicle);
		testDataVehicleTab.add(secondVehicle);
		// Build Assignment Tab
		TestData firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0);
		TestData secondAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);
		listDataAssignmentTab.add(secondAssignment);

		TestData testDataAssignmentTab = new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
		// Adjust Vehicle and Assignment tabs
		TestData testData = getPolicyDefaultTD()
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust("AssignmentTab", testDataAssignmentTab).resolveLinks();

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
		uploadFiles(getControlTableFile(), vinTableFile);

		//Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		createAndRateRenewal(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		// Add third vehicle to the quote
		testDataVehicleTab.add(new SimpleDataProvider().adjust(vehicleTab.getMetaKey(), secondVehicle
				.adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Regular").adjust(AutoCaMetaData.VehicleTab.ODOMETER_READING_DATE.getLabel(),  new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))));

		// Add third assignment and fill quote till P&C tab
		listDataAssignmentTab.add(secondAssignment);
		testDataAssignmentTab = new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
		testData = getPolicyDefaultTD()
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust("AssignmentTab", testDataAssignmentTab).resolveLinks();

		policy.getDefaultView().fillFromTo(testData, VehicleTab.class, PurchaseTab.class, false);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Start PAS-2714 Renewal Update Vehicle
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> CustomAssert.assertTrue("O".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(4).getValue())));

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
		public void endorsement(String vinTableFile, String vinNumber) {
			TestData testData = getTestDataWithSinceMembership(vinNumber).resolveLinks();

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);

			uploadFiles(getControlTableFile(), vinTableFile);

			mainApp().reopen();
			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
			vehicleTab.verifyFieldHasNotValue(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "Other Make");
			vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Model");

			TestData testData2 = getTestDataTwoVehicles(vinNumber);

			policy.getDefaultView().fillFromTo(testData2,VehicleTab.class, PremiumAndCoveragesTab.class);

			PremiumAndCoveragesTab.calculatePremium();

			PremiumAndCoveragesTab.buttonViewRatingDetails.click();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("Other Make");
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualToIgnoringCase("Model");
			String pageNumbers = "//*[@id='%1$s']/ancestor::div[@id='ratingDetailsPopupForm:vehiclePanel_body']//center//a[contains(text(),'%2$s')]";

			new Link(By.xpath(String.format(pageNumbers, PremiumAndCoveragesTab.tableRatingDetailsVehicles.getLocator().toString().split(" ")[1], 2))).click();

			List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
			pas2712Fields.forEach(f -> CustomAssert.assertTrue(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()));
			pas2712Fields.forEach(f -> CustomAssert.assertTrue("C".equalsIgnoreCase(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue())));
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("CA_CH");
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("Gt");
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * @name Restrict VIN Refresh by Vehicle Type.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto CA quote creation
	 * 2. Go to the vehicle tab, enter vehicle info vin Stat Code with which vehicle should not be refreshed and bind the policy
	 * 3. On Administration tab in Admin upload Excel files to update this VIN in the system
	 * 4. Open application and quote
	 * 5. Verify that VIN was NOT updated and all fields are populated with previous info
	 * @details
	 */
	public void pas4253_restrictVehicleRefreshNB(String vinTableFile, String vinNumber) {

			TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
					.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
					.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.TYPE.getLabel()), "Conversion Van")
					.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Change Vehicle Confirmation"), "OK")
					.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "AV - Custom Van");

			precondsTestVINUpload(testData, VehicleTab.class);

			//Verify that VIN which will be uploaded is not exist yet in the system
			assertSoftly(softly -> {
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("AV - Custom Van");
			});

			VehicleTab.buttonSaveAndExit.click();
			String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
			log.info("Quote {} is successfully saved for further use", quoteNumber);

			adminApp().open();
			NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, getControlTableFile());

			//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
			findAndRateQuote(testData, quoteNumber);
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

			assertSoftly(softly -> {
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("AV - Custom Van");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("OTHER");
				softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Model");
			});
		}

	private TestData getTestDataTwoVehicles(String vinNumber) {
		// Build test data with 2 vehicles
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey()).ksam(AutoCaMetaData.VehicleTab.VIN.getLabel(), AutoCaMetaData.VehicleTab.VIN.getLabel())
				.adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), vinNumber)
				.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)")
				.adjust("Odometer Reading", "20000").resolveLinks();

		TestData firstVehicle = getTestSpecificTD("TestData");
		// Build test data with 2 assignments
		TestData firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0);
		TestData secondAssignment = firstAssignment.ksam("Primary Driver");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);
		listDataAssignmentTab.add(secondAssignment);

		TestData testDataAssignmentTab = new SimpleDataProvider().adjust("DriverVehicleRelationshipTable",listDataAssignmentTab);
		// Add Second Vehicle to the vehicle tab
		List<TestData> testDataVehicleTab = new ArrayList<>();
		testDataVehicleTab.add(firstVehicle);
		testDataVehicleTab.add(secondVehicle);
		// add 2 vehicles + 2 assignments to the common testdata

		return getPolicyDefaultTD()
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust("AssignmentTab", testDataAssignmentTab).resolveLinks();
	}

	private TestData getTestDataWithSinceMembership(String vinNumber) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");
		// Workaround for latest membership changes
		// Start of  MembershipTab
		TestData addMemberSinceDialog = new SimpleDataProvider()
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click")
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.BTN_CANCEL.getLabel(), "click");
		TestData aaaMembershipReportRow = new SimpleDataProvider()
				.adjust("Action", "Add Member Since")
				.adjust("AddMemberSinceDialog", addMemberSinceDialog);
		// Adjust membershipTab
		TestData testMembershipTab = testData.getTestData(membershipTab.getMetaKey())
				.adjust(AutoCaMetaData.MembershipTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);
		testData.adjust(membershipTab.getMetaKey(), testMembershipTab);
		return testData;
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

	private void createAndRateRenewal(String policyNumber) {
		LocalDateTime policyExpDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpDate));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		PremiumAndCoveragesTab.calculatePremium();
	}

	private void findAndRateQuote(TestData testData, String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);
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

	protected String getControlTableFile() {
		String defaultControlFileName;
		if (getPolicyType().getShortName().equals("AutoCA")) {
			defaultControlFileName = "controlTable_CA_SELECT.xlsx";
		} else {
			defaultControlFileName = "controlTable_CA_CHOICE.xlsx";
		}
		return defaultControlFileName;
	}

	protected String getSpecificUploadFile(String type) {
		String defaultFileName;
		if (getPolicyType().getShortName().equals("AutoCA")) {
			defaultFileName = "upload%sVIN_CA_SELECT.xlsx";
		} else {
			defaultFileName = "upload%sVIN_CA_CHOICE.xlsx";
		}
		return String.format(defaultFileName, type);
	}

	protected static class UploadFileType {
		public static final UploadFileType UPDATED_VIN = new UploadFileType("Updated");
		public static final UploadFileType ADDED_VIN = new UploadFileType("Added");
		String fileType;

		UploadFileType(String fileType) {
			this.fileType = fileType;
		}

		public String get() {
			return fileType;
		}
	}
}
