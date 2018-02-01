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
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.Link;

public class TestVINUploadTemplate extends CommonTemplateMethods{

	private VehicleTab vehicleTab = new VehicleTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private MembershipTab membershipTab = new MembershipTab();
	private AssignmentTab assignmentTab = new AssignmentTab();

	protected void pas2716_AutomatedRenewal(String policyNumber,LocalDateTime nextPhaseDate,String  vinNumber) {
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		moveTimeAndRunRenewJobs(nextPhaseDate);
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("VIN data has been updated for the following vehicle(s): %s", vinNumber));
		//4. System rates renewal image according to renewal timeline
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//5. Validate vehicle was updated
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("CA_MAKE_TEXT");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("TEST");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).isPresent()).isEqualTo(false);
		});
		//  Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Year").getCell(2).getValue()).isEqualTo("2005");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Make").getCell(2).getValue()).isEqualTo("CA_MAKE_TEXT");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Model").getCell(2).getValue()).isEqualTo("Gt");
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

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
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//open Admin application and navigate to Administration tab
		adminApp().open();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

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
	 * PAS-938 Throw Rerate Error if User Skips P&C Page after a quote is a day old
	 *
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto CA quote creation
	 * 2. Go to the vehicle tab, fill info with not existing VIN, and calculate the premium
	 * 3. Save and exit the quote, move system time by 2 days and retrieve the quote
	 * 4. Attempt to bind without calculating premium; verify the Rerate Error message
	 * 5. Continue to bind the quote
	 * 6. On Administration tab in Admin upload Excel to add this VIN to the system
	 * 7. Open application and policy
	 * 8. Initiate Renewal for policy
	 * 9. Verify that VIN was uploaded and all fields are populated
	 * @details
	 */
	protected void newVinAddedRenewal(String vinTableFile, String vinNumber) {

		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();
//Start PAS-938 - edited steps for CA products
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//Johns - Move system time by two days
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));

		//Go back to MainApp, open quote, verify rerate error message, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();

		// Verify pas-938 'Rerate' Error message on error tab
		ErrorTab errorTab = new ErrorTab();
		Assertions.assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_CSA1801266BZWW.getMessage()).isPresent()).isEqualTo(true);
		log.info("PAS-938 Rerate Error Verified as Present");
		errorTab.cancel();

		//Change Quote Effective Date to current date, because CA quotes can't be back dated >=\
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get());
		GeneralTab generalTab = new GeneralTab();
		generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
		//End PAS-938

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		adminApp().open();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		pas533_CommonChecks();

		VehicleTab.buttonSaveAndExit.click();

		new VinUploadHelper(getPolicyType(), getState()).verifyActivitiesAndUserNotes(vinNumber);
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
	 * @scenario
	 * 0. Create customer
	 * 1. Initiate Auto SS quote creation
	 * 2. Go to the vehicle tab, enter some existed VIN and bind the policy
	 * 3. On Administration tab in Admin upload Excel files to update this VIN in the system
	 * 4. Open application and quote
	 * 5. Verify that VIN was updated successfully and all fields are populated properly
	 * @details
	 */
	protected void updatedVinRenewal(String vinTableFile, String vinNumber) {

		TestData testData = getTestDataTwoVehicles(vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		adminApp().reopen();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		searchForPolicy(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
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

		new VinUploadHelper(getPolicyType(), getState()).verifyActivitiesAndUserNotes(vinNumber);
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
	protected void endorsement(TestData testData,String vinTableFile, String vinNumber) {
		String policyNumber = createPolicyPreconds(testData);

		adminApp().reopen();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("OTHER");
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel()).getValue()).isEqualTo("Other Make");
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Other Model");

		TestData firstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD(), vinNumber).getTestData("VehicleTab");
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
						.adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), vinNumber);

		// Build Vehicle Tab old version vin + updated vehicle
		List<TestData> testDataVehicleTab = new ArrayList<>();
		testDataVehicleTab.add(firstVehicle);
		testDataVehicleTab.add(secondVehicle);

		// Build Assignment Tab
		TestData testDataAssignmentTab = getTwoAssignmentsTestData();

		// add 2 vehicles + 2 assignments to the common testdata
		testData
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();

		policy.getDefaultView().fillFromTo(testData, VehicleTab.class, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("OTHER");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualToIgnoringCase("OTHER");
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
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(),TestMSRPRefreshTemplate.getVehicleMotorHomeTestData());
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Motor Home");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("Motorhome");
		});

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		adminApp().reopen();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Motor Home");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("Motorhome");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Other Model");
		});
	}

	 /*
	 Comp/Coll symbols refreshed from VIN table VIN partial match

	 */
	protected void MSRPRefreshCompColl(String vinTableFile, String vinNumber) {

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Change Vehicle Confirmation"), "OK")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "Passenger Van");

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
		String collSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		adminApp().reopen();
		new VinUploadHelper(getPolicyType(), getState()).uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo(compSymbol);
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo(collSymbol);

		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
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
		TestData firstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD(), vinNumber);
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey());

		// Build Vehicle Tab
		List<TestData> testDataVehicleTab = new ArrayList<>();
		testDataVehicleTab.add(firstVehicle);
		testDataVehicleTab.add(secondVehicle);

		// Build Assignment Tab
		TestData testDataAssignmentTab = getTwoAssignmentsTestData();

		// add 2 vehicles + 2 assignments to the common testdata
		return getPolicyDefaultTD()
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
	}

	public TestData getNonExistingVehicleTestData(TestData testData, String vinNumber) {
		TestData testDataAdjusted = modifyVehicleTabNonExistingVin(testData, vinNumber);

		TestData firstAssignment = testDataAdjusted.getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);

		return testDataAdjusted.adjust("AssignmentTab", new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab));
	}

	public TestData modifyVehicleTabNonExistingVin(TestData testData, String vinNumber) {
		testData
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "OTHER")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2005")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel()), "Other Make")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()), "Other Model")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel()), "Other Series")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel()), "Other Body Style")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel()), "20")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel()), "40000")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "Passenger Car Large")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VALUE.getLabel()), "40000")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.ODOMETER_READING_DATE.getLabel()), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"));
		return testData;
	}

	protected TestData getTestDataWithSinceMembershipAndSpecificVinNumber(String vinNumber) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");
		// Workaround for latest membership changes
		return getTestWithSinceMembership(testData);
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
