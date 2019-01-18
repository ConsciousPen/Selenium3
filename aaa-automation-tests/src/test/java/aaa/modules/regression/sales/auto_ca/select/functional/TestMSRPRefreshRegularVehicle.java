package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.db.queries.MsrpQueries.CA_SELECT_REGULAR_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.VehicleQueries.REPAIR_COLLCOMP_BY_ID;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.helper.VinUploadCleanUpMethods;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefreshRegularVehicle extends TestMSRPRefreshTemplate{
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	protected String pas730_vinDoesNotMatchChoice = "1MSRP15H1V1011111";
	protected String vinMatchNBandNoMatchOnRenewal = "6MSRP15H8V1011111";

	protected String vinIdCopyWithLowCompMatch = "";
	protected String vinIdCopyWithHighCompMatch = "";
	protected String vinIdOriginalNoCompMatch = "";
	protected String vinIdCopyNoCompMatch = "";

	protected Map<String,String> allNewBusinessValues;
	protected String newBusinessCompNoCompMatch = "";
	protected String newBusinessCollNoCompMatch = "";

	private static final boolean isRegularType = true;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Create Auto quote: VIN doesn't match, type PPA/Regular
	 * 2. Calculate premium and validate comp/coll symbols
	 * 3. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate that comp/coll symbols WERE Changed because Vehicle type PPA/Regular
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_VehicleTypeRegular(@Optional("CA") String state) {
		TestData testData = new TestVINUploadTemplate().getNonExistingVehicleTestData(getPolicyTD(), "");
		// required to match MSRP version which will be added later
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2025");
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.VALUE.getLabel()), "11001");

		vehicleTypeRegular(testData, isRegularType);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Auto Policy created: VIN doesn't match, type PPA/Regular
	 * 2. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 3. Generate and rate renewal image
	 * 4. Open generated renewal image
	 * 5. Navigate to P&C page and validate that comp/coll symbols WERE Changed because Vehicle type PPA/Regular
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeRegular(@Optional("") String state) {
		// Some kind of random vin number
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(vehicleTab.getMetaKey());
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.VIN.getLabel()), "6FDEU15H7KL055795");
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2025");
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.VALUE.getLabel()), "11001");

		renewalVehicleTypeRegular(testData, isRegularType);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN DOES match on NB VIN DOESN'T match on Renewal
	 * 1. Auto Policy created: VIN matches to DB on NB, but doesn't match on Renewal
	 * 2. Generate and rate renewal image
	 * 3. Open generated renewal image
	 * 4. Navigate to P&C page and validate that comp/coll symbols WERE Changed because Vehicle type PPA/Regular
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_MatchOnNewBusinessNoMatchOnRenewal(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinMatchNBandNoMatchOnRenewal).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.MATCH_ON_NEW_BUSINESS_NO_MATCH_ON_RENEWAL.get()));

		checkMatchOnNBWithNoMatchOnRenewal(testData, vinMatchNBandNoMatchOnRenewal, isRegularType);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh Select is changed to Choice VIN Doesn't match to DB
	 * 1. Create CA Select quote: VIN DOES match for Select but DOESN'T exist for Choice
	 * 2. Calculate premium and validate comp/coll symbols(1)
	 * 3. Change product from Select to Choice
	 * 4. Enter Stat Code/Stated Amount in vehicle page
	 * 5. Navigate to P&C page and validate that comp/coll symbols WERE Changed because Vehicle type PPA/Regular
	 *
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_vinDoesNotMatchDB(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), pas730_vinDoesNotMatchChoice).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		new UploadToVINTableTab().uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.VIN_DOESNT_MATCH_AFTER_PRODUCT_CHANGE.get()));

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).setValueByRegex("No Coverage.*");

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).setValue("CA Choice");
		premiumAndCoveragesTab.calculatePremium();

		// fill needed fields for rating
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.BODY_STYLE).setValue("SUV");

		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VALUE).setValue("150000");
		premiumAndCoveragesTab.calculatePremium();

		CompCollSymbolChecks_pas730(compSymbol, collSymbol, isRegularType);

		premiumAndCoveragesTab.saveAndExit();
	}

	/**
	 @author Chris Johns / Viktor Petrenko
	 @scenario Test Distinct Match with NO VIN Entered
	 1. Create Auto Policy where no VIN is entered and the user manually selects year/Make/Model/Series/Body Style. Ensure the Dropdown selections match to only one VIN in VIN Table
	 2. After Policy Creation Delete the saved VIN Stub From DB (   PAS-12881 DONE  now stores vin stub on all quotes)
	 3. Initiate a Renewal Entry for the policy to initiate a renewal refresh
	 4. Calculate premium on the renewal image
	 5. Verify the VIN Stub is saved in the DB for the policy
	 @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal(@Optional("CA") String state) {
		verifyStoreStubRenewal();
	}

	/**
	 @author Chris Johns
	 @scenario Multiple Matches for manual selections, with a VIN entered (VIN returns no match/junk vin) - COMP SYMBOL MATCH
	 1. Create Auto Policy where no VIN is entered and the user manually selects year/Make/Model/Series/Body Style. Ensure the Dropdown selections match multiple VINs in VIN Table
	 2. After Policy Creation Delete the saved VIN Stub From DB (   PAS-12881 DONE  now stores vin stub on all quotes)
	 3. Initiate a Renewal Entry for the policy to initiate a renewal refresh
	 4. Calculate premium on the renewal image
	 5. Verify the CORRECT (VIN Stub will be selected if the COMP symbol matches the COMP symbol used at NB) Stub is saved in the DB for the policy
	 @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_COMP(@Optional("CA") String state) {
		String vehYear = "2018";
		String vehMake = "SUBARU";
		String vehModel = "WRX";
		String vehSeries = "WRX STI";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = openAppAndCreatePolicy(testData);
		String newBusinessCurrentVinBeforeNull = DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, policyNumber)).get();
		String sqlVinCompMatch =  newBusinessCurrentVinBeforeNull.replace("&","%") + "%";
		assertThat(newBusinessCurrentVinBeforeNull).isNotNull().isNotEmpty();

		log.info("Current Vin Stub # is : {}", newBusinessCurrentVinBeforeNull);
		//2. Clear the Current VIN Stub Stored at NB
		assertThat(DBService.get().executeUpdate(String.format(VehicleQueries.NULL_SPECIFIC_POLICY_STUB,newBusinessCurrentVinBeforeNull))).isGreaterThan(0);

		Map<String,String> allNewBusinessValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String newBusinessComp = allNewBusinessValues.get("COMPSYMBOL");
		String newBusinessColl = allNewBusinessValues.get("COLLSYMBOL");
		assertThat(allNewBusinessValues.get("CURRENTVIN")).isNullOrEmpty();

		log.info("New business compsymbol: {}, and collsymbol: {}", newBusinessComp, newBusinessColl);

		String getVehicleRefDataVinMaxId = "SELECT MAX(id) + 1 as id FROM VEHICLEREFDATAVIN";

		// Create VIN Entry with smaller COMP symbol then original
		vinIdCopyWithLowCompMatch = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_VIN,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinIdCopyWithLowCompMatch,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, 35, 35, vinIdCopyWithLowCompMatch,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_VIN, newBusinessCurrentVinBeforeNull.substring(0,newBusinessCurrentVinBeforeNull.length()-1)+"K", newBusinessCurrentVinBeforeNull, 35));

		// Create VIN Entry with Larger COMP symbol then original
		vinIdCopyWithHighCompMatch = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_VIN,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinIdCopyWithHighCompMatch,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, 55, 55, vinIdCopyWithHighCompMatch,sqlVinCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_VIN, newBusinessCurrentVinBeforeNull.substring(0,newBusinessCurrentVinBeforeNull.length()-1)+"L", newBusinessCurrentVinBeforeNull, 55));

		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		Map<String,String> allAutoRenewalVersionValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String autoRenewalVersionComp = allAutoRenewalVersionValues.get("COMPSYMBOL");
		String autoRenewalVersionColl = allAutoRenewalVersionValues.get("COLLSYMBOL");
		String autoRenewalVersionCurrentVin = allAutoRenewalVersionValues.get("CURRENTVIN");

		log.info("New business compsymbol: {}, and collsymbol: {}", autoRenewalVersionComp, autoRenewalVersionColl);

		assertThat(autoRenewalVersionComp).isEqualTo(newBusinessComp);
		assertThat(autoRenewalVersionColl).isEqualTo(newBusinessColl);
		assertThat(autoRenewalVersionCurrentVin).isEqualTo(newBusinessCurrentVinBeforeNull);
	}

	/**
	 @author Chris Johns
	 @scenario Multiple Matches for manual selections, with a VIN entered (VIN returns no match/junk vin) - NO COMP SYMBOL MATCH
	 1. Create Auto Policy where no VIN is entered and the user manually selects year/Make/Model/Series/Body Style. Ensure the Dropdown selections match multiple VINs in VIN Table
	 2. After Policy Creation Delete the saved VIN Stub From DB (  PAS-12881 DONE  now stores vin stub on all quotes)
	 3. Initiate a Renewal Entry for the policy to initiate a renewal refresh
	 4. Calculate premium on the renewal image
	 5. Verify that ANY VIN Stub is chosen//TRY to see which row it chooses
	 @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_NO_COMP_MATCH(@Optional("CA") String state) {

		String vehYear = "2018";
		String vehMake = "JAGUAR";
		String vehModel = "XF";
		String vehSeries = "XF S";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = openAppAndCreatePolicy(testData);
		String newBusinessCurrentVinBeforeNull = DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, policyNumber)).get();
		String sqlNoCompMatchVin =  newBusinessCurrentVinBeforeNull.replace("&","%") + "%";
		vinIdCopyNoCompMatch =  DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_ID_BY_VIN_VERSION, sqlNoCompMatchVin, DefaultVinVersions.DefaultVersions.CaliforniaSelect.get())).get();
		assertThat(newBusinessCurrentVinBeforeNull).isNotNull().isNotEmpty();

		log.info("Curren Vin # is : {}", newBusinessCurrentVinBeforeNull);
		//2. Clear the Current VIN Stub Stored at NB and modify the COMP Symbol for the utilized VIN STUB - this will ensure that there is no direct match to a vin stub on renewal
		DBService.get().executeUpdate(String.format(VehicleQueries.NULL_SPECIFIC_POLICY_STUB, newBusinessCurrentVinBeforeNull));

		allNewBusinessValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		newBusinessCompNoCompMatch = allNewBusinessValues.get("COMPSYMBOL");
		newBusinessCollNoCompMatch = allNewBusinessValues.get("COLLSYMBOL");
		assertThat(allNewBusinessValues.get("CURRENTVIN")).isNullOrEmpty();

		log.info("New business compsymbol: {} and collsymbol: {}", newBusinessCompNoCompMatch, newBusinessCollNoCompMatch);

		String getVehicleRefDataVinMaxId = "SELECT MAX(id) + 1 as id FROM VEHICLEREFDATAVIN";
		// Change current comp, coll symbol for existing vin
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, Integer.parseInt(newBusinessCompNoCompMatch)+5, Integer.parseInt(newBusinessCompNoCompMatch)+5, vinIdCopyNoCompMatch,sqlNoCompMatchVin,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));

		// Create VIN Entry with bigger COMP symbol then original
		vinIdCopyNoCompMatch = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_VIN,sqlNoCompMatchVin,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinIdCopyNoCompMatch,sqlNoCompMatchVin,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, Integer.parseInt(newBusinessCompNoCompMatch)+15, Integer.parseInt(newBusinessCompNoCompMatch)+15, vinIdCopyNoCompMatch,sqlNoCompMatchVin,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_VIN, newBusinessCurrentVinBeforeNull.substring(0,newBusinessCurrentVinBeforeNull.length()-1)+"K", newBusinessCurrentVinBeforeNull, Integer.parseInt(newBusinessCompNoCompMatch)+15));
		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		Map<String,String> allAutoRenewalVersionValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String autoRenewalVersionComp = allAutoRenewalVersionValues.get("COMPSYMBOL");
		String autoRenewalVersionColl = allAutoRenewalVersionValues.get("COLLSYMBOL");
		String autoRenewalVersionCurrentVin = allAutoRenewalVersionValues.get("CURRENTVIN");

		log.info("New business compsymbol: {}, and collsymbol: {}", autoRenewalVersionComp, autoRenewalVersionColl);

		assertThat(autoRenewalVersionComp).isNotEqualTo(newBusinessCompNoCompMatch);
		assertThat(autoRenewalVersionColl).isNotEqualTo(newBusinessCollNoCompMatch);
		assertThat(autoRenewalVersionCurrentVin).isNotNull().isNotEmpty();
	}

	@AfterClass(alwaysRun = true)
	protected void resetVinControlTable() {
		List<String> listOfVinNumbers = Arrays.asList(vinMatchNBandNoMatchOnRenewal,pas730_vinDoesNotMatchChoice);
		VinUploadCleanUpMethods.deleteVinByVinNumberAndVersion(listOfVinNumbers,DefaultVinVersions.DefaultVersions.SignatureSeries);

		List<String> listOfVinIds = Arrays.asList(vinIdCopyWithLowCompMatch, vinIdCopyWithHighCompMatch);
		VinUploadCleanUpMethods.deleteVinsById(listOfVinIds);

		if(vinIdOriginalNoCompMatch != null && !vinIdOriginalNoCompMatch.isEmpty()){
			DBService.get().executeUpdate(String.format(REPAIR_COLLCOMP_BY_ID,Integer.parseInt(newBusinessCollNoCompMatch)-5,Integer.parseInt(newBusinessCompNoCompMatch)-5, vinIdOriginalNoCompMatch,DefaultVinVersions.DefaultVersions.CaliforniaSelect.get()));
		}

		pas730_SelectCleanDataBase(CA_SELECT_REGULAR_VEH_MSRP_VERSION, vehicleTypeRegular);

	}
}
