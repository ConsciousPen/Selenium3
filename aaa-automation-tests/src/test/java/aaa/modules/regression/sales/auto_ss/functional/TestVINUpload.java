package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.testng.annotations.*;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.preconditions.ScorpionsPreconditions;
import aaa.modules.regression.sales.template.VinUploadAutoSSHelper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.TextBox;

public class TestVINUpload extends VinUploadAutoSSHelper {
	protected TestData tdBilling = testDataManager.billingAccount;
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	private static final String NEW_VIN = "AAAKN3DD0E0344466";
	private static final String NEW_VIN2 = "BBBKN3DD0E0344466";
	private static final String NEW_VIN3 = "CCCKN3DD0E0344466";
	private static final String NEW_VIN4 = "DDDKN3DD0E0344466";
	private static final String NEW_VIN5 = "EEEKN3DD0E0344466";
	private static final String NEW_VIN6 = "FFFKN3DD0E0344466";
	private static final String NEW_VIN7 = "GGGKN3DD0E0344466";
	private static final String NEW_VIN8 = "HHHKN3DD4E0344466";
	private static final String REFRESHABLE_VIN = "1HGEM215140028445";

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
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-2714 using Oldest Entry Date, PAS-2716 Entry date overlap between VIN versions
		// PAS-7345 Update "individual VIN retrieval" logic to get liab symbols instead of STAT/Choice Tier
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualToIgnoringCase("AC"));

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
	 * PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
	 * PAS-1487 No Match to Match but Year Doesn't Match
	 * PAS-544 Activities and User Notes
	 * PAS-6455 Make Entry Date Part of Key for VIN Table Upload
	 * PAS-938 Throw Rerate Error if User Skips P&C Page after a day
	 *
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario 0. Create customer
	 * 1. Initiate Auto SS quote creation
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
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527,PAS-544,PAS-1406,PAS-1487,PAS-1551, PAS-938")
	public void pas527_NewVinAddedRenewal(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN2);

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();

		//start pas 938
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		//Move system time by one day - The re-rate error message will only present itself if the quote is at least one day old
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

		//Go back to MainApp, open quote, verify re-rate error message, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();

		//Verify pas-938 'Rerate' Error message on error tab
		ErrorTab errorTab = new ErrorTab();
		assertThat(errorTab.tableErrors.getRowContains
				(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS1801266BZWW.getMessage())).exists();

		log.info("PAS-938 Rerate Error Verified as Present");
		errorTab.cancel();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());

		//end pas 938
		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Policy {} is successfully saved for further use", policyNumber);
		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		vehicleTabChecks_527_533_2716();

		VehicleTab.buttonSaveAndExit.click();

		vinMethods.verifyActivitiesAndUserNotes(NEW_VIN2);
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
	public void pas527_UpdatedVinRenewal(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get());
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), REFRESHABLE_VIN);

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

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
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL).getValue()).isEqualTo("TEST").as("Row with VALID=Y and oldest Entry Date should be used");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE).getValue()).isEqualTo("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL)).isPresent(false);
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR).getValue()).isEqualTo("2005");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED).getValue()).isEqualTo("Yes");
		});

		VehicleTab.buttonSaveAndExit.click();

		//method added for verification of PAS-544 - Activities and User Notes
		vinMethods.verifyActivitiesAndUserNotes(REFRESHABLE_VIN);
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
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get()));

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel()).getValue()).isEqualTo("Other Make");
		});

		// add Vehicle
		TestData twoVehicles = addSecondVehicle(NEW_VIN3, testData)
				.adjust("DriverActivityReportsTab", DataProviderFactory.emptyData());
		policy.getDefaultView().fillFromTo(twoVehicles, VehicleTab.class, PremiumAndCoveragesTab.class);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("TOYOTA");
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");

		premiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("Other Make");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualToIgnoringCase("Model");

		String pageNumbers = "//*[@id='%1$s']/ancestor::div[@id='ratingDetailsPopupForm:vehiclePanel_body']//center//a[contains(text(),'%2$s')]";
		new Link(By.xpath(String.format(pageNumbers, PremiumAndCoveragesTab.tableRatingDetailsVehicles.getLocator().toString().split(" ")[1], 2))).click();

		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("TOYOTA");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("Gt");

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue()).isEqualToIgnoringCase("AC"));

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
	public void pas4253_RestrictVehicleRefreshNB(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN4)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Conversion Van")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Change Vehicle Confirmation"), "OK")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()), "Custom Van");

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(this::verifyVehicleInfo_pas2453);

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is NOT applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			verifyVehicleInfo_pas2453(softly);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel()).getValue()).isEqualTo("Other Make");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Model");
		});

		vehicleTab.saveAndExit();
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
	public void pas6203_VinAndControlTablesUpload(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String added = "added:";
		String uploadExcelName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get());
		String configExcelName = vinMethods.getControlTableFile();

		//Open admin side of pas and navigate to administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VIN_Control table
		uploadToVINTableTab.uploadControlTable(configExcelName);

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
	public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("") String state) {
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
		vinMethods.uploadVinTable( pas2716VinTableFileName);
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
	public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN6);
		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN6.get());
		String pas2716ControlTableFileName = vinMethods.getControlTableFile();
		/*
		 * Automated Renewal R-45
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		adminApp().open();
		vinMethods.uploadVinTable( pas2716VinTableFileName);
		verifyAutomatedRenewal(NEW_VIN6, policyNumber, policyExpirationDate.minusDays(45));
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
	public void pas11659_Renewal_VersionR46(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);
		String configExcelName = vinMethods.getControlTableFile();
		String uploadExcelR45 = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get());

		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);

		//Get the policy expiration date
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//todo
		uploadToVINTableTab.uploadControlTable(configExcelName);
		uploadToVINTableTab.uploadVinTable(uploadExcelR45);

		//3. Move to R-46 and generate  renewal image (in data gather status). Retrieve policy and verify VIN data
		// did NOT refresh
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(46));
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
	public void pas11659_Renewal_VersionR45(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN2);
		String configExcelName = vinMethods.getControlTableFile();
		String uploadExcelR45 = vinMethods.getSpecificUploadFile(VinUploadFileType.R45.get());
		String uploadExcelR40 = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get());
		/*
		 * Automated Renewal at R-45
		 */
		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadControlTable(configExcelName);
		uploadToVINTableTab.uploadVinTable(uploadExcelR45);
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(45));
		/*
		 * Automated Renewal at R-40
		 */
		//3. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(uploadExcelR40);

		//4. Move to R-40 and generate automated renewal image (in data gather status). Retrieve policy and verify VIN data
		// DID refresh
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(40));
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
	public void pas11659_Renewal_VersionR35(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN3);
		String configExcelName = vinMethods.getControlTableFile();
		String uploadExcelR35 = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get());
		String EVENT = "R35";
		//1. Create a policy with VIN matched data and save the expiration data
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Upload Updated VIN Data for utilized VIN
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadControlTable(configExcelName);
		uploadToVINTableTab.uploadVinTable(uploadExcelR35);

		//3. Move to R-35 and generate automated renewal image. Retrieve policy and verify VIN data
		// DID refresh
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(35));
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
	public void pas11659_Renewal_VersionR25(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN4);
		String configExcelName = vinMethods.getControlTableFile();
		String uploadExcelR45 = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get());
		//1. Create a policy
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		/*
		 * Automated Renewal R-26 - renewal Version to Proposed Status
		 */
		//2. Move to R-26 and generate automated renewal image (in data proposed status).
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(26));
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//3. Upload Updated VIN Data
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadControlTable(configExcelName);
		uploadToVINTableTab.uploadVinTable(uploadExcelR45);
		/*
		 * Automated Renewal R-25
		 */
		//4. Move to R-25 and generate automated renewal image (in data gather status)
		// Retrieve policy and verify VIN data
		// did NOT refresh because renewal version has already been proposed
		verifyVinRefreshWhenVersionIsNotCurrent(policyNumber, policyExpirationDate.minusDays(25));

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
	public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN7);

		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN7.get());
		String pas2716ControlTableFileName = vinMethods.getControlTableFile();
		/*
		 * Automated Renewal R-35
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		adminApp().open();
		vinMethods.uploadVinTable(pas2716VinTableFileName);
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
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		adminApp().open();
		vinMethods.uploadFiles(controlTableFile, vinTableFile);

		// 2. Renewal term is inforce) R-35
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
		// Add vehicle at renewal version
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		// Make sure refresh occurs
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("BACKDATED_SS_MAKE");
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
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

	@AfterSuite(alwaysRun = true)
	protected void resetDefault() {
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN2,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN3,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN4,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN5,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN6,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN7,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN8,"SYMBOL_2000");
		DBService.get().executeUpdate(VehicleQueries.REFRESHABLE_VIN_CLEANER_SS);
		DBService.get().executeUpdate(String.format(VehicleQueries.REPAIR_SPECIFIED_COLLCOMP,"7MSRP15H%V"));
		DBService.get().executeUpdate(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE);
	}
}
