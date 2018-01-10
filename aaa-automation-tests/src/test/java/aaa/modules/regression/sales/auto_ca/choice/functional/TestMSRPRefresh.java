package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.queries.MsrpQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.queries.postconditions.TestVinUploadPostConditions;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestMSRPRefreshTemplate implements MsrpQueries, TestVinUploadPostConditions {
	private final String INSERT_VEHICLEREFDATAVINCONTROL_VERSION =
			"Insert into VEHICLEREFDATAVINCONTROL (ID,PRODUCTCD,FORMTYPE,STATECD,VERSION,EFFECTIVEDATE,EXPIRATIONDATE,MSRP_VERSION) values"
					+ "(%1$d,'%2$s','%3$s','%4$s','%5$s','%6$d','%7$d','%8$s')";
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_PartialMatch(@Optional("CA") String state) {
		partialMatch();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeRegular(@Optional("CA") String state) {
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(new VehicleTab().getMetaKey()).mask("VIN");
		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), vehicleTab).resolveLinks();

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbol();
		String collSymbol = getCollSymbol();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addRegularVehicleToDBChoice();

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotRegular(@Optional("CA") String state) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), testDataVehicleTabMotorHome).resolveLinks();

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbol();
		String collSymbol = getCollSymbol();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		addMotorHomeVehicleToDBChoice();

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeRegular(@Optional("CA") String state) {
		// Some kind of random vin number
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(new VehicleTab().getMetaKey()).adjust("VIN", "6FDEU15H7KL055795").resolveLinks();
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
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		addRegularVehicleToDBChoice();

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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotRegular(@Optional("CA") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());
		testData.adjust("AssignmentTab", getTwoAssignmentsTestData()).resolveLinks();
		testData.adjust("DocumentsAndBindTab",getTwoAdditionalInterests(testData)).resolveLinks();

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
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		addMotorHomeVehicleToDBChoice();

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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVINDoesMatchNBandNoMatchOn (@Optional("CA") String state) {
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		String vinNumber = "7MSRP15H5V1011111";
		VehicleTab vehicleTab = new VehicleTab();
		TestData testData = getPolicyTD().adjust(testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");
		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		// Vin control ta   ble has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		vinMethods.uploadFiles(vinTableFile);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");
		// Preconditions to to vin is not match
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CHOICE_T')", getState());

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

	}


	public TestData getTwoAdditionalInterests(TestData testData) {
		List<TestData> vehicleInformation = new ArrayList<>();
		vehicleInformation.add(testData.getTestData("DocumentsAndBindTab").getTestDataList("VehicleInformation").get(0));
		vehicleInformation.add(testData.getTestData("DocumentsAndBindTab").getTestDataList("VehicleInformation").get(0));

		return testData.getTestData("DocumentsAndBindTab").adjust("VehicleInformation",vehicleInformation);
	}

	public TestData getMSRPTestDataTwoVehicles(TestData testData) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData secondVehicle = new SimpleDataProvider().adjust(getPolicyTD().getTestData(new VehicleTab().getMetaKey()));
		//secondVehicle.adjust( AutoCaMetaData.VehicleTab.ARE_THERE_ANY_ADDITIONAL_INTERESTS.getLabel(), "No");

		// Build VehicleTab + two vehicles
		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testDataVehicleTabMotorHome);
		listVehicleTab.add(secondVehicle);

		return testData.adjust(new VehicleTab().getMetaKey(), listVehicleTab);
	}

	public TestData getVehicleMotorHomeTestData() {
		TestData validateAddressDialog = new SimpleDataProvider();
		return DataProviderFactory.emptyData()
				.adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Motor Home")
				.adjust(AutoCaMetaData.VehicleTab.YEAR.getLabel(), "2018")
				.adjust(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "index=1")
				.adjust(AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel(), "Other Make")
				.adjust(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Other Model")
				.adjust(AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel(), "OTHER SERIES")
				.adjust(AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), "Other Body Style")
				.adjust(AutoCaMetaData.VehicleTab.VALUE.getLabel(), "10000")
				.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "index=1")
				.adjust(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), "22")
				.adjust(AutoCaMetaData.VehicleTab.SALVAGED.getLabel(), "No")
				.adjust(AutoCaMetaData.VehicleTab.EXISTING_DAMEGE.getLabel(), "index=1")
				.adjust(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), "160000")
				.adjust(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes")
				.adjust(AutoCaMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click")
				.adjust("Validate Address Dialog", validateAddressDialog)
				;
	}

	public Map<String, String> getPolicyInfoByNumber(String quoteNumber) {
		return DBService.get().getRow(
				String.format(
						"Select ps.policynumber, B.Vehidentificationno, R.Vinmatched, R.Vinmatchedind, b.vehtypecd, i.compsymbol, i.collsymbol, i.stat, i.biSymbol, i.pdsymbol, i.umsymbol, i.mpsymbol, I.*\n"
								+ "From Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd Where R.Ratinginfo_Id = I.Id And B.Id = R.Baseinfo_Id And\n"
								+ "ps.policydetail_id = pd.id and pd.id = r.policydetail_id and policynumber = '%s'", quoteNumber));
	}

	public void addMotorHomeVehicleToDBChoice() {
		// Expire MSRP_2000 for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD, 20150101, getState(), "MSRP_2000_CHOICE"));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_VERSION,
				getUniqId, "AAA_CSA", "CHOICE", getState(), "SYMBOL_2000_CHOICE", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE));

		// Update need msrp version
		// Update msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 2015, 2011, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, getMotor(), NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE, 44));
	}

	public void addRegularVehicleToDBChoice() {
		// Expire MSRP_2000 for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD, 20150101, getState(), "MSRP_2000_CHOICE"));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_VERSION,
				getUniqId, "AAA_CSA", "CHOICE", getState(), "SYMBOL_2000_CHOICE", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE));

		// Update msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 2015, 2011, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, getRegular(), NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, 44));
	}

	public String getCompSymbol() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
	}

	public String getCollSymbol() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
	}

	public void pas730_commonChecks(String compSymbol, String collSymbol) {
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbol()).isNotEqualTo(compSymbol);
			softly.assertThat(getCollSymbol()).isNotEqualTo(collSymbol);
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from :
	 * Vehiclerefdatavin,
	 * Vehiclerefdatamodel
	 * VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		// Reset 'default' msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 9999, 2011, 4));
		// Reset to the default state  MSRP_2000
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE, getState()));
		// DELETE new VEHICLEREFDATAVINCONTROL version
		DBService.get().executeUpdate(String
				.format("DELETE FROM VEHICLEREFDATAVINCONTROL WHERE MSRP_VERSION = '%1$s' AND STATECD = '%2$s'", NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, getState()));
		DBService.get().executeUpdate(String
				.format("DELETE FROM VEHICLEREFDATAVINCONTROL WHERE MSRP_VERSION = '%1$s' AND STATECD = '%2$s'", NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE, getState()));
		// DELETE new MSRP version pas730_VehicleTypeRegular
		DBService.get()
				.executeUpdate(String
						.format("DELETE from MSRPCompCollCONTROL WHERE MSRPVERSION = '%1$s' AND KEY = %2$d AND VEHICLETYPE = '%3$s'", NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, 44, getMotor()));

		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		DBService.get()
				.executeUpdate(String
						.format("DELETE from MSRPCompCollCONTROL WHERE MSRPVERSION = '%1$s' AND KEY = %2$d AND VEHICLETYPE = '%3$s'", NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, 44, getRegular()));

		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CHOICE_T')", getState());
		vinMethods.enableVinRefresh(false);
	}

	private String getRegular() {
		return "Regular";
	}

	private String getMotor() {
		return "Motor";
	}
}
