package aaa.modules.regression.sales.template.functional;

import static aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsVehicles;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.preconditions.ScorpionsPreconditions;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestVINUploadTemplate extends CommonTemplateMethods {

	private VehicleTab vehicleTab = new VehicleTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	protected void pas2716_AutomatedRenewal(String policyNumber, LocalDateTime nextPhaseDate, String vinNumber) {
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
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue()).isEqualTo("TOYOTA");
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL).getValue()).isEqualTo("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE).getValue()).isEqualTo("TEST");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR).getValue()).isEqualTo("2018");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL).isPresent()).isEqualTo(false);
		});
		//  Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertSoftly(softly -> {
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo("2018");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo("TOYOTA");
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo("Gt");
		});
		PremiumAndCoveragesTab.RatingDetailsView.close();
		PremiumAndCoveragesTab.buttonSaveAndExit.click();
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("VIN data has been updated for the following vehicle(s): %s", vinNumber));
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
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(), vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		// Start PAS-2714 NB
		PremiumAndCoveragesTab.RatingDetailsView.open();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(tableRatingDetailsVehicles.getRow(1, f).getCell(1)).isPresent());
		// PAS-2714 using Oldest Entry Date
		// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier

		// According to VIN xls file, THE OLDEST ONE BY ENTRY DATE HAVE TO BE SELECTED
		pas2712Fields.forEach(f -> assertThat(tableRatingDetailsVehicles.getRow(1, f).getCell(2)).hasValue("C"));
		// End PAS-2714 NB

		PremiumAndCoveragesTab.RatingDetailsView.close();

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
	protected void  newVinAddedRenewal(String vinTableFile, String vinNumber) {

		TestData testData = getNonExistingVehicleTestData(getPolicyTD(), vinNumber);

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("No");
		vehicleTab.submitTab();
		//Start PAS-938 - edited steps for CA products
		//NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//3. Save and exit the quote, move system time by 2 days and retrieve the quote
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getPhaseStartTime().plusDays(2));

		//Go back to MainApp, open quote, verify rerate error message, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();

		// Verify pas-938 'Rerate' Error message on error tab
		ErrorTab errorTab = new ErrorTab();
		assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_CSA1801266BZWW.getMessage())).exists();
		log.info("PAS-938 Rerate Error Verified as Present");
		errorTab.cancel();

		//Change Quote Effective Date to current date, because CA quotes can't be back dated >=\
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get());
		new GeneralTab().getPolicyInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
		//End PAS-938

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber);

		pas533_CommonChecks();

		VehicleTab.buttonSaveAndExit.click();

		//"PAS-544 - Activities and User Notes may be broken: VIN refresh record is missed in Activities and User Notes:"
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).contains("VIN data has been updated for the following vehicle(s): " + vinNumber);
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

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(assignmentTab.getMetaKey()), getTestSpecificTD(assignmentTab.getMetaKey()));

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//save policy number to open it later
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		searchForPolicy(policyNumber);

		//Open Renewal to verify the fields
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());

		PremiumAndCoveragesTab.RatingDetailsView.open();
		// Start PAS-2714 Renewal Update Vehicle
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(tableRatingDetailsVehicles.getRow(1, f).getCell(1)).isPresent());
		// PAS-2714 using Oldest Entry Date and 'Valid' fields
		// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier
		pas2712Fields.forEach(f -> assertThat(tableRatingDetailsVehicles.getRow(1, f).getCell(2)).hasValue("A"));

		PremiumAndCoveragesTab.RatingDetailsView.close();
		// End PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		//Verify that fields are updated
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue()).isNotEqualTo(oldModelValue);
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL)).as("Row with VALID=Y and oldest Entry Date should be used").hasValue("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE)).hasValue("COUPE");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR)).hasValue("2018");
		});

		VehicleTab.buttonSaveAndExit.click();

		//"PAS-544 - Activities and User Notes may be broken: VIN refresh record is missed in Activities and User Notes:"
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).contains("VIN data has been updated for the following vehicle(s): " + vinNumber);
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
	protected void endorsement(TestData testData, String vinNumber) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		String policyNumber = openAppAndCreatePolicy(testData);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN9.get()));

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		log.info("First vehicle, at the vehicle tab, should have same values");

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MAKE).getValue()).isEqualTo("Other Make");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL).getValue()).isEqualTo("Other Model");

			TestData testDataTwoVehicles = getTestDataWithTwoVehicles(testData, vinNumber);
			policy.getDefaultView().fillFromTo(testDataTwoVehicles, VehicleTab.class, PremiumAndCoveragesTab.class, true);
			premiumAndCoveragesTab.calculatePremium();
			PremiumAndCoveragesTab.RatingDetailsView.open();

			log.info("First vehicle, at the PremiumAndCoveragesTab, should have same values");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("Other Make");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualToIgnoringCase("Other Model");

			PremiumAndCoveragesTab.RatingDetailsView.tableVehicleSummary.moveToPage(2);
			// Verify that eash symbol present
			getPolicySymbols().keySet().forEach(symbol -> softly.assertThat(tableRatingDetailsVehicles.getRow(1, symbol).getCell(1).isPresent()).isEqualTo(true));
			// Check second (uploaded) vehicle is here
			getPolicySymbols().forEach((key, value) -> softly.assertThat(tableRatingDetailsVehicles.getRow(1, key).getCell(3).getValue())
					.as("according to xls, symbols should be : BI Symbol should be BI001, PD001, UM001 ,MP001").isEqualTo(getPolicySymbols().get(key)));

			log.info("Second vehicle, at the PremiumAndCoveragesTab, should have different from first vehicle values");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("MAKEPAS2713ENDOR");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("MODELPAS2713ENDOR");
		});
		PremiumAndCoveragesTab.RatingDetailsView.close();
		Tab.buttonSaveAndExit.click();
	}

	private HashMap<String, String> getPolicySymbols() {
		HashMap<String,String> policySymbols = new HashMap<>(); // in fact it is duplication of symbols from NewVIN_CA.xlsx, New3VIN_UT_SS.xlsx

		policySymbols.put("BI Symbol","E");
		policySymbols.put("PD Symbol","E");
		policySymbols.put("UM Symbol","E");
		policySymbols.put("MP Symbol","E");
		return policySymbols;
	}

	private TestData getTestDataWithTwoVehicles(TestData testData, String vinNumber) {
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
		return testData
				.adjust(vehicleTab.getMetaKey(), testDataVehicleTab)
				.adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
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
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), TestMSRPRefreshTemplate.getVehicleMotorHomeTestData("2018"));
		// Add Vin to test data
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber);
		// Mask Vehicle from DriverVehicleRelationshipTable
		TestData firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
		testData.adjust(assignmentTab.getMetaKey(), new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", firstAssignment));

		createQuoteAndFillUpTo(testData, VehicleTab.class);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).getValue()).isEqualTo("Motor Home");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED).getValue()).isEqualTo("No");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE).getValue()).isEqualTo("Motorhome");
		});

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE)).hasValue("Motor Home");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).hasValue("Motorhome");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL)).hasValue("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL)).hasValue("Other Model");
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

		PremiumAndCoveragesTab.RatingDetailsView.open();
		String compSymbol = tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
		String collSymbol = tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
		PremiumAndCoveragesTab.RatingDetailsView.close();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertSoftly(softly -> {
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo(compSymbol);
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo(collSymbol);

		});
		PremiumAndCoveragesTab.RatingDetailsView.close();
	}

	private void pas533_CommonChecks() {
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.OTHER_MODEL).isPresent()).isEqualTo(false);
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL).getValue()).isEqualTo("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE).getValue()).isEqualTo("TEST");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.YEAR).getValue()).isEqualTo("2018");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN_MATCHED).getValue()).isEqualTo("Yes");
		});
	}

	protected void pas12872_VINRefreshNoMatchUnboundAutoCAQuote(String vinNumber, String vinTableFile, String vehYear, String vehMake, String vehModel, String vehSeries, String vehBodyStyle, String expectedYear, String expectedMake, String expectedModel) {
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();
		//1. Create a quote with no VIN matched data and save the quote number
		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);
		new  PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.RatingDetailsView.open();
		PremiumAndCoveragesTab.RatingDetailsView.close();
		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.debug("quoteNumber after creating auto_ca quote is "+quoteNumber);

		//2. Upload new vin data with updated Y/M/M/S/S
		adminApp().open();
		new UploadToVINTableTab().uploadVinTable(vinTableFile);

		//3. Retrieve the created quote
		findAndRateQuote(testData, quoteNumber);
		PremiumAndCoveragesTab.RatingDetailsView.open();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();

		//4. Check for the updated Y/M/M values in View Rating Details table
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo(expectedYear);
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo(expectedMake);
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo(expectedModel);

		PremiumAndCoveragesTab.RatingDetailsView.close();

		softly.close();
	}

	protected void pas12872_VINRefreshNoMatchOnRenewalAutoCA(String vinNumber, String vinTableFile, String vehYear, String vehMake, String vehModel, String vehSeries, String vehBodyStyle, String expectedYear, String expectedMake, String expectedModel) {
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();
		//1. Create a policy with VIN no matched data and save the expiration data
		String policyNumber = openAppAndCreatePolicy(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//2. Upload new vin data with updated Y/M/M/S/S
		adminApp().open();
		new UploadToVINTableTab().uploadVinTable(vinTableFile);

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();

		//4. Generate automated renewal image according to renewal timeline
		//5. Move time to renewal time point
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		//6. Retrieve the policy
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//7. System rates renewal image according to renewal timeline
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//8. Navigate to Premium and Coverages tab and calculate premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();
		//9. Check for the updated Y/M/M values in View Rating Details table
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo(expectedYear);
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo(expectedMake);
		softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo(expectedModel);

		PremiumAndCoveragesTab.RatingDetailsView.close();

		softly.close();
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

		TestData firstAssignment = testDataAdjusted.getTestData(assignmentTab.getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);

		return testDataAdjusted.adjust(assignmentTab.getMetaKey(), new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab));
	}

	public TestData modifyVehicleTabNonExistingVin(TestData testData, String vinNumber) {
		testData
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "OTHER")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2018")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel()), "Other Make")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel()), "Other Model")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel()), "Other Series")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel()), "Other Body Style")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel()), "20")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel()), "4000")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.STAT_CODE.getLabel()), "Passenger Car Large")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VALUE.getLabel()), "4000")
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
		premiumAndCoveragesTab.calculatePremium();
	}

	public void enableVinIfDisabled() {
		String isVinRefreshEnabled = DBService.get().getValue(VehicleQueries.SELECT_VALUE_VIN_REFRESH).get();
		log.info("Vin refresh is enabled {} ", isVinRefreshEnabled);
		if ("false".equalsIgnoreCase(isVinRefreshEnabled)) {
			log.info("Vin will be enabled");
			ScorpionsPreconditions.enableVinRefresh();

		}
	}

	protected void pas18969_restrictVehicleRefreshCAOnRenewal(TestData testData, String vinTableFile){
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		// Upload new vin data
		adminApp().open();
		new UploadToVINTableTab().uploadVinTable(vinTableFile);

		// Generate automated renewal image according to renewal timeline
		// Move time to renewal time point
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		// Retrieve the policy
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		// Navigate to Premium and Coverages tab and calculate premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();
	}
}
