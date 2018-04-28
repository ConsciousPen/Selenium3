package aaa.modules.regression.sales.template.functional;

import static aaa.helpers.db.queries.MsrpQueries.CA_CHOICE_MOTORHOME_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.CA_CHOICE_REGULAR_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.CA_SELECT_MOTORHOME_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.CA_SELECT_REGULAR_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY;
import static aaa.helpers.db.queries.MsrpQueries.INSERT_MSRPCOMPCOLLCONTROL_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_BY_VERSION_VEHICLEYEARMIN;
import static aaa.helpers.db.queries.MsrpQueries.UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_VEHICLEYEARMIN_KEY;
import static aaa.helpers.db.queries.MsrpQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE;
import static aaa.helpers.db.queries.MsrpQueries.getAvailableIdFromVehicleDataVinControl;
import static aaa.helpers.db.queries.VehicleQueries.UPDATE_CHOICE_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION;
import static aaa.helpers.db.queries.VehicleQueries.UPDATE_SELECT_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;

public class TestMSRPRefreshTemplate extends CommonTemplateMethods {

	public static String NEW_VIN = "1FDEU15H7KL055795";

	protected final String INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION =
			"Insert into VEHICLEREFDATAVINCONTROL (ID,PRODUCTCD,FORMTYPE,STATECD,VERSION,EFFECTIVEDATE,EXPIRATIONDATE,MSRP_VERSION) values"
					+ "(%1$d,'%2$s','%3$s','%4$s','%5$s','%6$d','%7$d','%8$s')";
	protected final String DELETE_VEHICLEREFDATAVINCONTROL_BY_VERSION_VEHICLETYPE =
			"DELETE from MSRPCompCollCONTROL WHERE VEHICLETYPE = '%1$s' AND MSRPVERSION = '%2$s'";
	String DELETE_FROM_VEHICLEREFDATAVINCONTROL  = "DELETE FROM VEHICLEREFDATAVINCONTROL WHERE FORMTYPE = '%s' AND VERSION = '%s' AND EFFECTIVEDATE = %d AND EXPIRATIONDATE = %d";

	protected static final int EXPECTED_MSRP_KEY = 4;
	protected static final int COMP_COLL_SYMBOL_KEY = 44;

	protected static int VEHICLYEARMIN_ADDED = 2025;
	protected static int VEHICLYEARMAX_ADDED = 9999;

	protected static int DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMIN_ADDED = 2011;
	protected static int DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMAX_ADDED = 2024;

	int DEFAULT_VEHREFVERSION_EXPIRATIONDATE = 20250101;
	int NEW_VEHREFVERSION_EFFECTIVEDATE = 20250102;
	int NEW_VEHREFVERSION_EXPIRATIONDATE = 20500102;

	protected String vehicleTypeMotorHome = "Motor";
	protected String VEHICLETYPE_Regular = "Regular";
	protected String formTypeSelect = "SELECT";
	protected String formTypeChoice = "CHOICE";
	protected String productTypeAAACSA = "AAA_CSA";

	protected VehicleTab vehicleTab = new VehicleTab();
	protected PurchaseTab purchaseTab = new PurchaseTab();
	protected MembershipTab membershipTab = new MembershipTab();
	protected PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	protected void vehicleTypeRegular(TestData testData) {

		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String compSymbol = getCompSymbolFromVRD();
		String collSymbol = getCollSymbolFromVRD();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			pas730_addRegularVehicleToDBSelect();
		} else {
			pas730_addRegularVehicleToDBChoice();
		}

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);

		premiumAndCoveragesTab.saveAndExit();
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
			pas730_addMotorHomeVehicleToDBSelect();
		} else {
			pas730_addMotorHomeVehicleToDBChoice();
		}

		findAndRateQuote(testData, quoteNumber);

		pas730_commonChecks(compSymbol, collSymbol);

		premiumAndCoveragesTab.saveAndExit();

	}

	protected void renewalVehicleTypeRegular(TestData testData) {
		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			pas730_addRegularVehicleToDBSelect();
		} else {
			pas730_addRegularVehicleToDBChoice();
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

		premiumAndCoveragesTab.saveAndExit();
	}

	protected void renewalVehicleTypeNotRegular(TestData testData) {

		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");

		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			pas730_addMotorHomeVehicleToDBSelect();
		} else {
			pas730_addMotorHomeVehicleToDBChoice();
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

		premiumAndCoveragesTab.saveAndExit();
	}

	protected void renewalVINDoesMatchNBandNoMatchOnRenewal(TestData testData) {
		// Should be added after VinUpload Tests
		String quoteNumber = createPolicyPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		Map<String, String> policyInfoBeforeRenewal = getPolicyInfoByNumber(quoteNumber);
		String compSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COMPSYMBOL");
		String collSymbolBeforeRenewal = policyInfoBeforeRenewal.get("COLLSYMBOL");
		// Preconditions to to vin is not match
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			//todo
			DatabaseCleanHelper.cleanVehicleRefDataVinTable("('SYMBOL_2000_CA_SELECT')", getState());
		} else {
			//todo
			DatabaseCleanHelper.cleanVehicleRefDataVinTable("('SYMBOL_2000_CHOICE_T')", getState());
		}

		// Move time to get refresh
		moveTimeAndRunRenewJobs(policyExpirationDate);

		findQuoteAndOpenRenewal(quoteNumber);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		pas730_commonChecks(compSymbolBeforeRenewal, collSymbolBeforeRenewal);

		premiumAndCoveragesTab.saveAndExit();

	}

	protected void partialMatch() {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

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

		new PremiumAndCoveragesTab().calculatePremium();

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
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()));

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

	protected static TestData getVehicleMotorHomeTestData() {
		TestData validateAddressDialog = new SimpleDataProvider();
		return DataProviderFactory.emptyData()
				.adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Motor Home")
				.adjust(AutoCaMetaData.VehicleTab.YEAR.getLabel(), "2018")
				.adjust(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "OTHER")
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

	private void pas730_addMotorHomeVehicleToDBSelect() {
		// Expire MSRP_2000_SELECT for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20250101, getState(), "MSRP_2000_SELECT", formTypeSelect));

		// Add new VEHICLEREFDATAVINCONTROL version
		BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();
		// Insert Vehicle with needed type
		DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeSelect, getState(), "SYMBOL_2000", NEW_VEHREFVERSION_EFFECTIVEDATE, NEW_VEHREFVERSION_EXPIRATIONDATE, CA_SELECT_MOTORHOME_VEH_MSRP_VERSION));

		// Add New  msrp version
		DBService.get()
				.executeUpdate(String
						.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLYEARMIN_ADDED, VEHICLYEARMAX_ADDED, vehicleTypeMotorHome, CA_SELECT_MOTORHOME_VEH_MSRP_VERSION, EXPECTED_MSRP_KEY));
	}

	private void pas730_addMotorHomeVehicleToDBChoice() {
		// Expire MSRP_2000 for AAA_CA Choice product
		//DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, 20150101, getState(), "MSRP_2000_CHOICE", formTypeChoice));

		// Add new VEHICLEREFDATAVINCONTROL version
		BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();

		DBService.get().executeUpdate(String.format(
				INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,getUniqId, productTypeAAACSA, formTypeChoice, getState(), "SYMBOL_2000_CHOICE", 20150102, 20500102, CA_CHOICE_MOTORHOME_VEH_MSRP_VERSION));

		// Update needed msrp version
		DBService.get().executeUpdate(String.format(
				UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_VEHICLEYEARMIN_KEY, DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMAX_ADDED, DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMIN_ADDED, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String.format(
						INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLYEARMIN_ADDED, VEHICLYEARMAX_ADDED, vehicleTypeMotorHome, CA_CHOICE_MOTORHOME_VEH_MSRP_VERSION, COMP_COLL_SYMBOL_KEY));
	}

	private void pas730_addRegularVehicleToDBSelect() {
		// Expire MSRP_2000_SELECT for AAA_CA Choice product
		DBService.get().executeUpdate(String.format(
				UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, DEFAULT_VEHREFVERSION_EXPIRATIONDATE, getState(), "MSRP_2000_SELECT", formTypeSelect));

		// Add new VEHICLEREFDATAVINCONTROL version
		BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();

		DBService.get().executeUpdate(String.format(
				INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,getUniqId, productTypeAAACSA, formTypeSelect, getState(), "SYMBOL_2000", NEW_VEHREFVERSION_EFFECTIVEDATE, NEW_VEHREFVERSION_EXPIRATIONDATE, CA_SELECT_REGULAR_VEH_MSRP_VERSION));

		// Add New  msrp version
		DBService.get().executeUpdate(String.format(
				INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLYEARMIN_ADDED, VEHICLYEARMAX_ADDED, VEHICLETYPE_Regular, CA_SELECT_REGULAR_VEH_MSRP_VERSION, EXPECTED_MSRP_KEY));
	}

	private void pas730_addRegularVehicleToDBChoice() {
		// Expire default VEHICLEREFDATAVINCONTROL for MSRP_2000_CHOICE
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE, DEFAULT_VEHREFVERSION_EXPIRATIONDATE, getState(), "MSRP_2000_CHOICE", formTypeChoice));

		// Add new VEHICLEREFDATAVINCONTROL version
		BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();

		DBService. get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_BY_VERSION,
				getUniqId, productTypeAAACSA, formTypeChoice, getState(), "SYMBOL_2000_CHOICE", NEW_VEHREFVERSION_EFFECTIVEDATE, NEW_VEHREFVERSION_EXPIRATIONDATE, CA_CHOICE_REGULAR_VEH_MSRP_VERSION));

		// Expire original msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_VEHICLEYEARMIN_KEY, DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMAX_ADDED, DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMIN_ADDED, 4));
		// Add New  msrp version
		DBService.get().executeUpdate(String
				.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLYEARMIN_ADDED, VEHICLYEARMAX_ADDED, VEHICLETYPE_Regular, CA_CHOICE_REGULAR_VEH_MSRP_VERSION, COMP_COLL_SYMBOL_KEY));
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
			softly.assertThat(getCompSymbolFromVRD()).isNotEmpty();
			softly.assertThat(getCollSymbolFromVRD()).isNotEmpty();
			softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo(compSymbol);
			softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo(collSymbol);
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	/**
	 * Clean after pas730_addRegularVehicleToDBChoice
	 * @param vehicleTypeMSRPVersion
	 * @param vehicleType
	 */

	protected void pas730_ChoiceCleanDataBase(String vehicleTypeMSRPVersion, String vehicleType) {
		// Delete added version from VEHICLE REF DATA VIN
		DBService.get().executeUpdate(String.format(
				DELETE_FROM_VEHICLEREFDATAVINCONTROL,formTypeChoice,"SYMBOL_2000_CHOICE",NEW_VEHREFVERSION_EFFECTIVEDATE,NEW_VEHREFVERSION_EXPIRATIONDATE));
		// Reset default version in VEHICLE REF DATA VIN
		resetChoiceDefaultVEHICLEREFDATAVINCONTROL();
		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		deleteAddedMsrpVersionFormMsrpControlTable(vehicleTypeMSRPVersion, COMP_COLL_SYMBOL_KEY, vehicleType);
		// RESET MSRP default version
		DBService.get().executeUpdate(String.format(
				UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_BY_VERSION_VEHICLEYEARMIN,9999,"MSRP_2000_CHOICE",DEFAULT_MSRPCOMPCOLLCONTROL_VEHICLYEARMIN_ADDED));
	}

	/**
	 * Clean after pas730_addRegularVehicleToDBSelect
	 * @param vehicleTypeMSRPVersion
	 * @param vehicleType
	 */
	protected void pas730_SelectCleanDataBase(String vehicleTypeMSRPVersion, String vehicleType) {
		// Delete added version from VEHICLE REF DATA VIN
		DBService.get().executeUpdate(String.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL,formTypeSelect,"SYMBOL_2000",NEW_VEHREFVERSION_EFFECTIVEDATE,NEW_VEHREFVERSION_EXPIRATIONDATE));
		resetSelectDefaultVEHICLEREFDATAVINCONTROL();
		// Reset default version in VEHICLE REF DATA VIN
		resetDefaultVehicleDataVinVersion(vehicleTypeMSRPVersion);
		// DELETE new MSRP version pas730_VehicleTypeRegular
		deleteAddedMsrpVersionFormMsrpControlTable(vehicleTypeMSRPVersion, EXPECTED_MSRP_KEY, vehicleType);
	}

	private void deleteAddedMsrpVersionFormMsrpControlTable(String vehicleTypeMSRPVersion, int compCollSymbolKey, String vehicleType2) {
		DBService.get().executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, vehicleTypeMSRPVersion, compCollSymbolKey, vehicleType2));
	}

	private void resetDefaultVehicleDataVinVersion(String vehicleTypeMSRPVersion) {
		DBService.get().executeUpdate(String.format("UPDATE VEHICLEREFDATAVINCONTROL SET EFFECTIVEDATE = '20000101',EXPIRATIONDATE = '99999999' WHERE MSRP_VERSION = '%s'",vehicleTypeMSRPVersion));
	}

	// Used in After suite method, cause of cross-test interruptions
	public void resetChoiceDefaultVEHICLEREFDATAVINCONTROL() {
		DBService.get().executeUpdate(UPDATE_CHOICE_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION);
	}

	public void resetSelectDefaultVEHICLEREFDATAVINCONTROL() {
		DBService.get().executeUpdate(UPDATE_SELECT_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION);
	}
}
