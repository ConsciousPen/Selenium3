package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.queries.LookupQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.queries.postconditions.TestVinUploadPostConditions;
import aaa.modules.regression.sales.auto_ss.functional.helpers.TestVinUploadHelper;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestVinUploadHelper implements LookupQueries, TestVinUploadPostConditions {

	private VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(getPolicyType());

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_PartialMatch(@Optional("UT") String state) {
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		String vehYear = "2015";
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

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Values from VIN comp and coll symbol in excel sheet
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbol()).isNotEqualTo("44");
			softly.assertThat(getCollSymbol()).isNotEqualTo("35");
		});

		String compSymbol = getCompSymbol();
		String collSymbol = getCollSymbol();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		vinMethods.uploadFiles(vinTableFile);

		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbol()).isEqualTo("44");
			softly.assertThat(getCollSymbol()).isEqualTo("35");
		});

		pas730_commonChecks(compSymbol, collSymbol);

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_VehicleTypePPA(@Optional("UT") String state) {
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(new VehicleTab().getMetaKey()).mask("VIN");
		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), vehicleTab).resolveLinks();

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbol();
		String collSymbol = getCollSymbol();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addPPAVehicleToDB();

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotPPA(@Optional("UT") String state) {
		TestData testDataInformationNoticeDialog =
				DataProviderFactory.emptyData().adjust(AutoSSMetaData.VehicleTab.InformationNoticeDialog.BTN_OK.getLabel(), "click");

		TestData testdataVehicleTabMotorHome = DataProviderFactory.emptyData()
				.adjust(AutoSSMetaData.VehicleTab.TYPE.getLabel(), "Motor Home")
				.adjust("InformationNoticeDialog", testDataInformationNoticeDialog)
				.adjust(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2018")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "Other Make")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Other Model")
				.adjust(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "10000");

		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), testdataVehicleTabMotorHome).resolveLinks();

		testData.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbol();
		String collSymbol = getCollSymbol();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addMotorHomeVehicleToDB();

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypePPA(@Optional("UT") String state) {
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(new VehicleTab().getMetaKey()).adjust("VIN", "5FDEU15H7KL055795").resolveLinks();
		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), vehicleTab).resolveLinks();

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);

		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLPSYMBOL");

		addPPAVehicleToDB();

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotPPA(@Optional("UT") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);

		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLPSYMBOL");

		addMotorHomeVehicleToDB();

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_VINDoesMatchNBandNoMatchOnRenewal (@Optional("UT") String state) {
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		final String vinNumber = "7MSRP15H5V1011111";
		TestData testDataSpecificVin = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vinNumber);

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		vinMethods.uploadFiles(vinTableFile);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		mainApp().open();
		createCustomerIndividual();
		createPolicy(testDataSpecificVin);
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLPSYMBOL");
		// Preconditions to to vin is not match
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_SS_TEST')", getState());

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

	}

	public Map<String, String> getPolicyInfoByNumber(String quoteNumber) {
		return DBService.get().getRow(
				String.format(
						"Select ps.policynumber, B.Vehidentificationno, R.Vinmatched, R.Vinmatchedind, b.vehtypecd, i.compsymbol, i.collsymbol, i.stat, i.biSymbol, i.pdsymbol, i.umsymbol, i.mpsymbol, I.*\n"
								+ "From Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd Where R.Ratinginfo_Id = I.Id And B.Id = R.Baseinfo_Id And\n"
								+ "ps.policydetail_id = pd.id and pd.id = r.policydetail_id and policynumber = '%s'", quoteNumber));
	}

	public TestData getMSRPTestDataTwoVehicles(TestData testData) {
		// Build override Informational Notice dialog
		TestData testDataInformationNoticeDialog =
				DataProviderFactory.emptyData().adjust(AutoSSMetaData.VehicleTab.InformationNoticeDialog.BTN_OK.getLabel(), "click");

		// Build MSRP Vehicle
		TestData testDataVehicleTabMotorHome = new SimpleDataProvider()
				.adjust(AutoSSMetaData.VehicleTab.TYPE.getLabel(), "Motor Home")
				.adjust("InformationNoticeDialog", testDataInformationNoticeDialog)
				.adjust(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "5FDEU15H7KL055795")
				.adjust(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2018")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "Other Make")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Other Model")
				.adjust(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "10000");

		TestData secondVehicle = new SimpleDataProvider().adjust(getPolicyTD().getTestData(vehicleTab.getMetaKey()));

		// Build VehicleTab + two vehicles
		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testDataVehicleTabMotorHome);
		listVehicleTab.add(secondVehicle);

		testData.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		return testData.adjust(vehicleTab.getMetaKey(), listVehicleTab);
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
	@AfterTest(alwaysRun = true)
	protected void vinTablesCleaner() {
		// Reset 'default' msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX, 9999, "MSRP_2000"));
		// Reset to the default state  MSRP_2000
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE, getState()));
		// DELETE new VEHICLEREFDATAVINCONTROL version
		DBService.get().executeUpdate(String.format("DELETE FROM VEHICLEREFDATAVINCONTROL WHERE MSRP_VERSION = '%1$s' AND STATECD = '%2$s'", NEWLY_ADDED_MSRP_VERSION_FOR_PPA_VEH, getState()));
		DBService.get().executeUpdate(String.format("DELETE FROM VEHICLEREFDATAVINCONTROL WHERE MSRP_VERSION = '%1$s' AND STATECD = '%2$s'", NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH, getState()));
		// DELETE new MSRP version pas730_VehicleTypePPA
		DBService.get().executeUpdate(String.format("DELETE from MSRPCompCollCONTROL WHERE MSRPVERSION = '%1$s' AND KEY = %2$d AND VEHICLETYPE = 'PPA'", NEWLY_ADDED_MSRP_VERSION_FOR_PPA_VEH, 4));
		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		DBService.get()
				.executeUpdate(String.format("DELETE from MSRPCompCollCONTROL WHERE MSRPVERSION = '%1$s' AND KEY = %2$d AND VEHICLETYPE = 'Motor'", NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH, 4));
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_SS_TEST')", getState());
		vinMethods.enableVinRefresh(false);
	}
}
