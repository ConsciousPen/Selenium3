package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.product.DatabaseCleanHelper.UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE_FORMTYPE;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.MsrpQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestMSRPRefreshTemplate implements MsrpQueries{
	PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refreshed from VIN table VIN partial match
	 * 1. Create Auto quote: VIN doesn't match, Year/Make/Model/Series/Body Style prefilled with not 'Other', comp/coll symbols are found in VIN table
	 * 2. Calculate premium and validate comp/coll symbols
	 * 3. Add new Active VIN version to DB, Adjust values in Vehiclerefdatavin table
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_PartialMatch(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		String vinTable = vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get());

		String vehYear = "2015";
		String vehMake = "VOLKSWAGEN";
		String vehModel = "GOLF";
		String vehSeries = "GOLF";
		String vehBodyStyle = "HATCHBACK 4 DOOR";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VALUE.getLabel()), "50000").resolveLinks();

		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Values from VIN comp and coll symbol in excel sheet
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo("43");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo("33");

		});

		String compSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
		String collSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		adminApp().open();
		vinMethods.uploadFiles(vinTable);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("43");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo(compSymbol);

			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("33");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo(collSymbol);

		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Create Auto quote: VIN doesn't match, type PPA/Regular
	 * 2. Calculate premium and validate comp/coll symbols
	 * 3. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeRegular(@Optional("") String state) {
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(vehicleTab.getMetaKey()).mask("VIN");
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
		testData.getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver").resolveLinks();

		vehicleTypeRegular(testData);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (NOT PPA/Regular)
	 * 1. Create Auto quote: VIN doesn't match, type NOT PPA/Regular
	 * 2. Calculate premium and validate comp/coll symbols
	 * 3. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotRegular(@Optional("") String state) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTabMotorHome).resolveLinks();
		testData.getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver").resolveLinks();

		vehicleTypeNotRegular(testData);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Auto Policy created: VIN doesn't match, type PPA/Regular
	 * 2. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 3. Generate and rate renewal image
	 * 4. Open generated renewal image
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeRegular(@Optional("") String state) {
		// Some kind of random vin number
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(vehicleTab.getMetaKey()).adjust("VIN", "6FDEU15H7KL055795").resolveLinks();
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();

		renewalVehicleTypeRegular(testData);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Auto Policy created: VIN doesn't match, type NOT PPA/Regular
	 * 2. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 3. Generate and rate renewal image
	 * 4. Open generated renewal image
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotRegular(@Optional("") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());
		testData.adjust("AssignmentTab", getTwoAssignmentsTestData()).resolveLinks();

		renewalVehicleTypeNotRegular(testData);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN DOES match on NB VIN DOESN'T match on Renewal
	 * 1. Auto Policy created: VIN matches to DB on NB, but doesn't match on Renewal
	 * 2. Generate and rate renewal image
	 * 3. Open generated renewal image
	 * 4. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVINDoesMatchNBandNoMatchOn(@Optional("") String state) {
		String vinNumber = "7MSRP15H5V1011111";
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		renewalVINDoesMatchNBandNoMatchOn(testData);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh Select is changed to Choice VIN Doesn't match to DB
	 * 1. Create CA Select quote: VIN DOES match for Select but DOESN'T exist for Choice
	 * 2. Calculate premium and validate comp/coll symbols(1)
	 * 3. Change product from Select to Choice
	 * 4. Enter Stat Code/Stated Amount in vehicle page
	 * 5. Navigate to P&C page and validate comp/coll symbols(2)
	 *
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_vinDoesNotMatchDB(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		String vinNumber = "7MSRP15H5V1011111";
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		adminApp().open();
		vinMethods.uploadFiles(vinMethods.getSpecificUploadFile(VinUploadHelper.UploadFilesTypes.ADDED_VIN.get()));

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValueByRegex("No Coverage.*");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).setValueByRegex("No Coverage.*");

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).setValue("CA Choice");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VALUE).setValue("15000");
		PremiumAndCoveragesTab.calculatePremium();

		pas730_commonChecks(compSymbol, collSymbol);
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
		// DELETE_VEHICLEREFDATAVINCONTROL_BY_VERSION_VEHICLETYPE
		DBService.get().executeUpdate(String.format(DELETE_VEHICLEREFDATAVINCONTROL_BY_VERSION_VEHICLETYPE, vehicleTypeRegular, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_SELECT));
		// DELETE new VEHICLEREFDATAVINCONTROL version
		DBService.get().executeUpdate(String
				.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_SELECT, getState()));
		DBService.get().executeUpdate(String
				.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_SELECT, getState()));
		// Reset to the default state  MSRP_2000
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE_FORMTYPE, getState(), formTypeSelect));
		// DELETE new MSRP version pas730_VehicleTypeRegular
		DBService.get().executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_SELECT, EXPECTED_MSRP_KEY, vehicleTypeRegular));
		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		DBService.get()
				.executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_SELECT, EXPECTED_MSRP_KEY, vehicleTypeMotorHome));
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CA_SELECT')", getState());
	}
}
