package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.db.queries.VehicleQueries.DELETE_VEHICLEREFDATAVIN_BY_ID;
import static aaa.helpers.db.queries.VehicleQueries.REPAIR_COLLCOMP_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.VinUploadAutoSSHelper;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefreshPPAVehicle extends VinUploadAutoSSHelper {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	protected String vinCopyIdWithLowComp = null;
	protected String vinCopyIdWithHighComp = null;
	protected String vinCopyIdNoCompMatch = null;
	protected String vinOriginalIdNoCompMatch = null;
	protected Map<String,String> allNewBusinessValues = null;
	protected String newBusinessCompNoCompMatch;
	protected String newBusinessCollNoCompMatch;

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refreshed from VIN table VIN partial match
	 * 1. Create Auto quote: VIN doesn't match, Year/Make/Model/Series/Body Style prefilled with not 'Other', comp/coll symbols are found in VIN table
	 * 2. Calculate premium and validate comp/coll symbols(
	 * 3. Add new Active VIN version to DB, Adjust values in Vehiclerefdatavin table
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate comp/coll symbols
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730, PAS-12881")
	public void pas730_PartialMatch(@Optional("UT") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vehYear = "2018";
		String vehMake = "VOLKSWAGEN";
		String vehModel = "PASSAT";
		String vehSeries = "PASSAT S";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		premiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Values from VIN comp and coll symbol in excel sheet
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo("55");
			softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo("66");
		});

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.PARTIAL_MATCH.get()));

		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isEqualTo("55");
			softly.assertThat(getCollSymbolFromVRD()).isEqualTo("66");
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		//PAS-12881: Update VIN Y/M/M/S/S to Store VIN Stub (quote): Verify in DB that VIN STUB is stored
		String expectedSTUB = "7MSRP15H&V";
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, quoteNumber)).get()).isNotNull().isEqualTo(expectedSTUB);

		PremiumAndCoveragesTab.buttonSaveAndExit.click();
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_VehicleTypePPA(@Optional("UT") String state) {
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(vehicleTab.getMetaKey()).mask("VIN");
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2025");

		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addPPAVehicleToDBAutoSS();

		findAndRateQuote(testData, quoteNumber);

		compCollSymbolCheck_pas730(compSymbol, collSymbol);

		PremiumAndCoveragesTab.buttonSaveAndExit.click();
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypePPA(@Optional("") String state) {
		// Some kind of random vin number
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(vehicleTab.getMetaKey()).adjust("VIN", "5FDEU15H7KL055795").resolveLinks();
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();

		String quoteNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);

		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		addPPAVehicleToDBAutoSS();

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		compCollSymbolCheck_pas730(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_RenewalVINDoesMatchNBandNoMatchOnRenewal(@Optional("") String state) {

		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get());

		String vinNumber = "7MSRP15H5V1011111";
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vinNumber);

		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

		String quoteNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");
		// Preconditions to to vin is not match
		//todo can be affected by partial match, use different vin number to resolve
		DatabaseCleanHelper.cleanVehicleRefDataVinTable("7MSRP15H5V1011111", "SYMBOL_2000");

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		compCollSymbolCheck_pas730(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

	}

	/**
	 @author Chris Johns/ Viktor Petrenko
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal(@Optional("UT") String state) {

		String vehYear = "2018";
		String vehMake = "VOLKSWAGEN";
		String vehModel = "PASSAT";
		String vehSeries = "PASSAT S";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);
		String newBusinessCurrentVinBeforeNull = DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, policyNumber)).get();
		assertThat(newBusinessCurrentVinBeforeNull).isNotNull().isNotEmpty();
		log.info("Curren Vin # is : {}", newBusinessCurrentVinBeforeNull);
		//2. Clear the Current VIN Stub Stored at NB
		DBService.get().executeUpdate(String.format(VehicleQueries.NULL_SPECIFIC_POLICY_STUB,newBusinessCurrentVinBeforeNull));

		Map<String,String> allNewBusinessValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String newBusinessComp = allNewBusinessValues.get("COMPSYMBOL");
		String newBusinessColl = allNewBusinessValues.get("COLLSYMBOL");
		assertThat(allNewBusinessValues.get("CURRENTVIN")).isNullOrEmpty();

		log.info("New business compsymbol: {}, and collsymbol: {}", newBusinessComp, newBusinessColl);

		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));

		//6. Verify VIN Stub was Stored at renewal in the DB
		Map<String,String> allRenewalVersionValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String renewalVersionComp = allRenewalVersionValues.get("COMPSYMBOL");
		String renewalVersionColl = allRenewalVersionValues.get("COLLSYMBOL");
		String renewalVersionCurrentVin = allRenewalVersionValues.get("CURRENTVIN");

		log.info("New business compsymbol: {}, and collsymbol: {}", renewalVersionComp, renewalVersionColl);

		//4. Go back to MainApp, find created policy, create Renewal image
		//searchForPolicy(policyNumber);

		//5. Open Renewal and calculate premium
		//PolicySummaryPage.buttonRenewals.click();
		//policy.dataGather().start();
		//premiumAndCoveragesTab.calculatePremium();
		//PremiumAndCoveragesTab.buttonSaveAndExit.click();

		assertThat(renewalVersionComp).isEqualTo(newBusinessComp);
		assertThat(renewalVersionColl).isEqualTo(newBusinessColl);
		assertThat(renewalVersionCurrentVin).isEqualTo(newBusinessCurrentVinBeforeNull);
	}

	/**
	 @author Chris Johns/ Viktor Petrenko
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_COMP(@Optional("UT") String state) {
		// Genesis has only one entry in db. Best fit for this test.
		String vehYear = "2018";
		String vehMake = "HYUNDAI";
		String vehModel = "GENESIS";
		String vehSeries = "GENESIS SPORT";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);
		String newBusinessCurrentVinBeforeNull = DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, policyNumber)).get();
		String sqlVinCompMatch =  newBusinessCurrentVinBeforeNull.replace("&","%") + "%";
		assertThat(newBusinessCurrentVinBeforeNull).isNotNull().isNotEmpty();

		log.info("Current Vin Stub # is : {}", newBusinessCurrentVinBeforeNull);
		//2. Clear the Current VIN Stub Stored at NB
		DBService.get().executeUpdate(String.format(VehicleQueries.NULL_SPECIFIC_POLICY_STUB,newBusinessCurrentVinBeforeNull));

		Map<String,String> allNewBusinessValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		String newBusinessComp = allNewBusinessValues.get("COMPSYMBOL");
		String newBusinessColl = allNewBusinessValues.get("COLLSYMBOL");
		assertThat(allNewBusinessValues.get("CURRENTVIN")).isNullOrEmpty();

		log.info("New business compsymbol: {}, and collsymbol: {}", newBusinessComp, newBusinessColl);

		String getVehicleRefDataVinMaxId = "SELECT MAX(id) + 1 as id FROM VEHICLEREFDATAVIN";

		// Create VIN Entry with smaller COMP symbol then original
		vinCopyIdWithLowComp = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_VIN,sqlVinCompMatch,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinCopyIdWithLowComp,sqlVinCompMatch,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, 35, 35, vinCopyIdWithLowComp,sqlVinCompMatch,defaultVersion));
		// Create VIN Entry with Larger COMP symbol then original
		vinCopyIdWithHighComp = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_ID, vinCopyIdWithLowComp,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinCopyIdWithHighComp,sqlVinCompMatch,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, 55, 55, vinCopyIdWithHighComp,sqlVinCompMatch,defaultVersion));
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

		//4. Go back to MainApp, find created policy, create Renewal image
		//searchForPolicy(policyNumber);

		////5. Open Renewal and calculate premium
		//PolicySummaryPage.buttonRenewals.click();
		//policy.dataGather().start();
		//premiumAndCoveragesTab.calculatePremium();
		//PremiumAndCoveragesTab.buttonSaveAndExit.click();

		////6. Verify VIN Stub was Stored at renewal in the DB
		//Map<String,String> allManualPremiumCalculationRenewalVersionValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		//String renewalVersioManualPremiumCalcComp = allManualPremiumCalculationRenewalVersionValues.get("COMPSYMBOL");
		//String renewalVersionManualPremiumCalcColl = allManualPremiumCalculationRenewalVersionValues.get("COLLSYMBOL");
		//String renewalVersionManualPremiumCalcCurrentVin = allManualPremiumCalculationRenewalVersionValues.get("CURRENTVIN");
	}

	/**
	 @author Chris Johns / Viktor Petrenko
	 @scenario Multiple Matches for manual selections, with a VIN entered (VIN returns no match/junk vin) - NO COMP SYMBOL MATCH
	 1. Create Auto Policy where no VIN is entered and the user manually selects year/Make/Model/Series/Body Style. Ensure the Dropdown selections match multiple VINs in VIN Table
	 2. After Policy Creation Delete the saved VIN Stub From DB (  PAS-12881 DONE  now stores vin stub on all quotes)
	 3. Initiate a Renewal Entry for the policy to initiate a renewal refresh
	 4. Calculate premium on the renewal image
	 5. Verify that ANY VIN Stub is chosen//TRY to see which row it chooses
	 @details
	 */

	// нет совпадений с комп.
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_NO_COMP_MATCH(@Optional("UT") String state) {

		String vehYear = "2018";
		String vehMake = "MASERATI";
		String vehModel = "GHIBLI";
		String vehSeries = "GHIBLI S";
		String vehBodyStyle = "SEDAN";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);
		String newBusinessCurrentVinBeforeNull = DBService.get().getValue(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_ON_QUOTE, policyNumber)).get();
		String sqlNoCompMatchVin =  newBusinessCurrentVinBeforeNull.replace("&","%") + "%";
		vinCopyIdNoCompMatch =  DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_ID_BY_VIN_VERSION, sqlNoCompMatchVin, defaultVersion)).get();
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
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, Integer.parseInt(newBusinessCompNoCompMatch)+5, Integer.parseInt(newBusinessCompNoCompMatch)+5, vinCopyIdNoCompMatch,sqlNoCompMatchVin,defaultVersion));

		// Create VIN Entry with bigger COMP symbol then original
		vinCopyIdNoCompMatch = DBService.get().getValue(getVehicleRefDataVinMaxId).get();
		DBService.get().executeUpdate(String.format(VehicleQueries.COPY_EXISTING_ROW_BY_VIN,sqlNoCompMatchVin,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_ID_FOR_COPIED_ROW, vinCopyIdNoCompMatch,sqlNoCompMatchVin,defaultVersion));
		DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_COMP_COLL_SYMBOL, Integer.parseInt(newBusinessCompNoCompMatch)+15, Integer.parseInt(newBusinessCompNoCompMatch)+15, vinCopyIdNoCompMatch,sqlNoCompMatchVin,defaultVersion));

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
		assertThat(autoRenewalVersionCurrentVin).isEqualTo(newBusinessCurrentVinBeforeNull);
		//4. Go back to MainApp, find created policy, create Renewal image
		//searchForPolicy(policyNumber);

		////5. Open Renewal and calculate premium
		//PolicySummaryPage.buttonRenewals.click();
		//policy.dataGather().start();
		////NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//premiumAndCoveragesTab.calculatePremium();
		//PremiumAndCoveragesTab.buttonSaveAndExit.click();

		////6. Verify VIN Stub was Stored at renewal in the DB
		//Map<String,String> allManualPremiumCalculationRenewalVersionValues = DBService.get().getRow(String.format(VehicleQueries.SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE, policyNumber));
		//String renewalVersioManualPremiumCalcComp = allManualPremiumCalculationRenewalVersionValues.get("COMPSYMBOL");
		//String renewalVersionManualPremiumCalcColl = allManualPremiumCalculationRenewalVersionValues.get("COLLSYMBOL");
		//String renewalVersionManualPremiumCalcCurrentVin = allManualPremiumCalculationRenewalVersionValues.get("CURRENTVIN");

		}

	@AfterClass(alwaysRun = true)
	protected void resetVinControlTable() {
		// pas730_PartialMatch clean
		//DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN,"");
		DBService.get().executeUpdate(String.format(DELETE_VEHICLEREFDATAVIN_BY_ID, vinCopyIdWithLowComp));
		DBService.get().executeUpdate(String.format(DELETE_VEHICLEREFDATAVIN_BY_ID, vinCopyIdWithHighComp));
		DBService.get().executeUpdate(String.format(DELETE_VEHICLEREFDATAVIN_BY_ID, vinCopyIdNoCompMatch));
		DBService.get().executeUpdate(String.format(REPAIR_COLLCOMP_BY_ID,Integer.parseInt(newBusinessCollNoCompMatch)-5,Integer.parseInt(newBusinessCompNoCompMatch)-5, vinOriginalIdNoCompMatch));
		resetMsrpPPAVeh();
		// Reset to the default state  MSRP_2000
		//resetDefaultMSRPVersionAtVinControlTable();
	}

}
