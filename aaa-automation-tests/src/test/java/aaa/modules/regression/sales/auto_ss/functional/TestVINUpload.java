package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.enums.DefaultVinVersions.DefaultVersions.SYMBOL_2017;
import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonRatingDetailsOk;
import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonViewRatingDetails;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.helper.VinUploadCleanUpMethods;
import aaa.modules.regression.sales.template.VinUploadAutoSSHelper;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.TextBox;

@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
public class TestVINUpload extends VinUploadAutoSSHelper {
	protected TestData tdBilling = testDataManager.billingAccount;
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private static final String NEW_VIN = "AAAKN3DD0E0344466";
	private static final String NEW_VIN2 = "BBBKN3DDXE0344466";
	private static final String NEW_VIN3 = "CCCKN3DD9E0344466";
	private static final String NEW_VIN4 = "DDDKN3DD8E0344466";
	private static final String NEW_VIN5 = "EEEKN3DD7E0344466";
	private static final String NEW_VIN6 = "FFFKN3DD6E0344466";
	private static final String SUBSEQUENT_RENEWAL_45 = "ZZZKN3DD3E0344466";
	private static final String SUBSEQUENT_RENEWAL_46 = "YYYKN3DD4E0344466";
	private static final String SUBSEQUENT_RENEWAL_35 = "XXXKN3DD4E0344466";
	private static final String NEW_VIN7 = "GGGKN3DD5E0344466";
	private static final String NEW_VIN8 = "HHHKN3DD4E0344466";
	private static final String REFRESHABLE_VIN = "19XFB5F54C1234567";
	private static final String NEW_VIN9 = "ZZXKN3DD2E0344466";
	private static final String NEW_VIN10 = "SSGKN3DD7E0344466";
	private static final String NEW_VIN11 = "DDAKN3DD1E0344466";

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Lev Kazarnovskiy, Team Scorpions
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
	 * 4. Open application and quote
	 * 5. Calculate premium and navigate back to vehicle page
	 * 6. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-533,PAS-1487,PAS-1551,PAS-2714,PAS-6455")
	public void pas533_newVinAdded(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		// Start PAS-2714 NB
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		CustomSoftAssertions.assertSoftly(softly -> {
			// Verify that eash symbol present
			getPolicySymbols().keySet().forEach(symbol -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, symbol).getCell(1).isPresent()).isEqualTo(true));
			// PAS-2714 using Oldest Entry Date, PAS-2716 Entry date overlap between VIN versions
			// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier
			getPolicySymbols().forEach((key, value) -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, key).getCell(2).getValue())
					.as("according to xls, symbols should be : BI Symbol should be BI001, PD001, UM001 ,MP001").isEqualTo(getPolicySymbols().get(key)));
		});

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 NB
		// Covers 2716 NB vin refresh case.
		vehicleTabChecks_527_533_2716();
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
	public void pas527_UpdatedVinRenewal(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get());
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), REFRESHABLE_VIN);

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		// Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		searchForPolicy(policyNumber);

		//Open Renewal to verify the fields
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().fill(addSecondVehicle(REFRESHABLE_VIN, testData));

		// Start PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualToIgnoringCase("AX"));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 Renewal Update Vehicle

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE)).doesNotHaveValue(oldModelValue);
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL)).as("Row with VALID=Y and oldest Entry Date should be used").hasValue("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE)).hasValue("COUPE");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL)).isPresent(false);
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR)).hasValue("2018");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("Yes");
		});

		VehicleTab.buttonSaveAndExit.click();

		//"PAS-544 - Activities and User Notes may be broken: VIN refresh record is missed in Activities and User Notes:"
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).contains("VIN data has been updated for the following vehicle(s): " + REFRESHABLE_VIN);

		// New file with original VIN data is needed for current test to reset original data (REFRESHABLE_VIN). Cleanup used in current method to avoid file Upload for not required tests
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(REFRESHABLE_VIN),DefaultVinVersions.DefaultVersions.SignatureSeries);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN_RESET_ORIGINAL.get()));

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

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN3);

		String policyNumber = createPreconds(testData);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get()));

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE)).hasValue("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MAKE)).hasValue("Other Make");
		});

		// add Vehicle
		TestData twoVehicles = addSecondVehicle(NEW_VIN3, testData)
				.adjust("DriverActivityReportsTab", DataProviderFactory.emptyData());
		policy.getDefaultView().fillFromTo(twoVehicles, VehicleTab.class, PremiumAndCoveragesTab.class);

		CustomSoftAssertions.assertSoftly(softly -> {

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE)).hasValue("TOYOTA");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL)).hasValue("Gt");

		premiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();


		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue())
				.as("First Vehicle : Make should be \"Other Make\"").isEqualToIgnoringCase("Other Make");
		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue())
				.as("First Vehicle : Model should be \"Model\"").isEqualToIgnoringCase("Model");

		new PremiumAndCoveragesTab.RatingDetailsView().openVehicleSummaryPage(2);

		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue())
				.as("Second Vehicle : Make should be \"TOYOTA\"").isEqualToIgnoringCase("TOYOTA");
		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue())
				.as("Second Vehicle : Model should be \"Gt\"").isEqualToIgnoringCase("Gt");

			// Verify that eash symbol present
			getPolicySymbols().keySet().forEach(symbol -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, symbol).getCell(1).isPresent()).isEqualTo(true));
			// PAS-2714 using Oldest Entry Date, PAS-2716 Entry date overlap between VIN versions
			// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier
			getPolicySymbols().forEach((key, value) -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, key).getCell(3).getValue())
					.as("according to xls, symbols should be : BI Symbol should be BI001, PD001, UM001 ,MP001").isEqualTo(getPolicySymbols().get(key)));
		});

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		premiumAndCoveragesTab.saveAndExit();
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
	public void pas4253_RestrictVehicleRefreshNB(@Optional("UT") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN4)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Conversion Van")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()), "Custom Van");

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(this::verifyVehicleInfo_pas2453);

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is NOT applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			verifyVehicleInfo_pas2453(softly);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MAKE)).hasValue("Other Make");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL)).hasValue("Model");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("No");
		});

		vehicleTab.saveAndExit();
	}


	/**
	 * @author Kiruthika Rajendran
	 *
	 * PAS-18969 Restrict VIN Refresh by Vehicle Type
	 * @name Restrict VIN Refresh by Vehicle Type.
	 * @scenario
	 * 0. Create customer and bind the policy
	 * 1. Go to the vehicle tab, enter vehicle info vin Stat Code with which vehicle should not be refreshed and bind the policy
	 * 2. On Administration tab in Admin upload Excel files to update this VIN in the system
	 * 4. Open application and quote
	 * 5. Verify that VIN was NOT updated and all fields are populated with previous info
	 * @details
	 */
  	  @Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18969")
	public void pas18969_testRestrictVehicleRefreshOnRenewal(@Optional("UT") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.STATCODE_VIN_REFERSH_RENEWAL.get());

		TestData testData = getPolicyTD()
		        .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()),"Limited Production/Antique")
		        .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.USAGE.getLabel()),"Pleasure")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()),"1J2WW12P25S124567")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), "2018")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), "OTHER")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel()), "Other Make")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()), "Other Model")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.OTHER_SERIES.getLabel()), "Other Series")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel()), "Sedan")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel()), "30000").resolveLinks();

		  pas18969_restrictVehicleRefreshOnRenewal(testData, vinTableFile);

		  //Check for vehicle information in View Rating Details
		  ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		  softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2)).hasValue("2018");
		  softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2)).hasValue("Other Make");
		  softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2)).hasValue("Other Model");
		  PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
  	 }


	/**
	 * @author Lev Kazarnovskiy/Chris Johns
	 * <p>
	 * PAS-6203
	 * PAS-6455
	 * @name Quick Test of VIN and Control Table Upload
	 * @scenario Upload VIN and Control Tables and verify one row is added to the DB
	 * Adding a simple  test for quicker verification of VIN and Control Table Changes
	 * 1. On Administration tab, in Admin Side of PAS, upload VIN and Control Tables using latest table format
	 * 2. Verify both tables were uploaded by validating the 'rows added' response on UI
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6203, PAS-6455")
	public void pas6203_VinAndControlTablesUpload(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String added = "added:";
		String uploadExcelName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get());

		//Open admin side of pas and navigate to administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VIN_Control table

		//Verify that the proper number or rows were added in the Control table; one row will be added
		//		assertThat(UploadToVINTableTab.labelUploadSuccessful).valueContains(added);

		//Uploading of VIN table
		uploadToVINTableTab.uploadVinTable(uploadExcelName);

		//Verify that the proper number or rows were added in the VIN table; one row will be added
		//		assertThat(UploadToVINTableTab.labelUploadSuccessful).valueContains(added);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) according to renewal timeline
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN5);
		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN5.get());
		/*
		 * Automated Renewal R-Expiration Date
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(pas2716VinTableFileName);
		adminApp().close();
		verifyAutomatedRenewal(NEW_VIN5, policyNumber, policyExpirationDate);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) R-45
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("UT") String state) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), SUBSEQUENT_RENEWAL_45);
		String pas2716VinTableFileName = new VinUploadHelper(getPolicyType(), getState()).getSpecificUploadFile(VinUploadFileType.SUBSEQUENT_RENEWAL_45.get());
		/*
		 * Automated Renewal R-45
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(pas2716VinTableFileName);
		verifyAutomatedRenewal(SUBSEQUENT_RENEWAL_45, policyNumber, policyExpirationDate.minusDays(45));
	}

	/**
	 * @author Chris Johns
	 * <p>
	 * PAS-11659 Renewal Refresh: address scenario when refreshed version is not made "current" (renewal refresh only between R-45 and R-35)
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario DO NOT Refresh before R-46
	 * 0. Retrieve active policy with no vn match
	 * 1 Modify vin data in DB
	 * 2. Move time to R-46, create a renewal version and verify Vin Data Does NOT Refresh
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11659")
	public void pas11659_Renewal_VersionR46(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), SUBSEQUENT_RENEWAL_46);
		String uploadExcelR45 = vinMethods.getSpecificUploadFile(VinUploadFileType.SUBSEQUENT_RENEWAL_46.get());

		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);

		//Get the policy expiration date
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(uploadExcelR45);

		//3. Move to R-46 and generate  renewal image (in data gather status). Retrieve policy and verify VIN data
		// did NOT refresh
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(46), softly);
		softly.close();
	}

	/**
	 * @author Chris Johns
	 * <p>
	 * PAS-11659 Renewal Refresh: address scenario when refreshed version is not made "current" (renewal refresh only between R-45 and R-35)
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario Refresh every time before 'Proposed' Status
	 * 0. Create a policy with no vn match
	 * 1. Upload new vin data
	 * 2. Move time to R-45, create a renewal version and verify Vin Data Does Refresh
	 * 3.Upload new vin data
	 * 4. Move time to R-40, create a renewal version and verify Vin Data Does Refresh

	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11659")
	public void pas11659_Renewal_VersionR45(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN2);

		String R45VIN_UT_SS = vinMethods.getSpecificUploadFile(VinUploadFileType.R45.get());
		String New2VIN_UT_SS = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get());
		/*
		 * Automated Renewal at R-45
		 */
		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(R45VIN_UT_SS);

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();

		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(45), softly);
		/*
		 * Automated Renewal at R-40
		 */
		//3. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(New2VIN_UT_SS);

		//4. Move to R-40 and generate automated renewal image (in data gather status). Retrieve policy and verify VIN data
		// DID refresh
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(40), softly);

		softly.close();
	}

	/**
	 * @author Chris Johns
	 * <p>
	 * PAS-11659 Renewal Refresh: address scenario when refreshed version is not made "current" (renewal refresh only between R-45 and R-35)
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario Refresh occurs at R-35 if renewal image is not proposed yet
	 * 0. Retrieve active policy with no vn match
	 * 2. Move time to R-35, create a renewal version and verify Vin Data Does Refresh
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11659")
	public void pas11659_Renewal_VersionR35(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), SUBSEQUENT_RENEWAL_35);
		String controlTableR35 = vinMethods.getControlTableFile();
		String vinTableR35 = vinMethods.getSpecificUploadFile(VinUploadFileType.SUBSEQUENT_RENEWAL_35.get());

		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadFiles(controlTableR35, vinTableR35);

		//3. Move to R-35 and generate automated renewal image. Retrieve policy and verify VIN data
		// DID refresh

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(35), softly);
		softly.close();
	}

	/**
	 * @author Chris Johns
	 * <p>
	 * PAS-11659 Renewal Refresh: address scenario when refreshed version is not made "current" (renewal refresh only between R-45 and R-25)
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario: Automated Renewal Image Is Created and Proposed at R-26 (UT)
	 * 0. Retrieve active policy with no vn match
	 * 1. Move time to R-26, create and propose a renewal version
	 * 2. Move time to R-25, verify Vin Data Does NOT Refresh
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11659")
	public void pas11659_Renewal_VersionR25(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN4);
		String controlTableR45 = vinMethods.getControlTableFile();
		String vinTableR45 = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get());
		//1. Create a policy
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		/*
		 * Automated Renewal R-26 - renewal Version to Proposed Status
		 */
		//2. Move to R-26 and generate automated renewal image (in data proposed status).
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();

		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(26), softly);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//3. Upload Updated VIN Data
		adminApp().open();
		uploadToVINTableTab.uploadFiles(controlTableR45, vinTableR45);
		/*
		 * Automated Renewal R-25
		 */
		//4. Move to R-25 and generate automated renewal image (in data gather status)
		// Retrieve policy and verify VIN data
		// did NOT refresh because renewal version has already been proposed
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(25), softly);

		softly.close();
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R-35
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) R-35
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN7);

		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN7.get());
		/*
		 * Automated Renewal R-35
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(pas2716VinTableFileName);
		verifyAutomatedRenewal(NEW_VIN7, policyNumber, policyExpirationDate.minusDays(35));
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R-45
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 1. Create Auto policy with 2 vehicles
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
	public void pas2716_BackDatedEndorsement(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = "backdatedVinTable_UT_SS.xlsx";
		String controlTableFile = "backdatedControlTable_UT_SS.xlsx";
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN8);
		// 1. Create Auto policy with 2 vehicles
		String policyNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		adminApp().open();
		uploadToVINTableTab.uploadFiles(controlTableFile, vinTableFile);

		// 2. Renewal term is inforce) R-35
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
		// Add vehicle at renewal version
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		// Make sure refresh occurs
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE)).hasValue("BACKDATED_SS_MAKE");
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL)).hasValue("Gt");
		// Add Vehicle to new renewal version
		TestData renewalVersionVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
				.adjust(AutoSSMetaData.VehicleTab.TYPE.getLabel(), "Private Passenger Auto")
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "7MSRP15H5V1011111");

		List<TestData> renewalVerrsionVehicleTab = new ArrayList<>();
		renewalVerrsionVehicleTab.add(getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN8).getTestData("VehicleTab"));
		renewalVerrsionVehicleTab.add(renewalVersionVehicle);

		TestData testDataRenewalVersion = getPolicyTD().adjust(vehicleTab.getMetaKey(), renewalVerrsionVehicleTab)
				.mask(TestData.makeKeyPath(premiumAndCoveragesTab.getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));

		vehicleTab.fillTab(testDataRenewalVersion);
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		new DocumentsAndBindTab().submitTab();
		// ProposeRenewalVersion500Error

		/*SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		new BillingAccount().acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(1));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);*/
		openAndSearchActivePolicy(policyNumber);

		// 4. Initiate Prior Term (backdated) endorsement with effective date in previous term
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(2));
		openAndSearchActivePolicy(policyNumber);

		policy.createPriorTermEndorsement(getPolicyTD("Endorsement", "TestData_Plus10Day")
				.adjust(TestData.makeKeyPath("EndorsementActionTab", "Endorsement Date"),
						policyExpirationDate.minusDays(7).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));

		new GeneralTab().getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AUTHORIZED_BY.getLabel(), TextBox.class).setValue("Me");

		// 5. Add new vehicle to the endorsement
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		TestData secondVehicle = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), REFRESHABLE_VIN);

		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testData.getTestData(vehicleTab.getMetaKey()));
		listVehicleTab.add(secondVehicle);

		policy.getDefaultView().fillFromTo(testData.adjust(vehicleTab.getMetaKey(), listVehicleTab), VehicleTab.class, DocumentsAndBindTab.class);
		// Upload New Version of Vehicle Added during endorsement in step 5

		// 6. Bind endorsement
		new DocumentsAndBindTab().submitTab();
		new ErrorTab().overrideAllErrors();
		new DocumentsAndBindTab().submitTab();
		new ErrorTab().overrideAllErrors();
		new DocumentsAndBindTab().submitTab();
		// 7. Roll on changes for renewal term with changes made in OOS endorsement
		policy.rollOn().perform(false, false);

		policy.dataGather().start();
	}

	/**
	 * @author Kiruthika Rajendran
	 * <p>
	 * PAS-12872 Update VIN Refresh Y/M/M/S/S Match to use VIN Stub
	 * @name Y/M/M/S/S refreshed from VIN table VIN partial match
	 * @scenario
	 * 0. Create a customer and an auto quote with VIN partial match
	 * 1. Upload new vin data with updated Y/M/M/S/S
	 * 2. Retrieve the created quote
	 * 3. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN stub
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12872")
	public void pas12872_VINRefreshPartialMatchUnboundQuote(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vehYear = "2016";
		String vehMake = "CHEVROLET";
		String vehModel = "MALIBU";
		String vehSeries = "MALIBU HYBRID";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a quote with partial VIN matched data and save the quote number
		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);
		new PremiumAndCoveragesTab().calculatePremium();
		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Current quote #: {}",quoteNumber);

		Map<String, String> stubResultBeforeVinUpload = getStubInfo(quoteNumber);

		//2. Upload new vin data with updated Y/M/M/S/S
		adminApp().open();
		new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.PARTIAL_MATCH_NEW_QUOTE.get()));

		//3. Retrieve the created quote
		findAndRateQuote(testData, quoteNumber);

		Map<String, String> stubResultAfterVinUpload = getStubInfo(quoteNumber);

		buttonViewRatingDetails.click();
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(stubResultBeforeVinUpload.get("COMPSYMBOL")).as("COMPSYMBOL should be changed after upload").isNotEqualTo(stubResultAfterVinUpload.get("COMPSYMBOL"));
			softly.assertThat(stubResultBeforeVinUpload.get("COLLSYMBOL")).as("COLLSYMBOL should be changed  after upload").isNotEqualTo(stubResultAfterVinUpload.get("COLLSYMBOL"));

			getPolicySymbols().forEach((key, value) -> softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, key).getCell(2).getValue())
					.as("according to xls, symbols should be : BI Symbol should be BI002, PD002, UM002 ,MP002").contains("002"));
		});

		buttonRatingDetailsOk.click();
	}

	private Map<String, String> getStubInfo(String quoteNumber) {
		return DBService.get().getRow(String.format("SELECT r.currentVin,I.COMPSYMBOL, I.COLLSYMBOL , R.version FROM Riskitem R, \n"
				+ "Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd \n"
				+ "WHERE R.Ratinginfo_Id = I.Id AND B.Id = R.Baseinfo_Id AND ps.policydetail_id = pd.id \n"
				+ "AND pd.id = r.policydetail_id AND policynumber LIKE '%s' ORDER BY r.ID DESC", quoteNumber));
	}

	/**
	 * @author Kiruthika Rajendran
	 * <p>
	 * PAS-12872 Update VIN Refresh Y/M/M/S/S Match to use VIN Stub
	 * @name VIN refresh no match at renewal timeline R-45
	 * @scenario
	 * 0. Create a customer and an auto quote with VIN no match
	 * 1. Upload new vin data with updated Y/M/M/S/S
	 * 2. Generate automated renewal image R-45
	 * 3. Retrieve the policy
	 * 3. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN stub
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12872")
	public void pas12872_VINRefreshNoMatchRenewalTimeline(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NO_MATCH_ON_RENEWAL.get());
		String vehYear = "2017";
		String vehMake = "FORD";
		String vehModel = "FIESTA";
		String vehSeries = "FIESTA SE";
		String vehBodyStyle = "SEDAN";
		String expectedYear = "2017";
		String expectedMake = "FORD MOTOR";
		String expectedModel = "FORD FIESTA";

		pas12872_VINRefreshOnRenewal(NEW_VIN9, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, vinTableFile, expectedYear, expectedMake, expectedModel);
	}

	/**
	 * @author Kiruthika Rajendran
	 * <p>
	 * PAS-12872 Update VIN Refresh Y/M/M/S/S to make VIN no to full match
	 * @name Y/M/M/S/S refreshed from VIN table VIN no to full match
	 * @scenario
	 * 0. Create a customer and an auto quote with VIN no match
	 * 1. Upload new vin data with updated Y/M/M/S/S
	 * 2. Retrieve the created quote
	 * 3. Navigate to P&C page and validate the updated Y/M/M/S/S for the VIN full match
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12872")
	public void pas12872_VINRefreshPartialtoFullMatchRenewaltimeline(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NO_MATCH_ON_NEW_BUSINESS_FULL_MATCH_ON_RENEWAL.get());
		String vehYear = "2016";
		String vehMake = "ACURA";
		String vehModel = "MDX";
		String vehSeries = "MDX";
		String vehBodyStyle = "SUV";
		String expectedYear = "2017";
		String expectedMake = "ACURA MOTOR";
		String expectedModel = "ACC MDX";

		pas12872_VINRefreshOnRenewal(NEW_VIN10, vehYear, vehMake, vehModel, vehSeries, vehBodyStyle, vinTableFile, expectedYear, expectedMake, expectedModel);
	}

	@AfterClass(alwaysRun = true)
	protected void resetDefault() {
		List<String> listOfVinNumbers = Arrays.asList(NEW_VIN, NEW_VIN2, NEW_VIN3, NEW_VIN4,NEW_VIN5, NEW_VIN6, NEW_VIN7, NEW_VIN8, SUBSEQUENT_RENEWAL_35, SUBSEQUENT_RENEWAL_45, SUBSEQUENT_RENEWAL_46, NEW_VIN10);
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(listOfVinNumbers, DefaultVinVersions.DefaultVersions.SignatureSeries);
		// pas533_newVinAdded
		// NewVIN_UT_SS.xlsx
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(NEW_VIN) , SYMBOL_2017);
		// pas2714_Endorsement
		// New3VIN_UT_SS.xlsx
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(NEW_VIN3) , SYMBOL_2017);
		// pas11659_Renewal_VersionR45
		// New2VIN_UT_SS.xlsx
		// R45VIN_UT_SS.xlsx
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(Arrays.asList(NEW_VIN2) , SYMBOL_2017);

		DBService.get().executeUpdate(String.format(VehicleQueries.REPAIR_COLLCOMP, "7MSRP15H%V"));
		DBService.get().executeUpdate(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE);
		//PartialMatchNewQuote_UT_SS.xlsx
		//pas12872_VINRefreshPartialMatchUnboundQuote
		DBService.get().executeUpdate(String.format("Delete FROM Vehiclerefdatavin WHERE VIN like '%s' and BI_Symbol IN ('BI001','BI002')","1G1ZJ5SU%G"));

		DatabaseCleanHelper.updateVehicleRefDataVinTableByVinAndMaketext("1", "3FADP4BE%H", "SYMBOL_2000", "FORD");
		DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("3FADP4BE%H", "FORD MOTOR");
		DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("1J2WW12P&5", "MDX");
	}
}
