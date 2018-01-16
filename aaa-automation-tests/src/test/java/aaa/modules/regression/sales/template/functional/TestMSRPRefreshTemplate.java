package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.queries.MsrpQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;

public class TestMSRPRefreshTemplate extends CommonTemplateMethods implements MsrpQueries {
	protected final String INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION =
			"Insert into VEHICLEREFDATAVINCONTROL (ID,PRODUCTCD,FORMTYPE,STATECD,VERSION,EFFECTIVEDATE,EXPIRATIONDATE,MSRP_VERSION) values"
					+ "(%1$d,'%2$s','%3$s','%4$s','%5$s','%6$d','%7$d','%8$s')";
	protected final String DELETE_VEHICLEREFDATAVINCONTROL_BY_VERSION_VEHICLETYPE =
			"DELETE from MSRPCompCollCONTROL WHERE VEHICLETYPE = '%1$s' AND MSRPVERSION = '%2$s'";

	protected static final int EXPECTED_MSRP_KEY = 4;

	protected String vehicleTypeMotorHome = "Motor";
	protected String vehicleTypeRegular = "Regular";
	protected String formTypeSelect = "SELECT";
	protected String formTypeChoice = "CHOICE";
	protected String productTypeAAACSA = "AAA_CSA";

	protected VehicleTab vehicleTab = new VehicleTab();
	protected PurchaseTab purchaseTab = new PurchaseTab();
	protected MembershipTab membershipTab = new MembershipTab();
	protected VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(getPolicyType());

	protected void partialMatch() {
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

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
		vinMethods.uploadFiles(vinTableFile);

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

	protected void vehicleTypeRegular(TestData testData) {

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			addRegularVehicleToDBSelect();
		} else {
			addRegularVehicleToDBChoice();
		}

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	protected void vehicleTypeNotRegular(TestData testData) {

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			addMotorHomeVehicleToDBSelect();
		} else {
			addMotorHomeVehicleToDBChoice();
		}

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);
	}

	protected void renewalVehicleTypeRegular(TestData testData) {


		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			addRegularVehicleToDBSelect();
		} else {
			addRegularVehicleToDBChoice();
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
	}

	protected void renewalVehicleTypeNotRegular(TestData testData) {

		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			addMotorHomeVehicleToDBSelect();
		} else {
			addMotorHomeVehicleToDBChoice();
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);
	}

	protected void renewalVINDoesMatchNBandNoMatchOn(TestData testData) {

		// Vin control ta   ble has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		vinMethods.uploadFiles(vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get()));

		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");
		// Preconditions to to vin is not match
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CA_SELECT')", getState());
		} else {
			DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CHOICE_T')", getState());
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

	}

	/* ############ HELPERS ############ */

	protected TestData getTwoAdditionalInterests(TestData testData) {
		List<TestData> vehicleInformation = new ArrayList<>();
		vehicleInformation.add(testData.getTestData("DocumentsAndBindTab").getTestDataList("VehicleInformation").get(0));
		vehicleInformation.add(testData.getTestData("DocumentsAndBindTab").getTestDataList("VehicleInformation").get(0));

		return testData.getTestData("DocumentsAndBindTab").adjust("VehicleInformation", vehicleInformation);
	}

	protected TestData getMSRPTestDataTwoVehicles(TestData testData) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData secondVehicle = new SimpleDataProvider().adjust(getPolicyTD().getTestData(new VehicleTab().getMetaKey()));
		//secondVehicle.adjust( AutoCaMetaData.VehicleTab.ARE_THERE_ANY_ADDITIONAL_INTERESTS.getLabel(), "No");

		// Build VehicleTab + two vehicles
		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testDataVehicleTabMotorHome);
		listVehicleTab.add(secondVehicle);

		return testData.adjust(new VehicleTab().getMetaKey(), listVehicleTab);
	}

	protected TestData getVehicleMotorHomeTestData() {
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

	private Map<String, String> getPolicyInfoByNumber(String quoteNumber) {
		return DBService.get().getRow(
				String.format(
						"Select ps.policynumber, B.Vehidentificationno, R.Vinmatched, R.Vinmatchedind, b.vehtypecd, i.compsymbol, i.collsymbol, i.stat, i.biSymbol, i.pdsymbol, i.umsymbol, i.mpsymbol, I.*\n"
								+ "From Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd Where R.Ratinginfo_Id = I.Id And B.Id = R.Baseinfo_Id And\n"
								+ "ps.policydetail_id = pd.id and pd.id = r.policydetail_id and policynumber = '%s'", quoteNumber));
	}

	private void addMotorHomeVehicleToDBSelect() {
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20150101, getState(), "MSRP_2000", formTypeSelect));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeSelect, getState(), "SYMBOL_2000", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_SELECT));

		// Add New  msrp version
		DBService.get()
				.executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, vehicleTypeMotorHome, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_SELECT, EXPECTED_MSRP_KEY));
	}

	private void addRegularVehicleToDBSelect() {
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20150101, getState(), "MSRP_2000", formTypeSelect));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeSelect, getState(), "SYMBOL_2000", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_SELECT));

		// Add New  msrp version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, vehicleTypeRegular, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_SELECT, EXPECTED_MSRP_KEY));
	}

	private void addMotorHomeVehicleToDBChoice() {
		// Expire MSRP_2000 for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20150101, getState(), "MSRP_2000_CHOICE", formTypeChoice));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeChoice, getState(), "SYMBOL_2000_CHOICE", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE));

		// Update needed msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 2015, 2011, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, vehicleTypeMotorHome, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE, 44));
	}

	private void addRegularVehicleToDBChoice() {
		// Expire MSRP_2000 for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20150101, getState(), "MSRP_2000_CHOICE", formTypeChoice));

		// Add new VEHICLEREFDATAVINCONTROL version
		int getUniqId = Integer.parseInt(DBService.get().getColumn(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID).get(0)) + 1;

		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeChoice, getState(), "SYMBOL_2000_CHOICE", 20150102, 20500102, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE));

		// Update msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 2015, 2011, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2016, 9999, vehicleTypeRegular, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, 44));
	}

	private void findQuoteAndOpenRenewal(String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
	}

	protected String getCompSymbolFromVRD() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
	}

	protected String getCollSymbolFromVRD() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
	}

	protected void pas730_commonChecks(String compSymbol, String collSymbol) {
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo(compSymbol);
			softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo(collSymbol);
			softly.assertThat(getCompSymbolFromVRD()).isNotEmpty();
			softly.assertThat(getCollSymbolFromVRD()).isNotEmpty();
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

}
