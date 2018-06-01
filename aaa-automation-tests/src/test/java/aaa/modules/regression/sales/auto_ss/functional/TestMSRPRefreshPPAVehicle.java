package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterSuite;
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

	private VehicleTab vehicleTab = new VehicleTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

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

		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN8.get());

		String vehYear = "2018";
		String vehMake = "VOLKSWAGEN";
		String vehModel = "GOLF";
		String vehSeries = "GOLF";
		String vehBodyStyle = "HATCHBACK 4 DOOR";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		new PremiumAndCoveragesTab().calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Values from VIN comp and coll symbol in excel sheet
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo("55");
			softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo("66");
		});

		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		adminApp().open();
		vinMethods.uploadVinTable(vinTableFile);

		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isEqualTo("55");
			softly.assertThat(getCollSymbolFromVRD()).isEqualTo("66");
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		//PAS-12881: Update VIN Y/M/M/S/S to Store VIN Stub (quote): Verify in DB that VIN STUB is stored
		String expectedSTUB = "7MSRP15H&V";
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, quoteNumber)).get()).isNotNull().isEqualTo(expectedSTUB);

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

		pas730_commonChecks(compSymbol, collSymbol);

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

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
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
		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

	}

	/**
	 @author Chris Johns
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
		String vehModel = "GOLF";
		String vehSeries = "GOLF";
		String vehBodyStyle = "HATCHBACK 4 DOOR";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);

		//2. Clear the Current VIN Stub Stored at NB
		DBService.get().executeUpdate(VehicleQueries.NULL_POLICY_STUB);
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNullOrEmpty();

		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//4. Go back to MainApp, find created policy, create Renewal image
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		searchForPolicy(policyNumber);

		//5. Open Renewal and calculate premium
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//6. Verify VIN Stub was Stored at renewal in the DB
		String expectedSTUB = "7MSRP15H&V";
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNotNull().isEqualTo(expectedSTUB);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_NO_COMP(@Optional("UT") String state) {

		String vehYear = "2016";
		String vehMake = "TOYOTA";
		String vehModel = "TACOMA";
		String vehSeries = "TACOMA DOUBLE CAB";
		String vehBodyStyle = "PICKUP";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);

		//2. Clear the Current VIN Stub Stored at NB
		DBService.get().executeUpdate(VehicleQueries.NULL_POLICY_STUB);
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNullOrEmpty();

		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//4. Go back to MainApp, find created policy, create Renewal image
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		searchForPolicy(policyNumber);

		//5. Open Renewal and calculate premium
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//6. Verify VIN Stub was Stored at renewal in the DB
		String expectedSTUB = "5TFEZ5CN&G";
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNotNull().isEqualTo(expectedSTUB);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12877")
	public void pas12877_StoreStubRenewal_COMP(@Optional("UT") String state) {

		String vehYear = "2017";
		String vehMake = "TOYOTA";
		String vehModel = "TACOMA";
		String vehSeries = "TACOMA DOUBLE CAB";
		String vehBodyStyle = "PICKUP";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZYKN3DD8E0344466")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a Policy with specific test data
		String policyNumber = createPreconds(testData);

		//2. Clear the Current VIN Stub Stored at NB and modify the COMP Symbol for the utilized VIN STUB - this will ensure that there is no direct match to a vin stub on renewal
		DBService.get().executeUpdate(VehicleQueries.NULL_POLICY_STUB);
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNullOrEmpty();
		DBService.get().executeUpdate(VehicleQueries.EDIT_COMP_VALUE);

		//3. Generate Renewal Image
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//4. Go back to MainApp, find created policy, create Renewal image
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		searchForPolicy(policyNumber);

		//5. Open Renewal and calculate premium
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		//6. Verify VIN Stub was Stored at renewal in the DB
		assertThat(DBService.get().getValue(String.format(VehicleQueries.SELECT_VIN_STUB_ON_QUOTE, policyNumber)).get()).isNullOrEmpty();

		//7. Repair the COMP Symbol of the original VIN
		DBService.get().executeUpdate(VehicleQueries.REPAIR_COMP_VALUE);
	}

	@AfterSuite(alwaysRun = true)
	protected void resetVinControlTable() {
		// pas730_PartialMatch clean
		//DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN,"");
		DBService.get().executeUpdate(VehicleQueries.REPAIR_7MSRP15H_COMP);
		DBService.get().executeUpdate(VehicleQueries.REPAIR_7MSRP15H_COLL);
		resetMsrpPPAVeh();
		// Reset to the default state  MSRP_2000
		//resetDefaultMSRPVersionAtVinControlTable();

	}

}
