package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.postconditions.TestVinUploadPostConditions;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.Link;

public class TestVINUploadTemplate extends PolicyBaseTest implements TestVinUploadPostConditions{

	private VehicleTab vehicleTab = new VehicleTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private MembershipTab membershipTab = new MembershipTab();
	protected VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(getPolicyType().getShortName());

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
	protected void newVinAdded(String vinTableFile, String vinNumber) {

		TestData testData = getTestDataWithSinceMembership(vinNumber);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//open Admin application and navigate to Administration tab
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		// Start PAS-2714 NB
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> Assertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> Assertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualTo("C"));
		// End PAS-2714 NB

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		pas533_CommonChecks();
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
	protected void newVinAddedRenewal(String vinTableFile, String vinNumber) {

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
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		pas533_CommonChecks();

		VehicleTab.buttonSaveAndExit.click();

		vinMethods.verifyActivitiesAndUserNotes(vinNumber);
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
	protected void updatedVinRenewal(String vinTableFile, String vinNumber) {

		TestData testData = getTestDataTwoVehicles(vinNumber);

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
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		createAndRateRenewal(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		// Add third vehicle to the quote
		List<TestData> existingVehicles = testData.getTestDataList("VehicleTab");

		TestData thirdVehicle = new SimpleDataProvider().adjust(vehicleTab.getMetaKey(), existingVehicles.get(1)
				.adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Regular")
				.adjust(AutoCaMetaData.VehicleTab.ODOMETER_READING_DATE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>")));
		policy.getDefaultView().fill(thirdVehicle.resolveLinks());
		// Add third assignment and fill quote till P&C tab
		List<TestData> existingAssignment = testData.getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable");
		TestData testDataAssignmentTab = new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", existingAssignment);

		policy.getDefaultView().fill(new SimpleDataProvider().adjust("AssignmentTab", testDataAssignmentTab).resolveLinks());

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Start PAS-2714 Renewal Update Vehicle
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> Assertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-2714 using Oldest Entry Date
		pas2712Fields.forEach(f -> Assertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(4).getValue()).isEqualTo("O"));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		//Verify that fields are updated
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE.getLabel()).getValue()).isNotEqualTo(oldModelValue);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("TEST");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
		});

		VehicleTab.buttonSaveAndExit.click();

		vinMethods.verifyActivitiesAndUserNotes(vinNumber);
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
	protected void endorsement(String vinTableFile, String vinNumber) {
		TestData testData = getTestDataWithSinceMembership(vinNumber).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);

		vinMethods.uploadFiles(vinTableFile);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("Other Make");
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Model");

		TestData testData2 = getTestDataTwoVehicles(vinNumber);

		policy.getDefaultView().fillFromTo(testData2, VehicleTab.class, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("Other Make");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualToIgnoringCase("Model");
		String pageNumbers = "//*[@id='%1$s']/ancestor::div[@id='ratingDetailsPopupForm:vehiclePanel_body']//center//a[contains(text(),'%2$s')]";

		new Link(By.xpath(String.format(pageNumbers, PremiumAndCoveragesTab.tableRatingDetailsVehicles.getLocator().toString().split(" ")[1], 2))).click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue()).isEqualToIgnoringCase("C"));

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
	protected void pas4253_restrictVehicleRefreshNB(String vinTableFile, String vinNumber) {

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
		vinMethods.uploadFiles(vinTableFile);

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

	private void pas533_CommonChecks() {
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).isPresent()).isEqualTo(false);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("TEST");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
		});
	}

	private TestData getTestDataTwoVehicles(String vinNumber) {
		// Build test data with 2 vehicles
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

	public void precondsTestVINUpload(TestData testData, Class<? extends Tab> tab) {
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

	/**
	Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.

	'SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT' are names of configurations which are used and listed in excel
	files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	this after method should be updated. But such updates are not supposed to be done.
	Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		String configNames = "('SYMBOL_2000_CHOICE_T', 'SYMBOL_2000_CA_SELECT')";
		DatabaseCleanHelper.cleanVinUploadTables(configNames, getState());
	}
}
