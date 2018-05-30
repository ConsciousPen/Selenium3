package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.db.queries.MsrpQueries.CA_SELECT_REGULAR_VEH_MSRP_VERSION;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMSRPRefreshRegularVehicle extends TestMSRPRefreshTemplate{
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	final static String pas730_vinDoesNotMatchDB = "1MSRP15H1V1011111";

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
		partialMatch();
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_VehicleTypeRegular(@Optional("CA") String state) {
		TestData testData = new TestVINUploadTemplate().getNonExistingVehicleTestData(getPolicyTD(), "");
		// required to match MSRP version which will be added later
		testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(),AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2025");

		vehicleTypeRegular(testData);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeRegular(@Optional("") String state) {
		// Some kind of random vin number
		TestData testDataVehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(vehicleTab.getMetaKey()).adjust("VIN", "6FDEU15H7KL055795").resolveLinks();
		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();

		renewalVehicleTypeRegular(testData);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_RenewalVINDoesMatchNBandNoMatchOnRenewal(@Optional("") String state) {
		String vinNumber = "7MSRP15H5V1011111";
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		renewalVINDoesMatchNBandNoMatchOnRenewal(testData);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_vinDoesNotMatchDB(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), pas730_vinDoesNotMatchDB).resolveLinks();
		testData.getTestData(new AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();

		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()));

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValueByRegex("No Coverage.*");
		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).setValueByRegex("No Coverage.*");
		premiumAndCoveragesTab.calculatePremium();

		premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).setValue("CA Choice");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VALUE).setValue("150000");
		premiumAndCoveragesTab.calculatePremium();

		pas730_commonChecks(compSymbol, collSymbol);

		premiumAndCoveragesTab.saveAndExit();
	}

	@AfterSuite(alwaysRun = true)
	protected void resetVinControlTable() {
		pas730_SelectCleanDataBase(CA_SELECT_REGULAR_VEH_MSRP_VERSION, vehicleTypeRegular);
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(pas730_vinDoesNotMatchDB,"SYMBOL_2000");
	}
}
