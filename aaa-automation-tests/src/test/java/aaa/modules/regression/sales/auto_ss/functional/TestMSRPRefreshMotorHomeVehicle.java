package aaa.modules.regression.sales.auto_ss.functional;

import java.time.LocalDateTime;
import java.util.Map;
import org.testng.annotations.*;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.VinUploadAutoSSHelper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMSRPRefreshMotorHomeVehicle extends VinUploadAutoSSHelper {

	private VehicleTab vehicleTab = new VehicleTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotPPA(@Optional("UT") String state) {
		// Build override Informational Notice dialog
		TestData testDataInformationNoticeDialog =
				DataProviderFactory.emptyData().adjust(AutoSSMetaData.VehicleTab.InformationNoticeDialog.BTN_OK.getLabel(), "click");
		// Build MSRP Vehicle
		TestData testDataVehicleTabMotorHome = DataProviderFactory.emptyData()
				.adjust(AutoSSMetaData.VehicleTab.TYPE.getLabel(), "Motor Home")
				.adjust("InformationNoticeDialog", testDataInformationNoticeDialog)
				.adjust(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), "index=2")
				.adjust(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2018")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "Other Make")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Other Model")
				.adjust(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "10000");

		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTabMotorHome).resolveLinks();

		testData.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addMotorHomeVehicleToDB_AutoSS();

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);

		PremiumAndCoveragesTab.buttonSaveAndExit.click();
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotPPA(@Optional("") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());

		String quoteNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);

		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		addMotorHomeVehicleToDB_AutoSS();

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

		PremiumAndCoveragesTab.buttonSaveAndExit.click();
	}

	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/``ingfiles/vinUploadFiles.
	 */
	@AfterTest(alwaysRun = true)
	protected void resetMSRPTables() {
		resetMsrpHomeVehHelper();
	}

	@AfterClass(alwaysRun = true)
	protected void resetVinUploadTables() {
		// pas730_PartialMatch clean
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_SS_TEST')", getState());
	}

	@AfterSuite(alwaysRun = true)
	protected void resetVinControlTable() {
		// Reset to the default state  MSRP_2000
		resetDefaultMSRPVersionValuesVinControlTable();
	}
}
