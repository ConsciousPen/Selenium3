package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.sales.auto_ss.functional.helpers.TestVinUploadHelper;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;

public class TestVINUpload extends TestVinUploadHelper {
	protected TestData tdBilling = testDataManager.billingAccount;

	private VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(getPolicyType());

	private static final String NEW_VIN = "1FDEU15H7KL055795";
	private static final String UPDATABLE_VIN = "1HGEM215140028445";
	private final String pas2716VinTableFileName = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());
	private final String pas2716ControlTableFileName = vinMethods.getControlTableFile();

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

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);
		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		VehicleTab.buttonSaveAndExit.click();

		//save quote number to open it later
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		// Start PAS-2714 NB
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		// PAS-2714 using Oldest Entry Date, PAS-2716 Entry date overlap between VIN versions
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).getValue()).isEqualToIgnoringCase("AK"));

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

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());
		String controlTableFile = vinMethods.getControlTableFile();
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN);

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "No");
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Policy {} is successfully saved for further use", policyNumber);

		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, find created policy, initiate Renewal, verify if VIN value is applied
		createAndRateRenewal(policyNumber,TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));

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
	public void pas527_updatedVinRenewal(@Optional("UT") String state) {

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.UPDATED_VIN.get());
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), UPDATABLE_VIN);

		precondsTestVINUpload(getTestDataWithMembershipSinceDate(testData), VehicleTab.class);

		//Verify that VIN which will be updated exists in the system, save value that will be updated
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel(), "Yes");
		String oldModelValue = vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE).getValue();
		vehicleTab.submitTab();

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Policy {} is successfully saved for further use", policyNumber);

		vinMethods.uploadFiles(vinTableFile);

		// Go back to MainApp, find created policy, create Renewal image and verify if VIN was updated and new values are applied
		createAndRateRenewal(policyNumber,TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();

		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
				.adjust(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), UPDATABLE_VIN);

		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), UPDATABLE_VIN));
		listVehicleTab.add(secondVehicle);

		vehicleTab.getAssetList().fill(addSecondVehicle(NEW_VIN, testData));

		// Start PAS-2714 Renewal Update Vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).isPresent()).isEqualTo(true));
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(1).getValue()).isEqualToIgnoringCase("SS"));

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
	public void pas2714_Endorsement(@Optional("UT") String state) {

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());
		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);

		String policyNumber = createPreconds(testData);

		vinMethods.uploadFiles(vinTableFile);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("Other Make");
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Model");

		VehicleTab.buttonAddVehicle.click();
		// add Vehicle
		policy.getDefaultView().fillUpTo(getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN),PremiumAndCoveragesTab.class);

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
	public void pas4253_RestrictVehicleRefresh(@Optional("UT") String state) {

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), NEW_VIN)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Conversion Van")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Change Vehicle Confirmation"), "OK")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()), "AV - Custom Van");

		precondsTestVINUpload(testData, VehicleTab.class);

		//Verify that VIN which will be uploaded is not exist yet in the system
		assertSoftly(this::pas2453_CommonChecks);

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Quote {} is successfully saved for further use", quoteNumber);

		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			pas2453_CommonChecks(softly);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("OTHER");
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
	public void pas6203_VinAndControlTablesUpload(@Optional("UT") String state) {

		String added = "added: 1";
		String uploadExcelName = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());
		String configExcelName = vinMethods.getControlTableFile();

		//Open admin side of pas and navigate to administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());

		//Uploading of VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, configExcelName);

		//Verify that the proper number or rows were added in the Control table; one row will be added
		assertThat(UploadToVINTableTab.LBL_UPLOAD_SUCCESSFUl).valueContains(added);

		//Uploading of VIN table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, uploadExcelName);

		//Verify that the proper number or rows were added in the VIN table; one row will be added
		assertThat(UploadToVINTableTab.LBL_UPLOAD_SUCCESSFUl).valueContains(added);
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
		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);

		/*
		 * Automated Renewal R-Expiration Date
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		pas2716_CommonSteps(NEW_VIN, pas2716VinTableFileName, pas2716ControlTableFileName, policyNumber, policyExpirationDate);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);
		/*
		 * Automated Renewal R-45
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		pas2716_CommonSteps(NEW_VIN, pas2716VinTableFileName, pas2716ControlTableFileName, policyNumber, policyExpirationDate.minusDays(45));
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);
		/*
		 * Automated Renewal R-35
		 */
		//1. Retrieve active policy (VIN matched)
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		pas2716_CommonSteps(NEW_VIN, pas2716VinTableFileName, pas2716ControlTableFileName, policyNumber, policyExpirationDate.minusDays(35));
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
		String vinTableFile = "backdatedVinTable_UT_SS.xlsx" ;
		String controlTableFile =  "backdatedControlTable_UT_SS.xlsx";
		TestData testData = getTestDataSinceMembershipVin(NEW_VIN);
		// 1. Create Auto policy with 2 vehicles
		String policyNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		vinMethods.uploadFiles(controlTableFile,vinTableFile);

		// 2. Renewal term is inforce) R-35
		// According to controlTableFile
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		new BillingAccount().acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(1));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		openAndSearchPolicy(policyNumber);
		// 4. Initiate Prior Term (backdated) endorsement with effective date in previous term (for example R-5)
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusMonths(1));
		openAndSearchPolicy(policyNumber);

		policy.createPriorTermEndorsement(getPolicyTD("Endorsement", "TestData_Plus10Day")
				.adjust(TestData.makeKeyPath("EndorsementActionTab", "Endorsement Date"),
						policyExpirationDate.minusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		new GeneralTab().getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AUTHORIZED_BY.getLabel(),TextBox.class).setValue("Me");
		// 5. Add new vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		TestData twoVehicles = addSecondVehicle(NEW_VIN,testData)
				.adjust("DriverActivityReportsTab", DataProviderFactory.emptyData());

		policy.getDefaultView().fillFromTo(twoVehicles,VehicleTab.class,DocumentsAndBindTab.class);
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

	public void killChromeDrivers() throws IOException {
		String taskkill = "TASKKILL /F /IM chromedriver.exe /T";
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(taskkill);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		String configNames = "('SYMBOL_2000_SS_TEST','BACKDATED_SS','BACKDATED2_SS')";
		DatabaseCleanHelper.cleanVinUploadTables(configNames, getState());
	}
}
