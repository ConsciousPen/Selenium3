package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.VinUploadAutoSSHelper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;

public class TestVINUpload extends VinUploadAutoSSHelper {
	protected TestData tdBilling = testDataManager.billingAccount;

	private static final String NEW_VIN = "1FDEU15H7KL055795";
	private static final String UPDATABLE_VIN = "1HGEM215140028445";

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

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
	 * 4. Open application and quote
	 * 5. Calculate premium and navigate back to vehicle page
	 * 6. Verify that VIN was uploaded and all fields are populated, VIN refresh works after premium calculation
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-533,PAS-1487,PAS-1551,PAS-2714,PAS-6455")
	public void pas533_newVinAdded(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());

		TestData testData = getPolicyTD().adjust(new SimpleDataProvider().adjust(getTestSpecificTD("TestData")))
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-2714 using Oldest Entry Date, PAS-2716 Entry date overlap between VIN versions
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualToIgnoringCase("AN"));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 NB
		// Covers 2716 NB vin refresh case.
		pas527_533_2716_VehicleTabCommonChecks();
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-527,PAS-544,PAS-1406,PAS-1487,PAS-1551")
	public void pas527_NewVinAddedRenewal(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = vinMethods.getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

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

		//Johns - Move system time by two days
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));

		//Go back to MainApp, open quote, verify rerate error message, calculate premium and verify if VIN value is applied
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();

		//Verify pas-938 'Rerate' Error message on error tab
		ErrorTab errorTab = new ErrorTab();
		assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS1801266BZWW.getMessage()).isPresent()).isEqualTo(true);
		log.info("PAS-938 Rerate Error Verified as Present");
		errorTab.cancel();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());

		//end pas 938
		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Policy {} is successfully saved for further use", policyNumber);
		adminApp().open();
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

		pas527_533_2716_VehicleTabCommonChecks();

		VehicleTab.buttonSaveAndExit.click();

		vinMethods.verifyActivitiesAndUserNotes(NEW_VIN);
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
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.UPDATED_VIN.get());
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), UPDATABLE_VIN);

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
		vinMethods.uploadFiles(vinTableFile);

		// Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		searchForPolicy(policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().fill(addSecondVehicle(UPDATABLE_VIN, testData));

		// Start PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualToIgnoringCase("AX"));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		// End PAS-2714 Renewal Update Vehicle

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo(oldModelValue);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("TEST");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).isVisible()).isEqualTo(false);
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
		});

		VehicleTab.buttonSaveAndExit.click();

		vinMethods.verifyActivitiesAndUserNotes(UPDATABLE_VIN);
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
	public void pas2714_Endorsement(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

		String policyNumber = createPreconds(testData);

		adminApp().open();
		vinMethods.uploadFiles(vinTableFile);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel()).getValue()).isEqualTo("Other Make");
		});

		VehicleTab.buttonAddVehicle.click();
		// add Vehicle
		policy.getDefaultView().fillUpTo(getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN), PremiumAndCoveragesTab.class);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("UT_SS");
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("UT_SS");

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("UT_SS");

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue()).isEqualToIgnoringCase("AC"));

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
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
	public void pas4253_RestrictVehicleRefresh(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Conversion Van")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Change Vehicle Confirmation"), "OK")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()), "Custom Van");

		createAndFillUpTo(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(this::pas2453_CommonChecks);

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		adminApp().open();
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is NOT applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			pas2453_CommonChecks(softly);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("OTHER");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).getValue()).isEqualTo("Model");
		});
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
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String added = "added:";
		String uploadExcelName = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		String configExcelName = vinMethods.getControlTableFile();

		//Open admin side of pas and navigate to administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VIN_Control table
		uploadToVINTableTab.uploadControlTable(configExcelName);

		//Verify that the proper number or rows were added in the Control table; one row will be added
		assertThat(UploadToVINTableTab.labelUploadSuccessful).valueContains(added);

		//Uploading of VIN table
		uploadToVINTableTab.uploadVinTable(uploadExcelName);

		//Verify that the proper number or rows were added in the VIN table; one row will be added
		assertThat(UploadToVINTableTab.labelUploadSuccessful).valueContains(added);
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
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);
		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		String pas2716ControlTableFileName = vinMethods.getControlTableFile();
		/*
		 * Automated Renewal R-Expiration Date
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		vinMethods.uploadFiles(pas2716ControlTableFileName, pas2716VinTableFileName);
		pas2716_CommonSteps(NEW_VIN, policyNumber, policyExpirationDate);
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
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);
		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		String pas2716ControlTableFileName = vinMethods.getControlTableFile();
		/*
		 * Automated Renewal R-45
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		vinMethods.uploadFiles(pas2716ControlTableFileName, pas2716VinTableFileName);
		pas2716_CommonSteps(NEW_VIN, policyNumber, policyExpirationDate.minusDays(45));
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
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

		String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());
		String pas2716ControlTableFileName = vinMethods.getControlTableFile();
		/*
		 * Automated Renewal R-35
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		vinMethods.uploadFiles(pas2716ControlTableFileName, pas2716VinTableFileName);
		pas2716_CommonSteps(NEW_VIN, policyNumber, policyExpirationDate.minusDays(35));
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
	public void pas2716_500InternalErrorBackDatedEndorsement(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinTableFile = "backdatedVinTable_UT_SS.xlsx";
		String controlTableFile = "backdatedControlTable_UT_SS.xlsx";
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);
		// 1. Create Auto policy with 2 vehicles
		String policyNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		adminApp().open();
		vinMethods.uploadFiles(controlTableFile, vinTableFile);

		// 2. Renewal term is inforce) R-35
		// According to controlTableFile
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		new BillingAccount().acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(1));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		openAndSearchActivePolicy(policyNumber);
		// 4. Initiate Prior Term (backdated) endorsement with effective date in previous term (for example R-5)
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusMonths(1));
		openAndSearchActivePolicy(policyNumber);

		policy.createPriorTermEndorsement(getPolicyTD("Endorsement", "TestData_Plus10Day")
				.adjust(TestData.makeKeyPath("EndorsementActionTab", "Endorsement Date"),
						policyExpirationDate.minusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		new GeneralTab().getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AUTHORIZED_BY.getLabel(), TextBox.class).setValue("Me");
		// 5. Add new vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		TestData twoVehicles = addSecondVehicle(NEW_VIN, testData)
				.adjust("DriverActivityReportsTab", DataProviderFactory.emptyData());

		policy.getDefaultView().fillFromTo(twoVehicles, VehicleTab.class, DocumentsAndBindTab.class);
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
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test     in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		String configNames = "('SYMBOL_2000_SS_TEST','BACKDATED_SS','BACKDATED2_SS')";
		DatabaseCleanHelper.cleanVinUploadTables(configNames, getState());
	}
}
