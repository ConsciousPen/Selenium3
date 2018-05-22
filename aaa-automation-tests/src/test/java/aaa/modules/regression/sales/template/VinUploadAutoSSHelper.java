package aaa.modules.regression.sales.template;

import static aaa.helpers.db.queries.MsrpQueries.AUTO_SS_PPA_VEH_MSRP_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY;
import static aaa.helpers.db.queries.MsrpQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD;
import static aaa.helpers.db.queries.MsrpQueries.INSERT_MSRPCOMPCOLLCONTROL_VERSION;
import static aaa.helpers.db.queries.MsrpQueries.UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX;
import static aaa.helpers.db.queries.VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;


public class VinUploadAutoSSHelper extends PolicyBaseTest{
	protected static VehicleTab vehicleTab = new VehicleTab();
	protected static UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	protected static PurchaseTab purchaseTab = new PurchaseTab();
	private static RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	protected VinUploadHelper vinMethods;

	String INSERT_VEHICLEREFDATAVINCONTROL_VERSION =
			"Insert into VEHICLEREFDATAVINCONTROL (ID,PRODUCTCD,FORMTYPE,STATECD,VERSION,EFFECTIVEDATE,EXPIRATIONDATE,MSRP_VERSION) values"
					+ "(%1$d,'%2$s',%3$s,'%4$s','%5$s','%6$d','%7$d','%8$s')";

	protected void pas730_commonChecks(String compSymbol, String collSymbol) {
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo(compSymbol);
			softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo(collSymbol);
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	protected void pas2453_CommonChecks(ETCSCoreSoftAssertions softly) {
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("Custom Van");
	}

	protected void pas2716_CommonSteps(String vinNumber, String policyNumber, LocalDateTime policyExpirationDate) {
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		moveTimeAndRunRenewJobs(policyExpirationDate);
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("VIN data has been updated for the following vehicle(s): %s", vinNumber));
		//4. System rates renewal image according to renewal timeline
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//5. Validate vehicle was updated
		pas527_533_2716_VehicleTabCommonChecks();
		//  Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo("2005");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo("TOYOTA");
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo("Gt").as("Row with VALID=Y and oldest Entry Date should be used");
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	protected void pas11659_CommonSteps(String vinNumber, String policyNumber, LocalDateTime timeShiftedDate) {
		//1. Move time to renewal time point and
		moveTimeAndRunRenewJobs(timeShiftedDate);
		//3. Retrieve the policy
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		//3. Get Renewal Date for below comparison
		LocalDateTime renewalDate = PolicySummaryPage.getExpirationDate();
		//5. Initiate a new renewal version
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//  Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		if (timeShiftedDate == renewalDate.minusDays(46) || timeShiftedDate == renewalDate.minusDays(40)|| timeShiftedDate == renewalDate.minusDays(25)) {
			assertSoftly(softly -> {
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isNotEqualTo("2018");
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isNotEqualTo("TOYOTA");
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isNotEqualTo("Gt");
			});
		}
		else if (timeShiftedDate == renewalDate.minusDays(45))	{
			assertSoftly(softly -> {
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).isEqualTo("2007");
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualTo("UT_SS_R45");
				softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).isEqualTo("Gt_R45");
			});
		}
		else log.info("Doing Nothing, for now...");

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}


	protected void pas527_533_2716_VehicleTabCommonChecks() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
		    //Verification of tableVehicleList instead of Vehicle page field (to avoid cache issue)
			softly.assertThat(VehicleTab.tableVehicleList.getRow("Make", "TOYOTA")).exists();
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt").as("Row with VALID=Y and oldest Entry Date should be used");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("UT_SS");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2018");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).isPresent()).isEqualTo(false);
		});
	}

	protected String getCompSymbolFromVRD() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
	}

	protected String getCollSymbolFromVRD() {
		return PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
	}

	protected void createAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	protected void createAndRateRenewal(String policyNumber, LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		searchForPolicy(policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		new PremiumAndCoveragesTab().calculatePremium();
	}

	protected void findAndRateQuote(TestData testData, String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PremiumAndCoveragesTab.class, true);
	}

	protected void moveTimeAndRunRenewJobs(LocalDateTime nextPhaseDate) {
		TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	protected String createPreconds(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createPolicy(testData);
	}

	protected void openAndSearchActivePolicy(String policyNumber) {
		searchForPolicy(policyNumber);
		SearchPage.tableSearchResults.getRow("Status", "Policy Active").getCell(1).controls.links.getFirst().click();
	}

	protected void openAndSearchExpiredPolicy(String policyNumber) {
		searchForPolicy(policyNumber);
		SearchPage.tableSearchResults.getRow("Status", "Policy Expired").getCell(1).controls.links.getFirst().click();
	}

	public void searchForPolicy(String policyNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	protected void findQuoteAndOpenRenewal(String quoteNumber) {
		searchForPolicy(quoteNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
	}

	/* TestData helpers */

	protected TestData addSecondVehicle(String vinNumber, TestData testData) {
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), vinNumber)
				.adjust(AutoSSMetaData.VehicleTab.TYPE.getLabel(), "Private Passenger Auto");

		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testData.getTestData(vehicleTab.getMetaKey()));
		listVehicleTab.add(secondVehicle);
		return testData.adjust(vehicleTab.getMetaKey(), listVehicleTab);
	}

	/**
	 * Fills Non existing vehicle + membership since date
	 * @param vin non - existing
	 * @return
	 */
	protected TestData getTestDataSinceMembershipVin(String vin) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vin);

		return addMembershipSinceDateToTestData(testData);
	}

	/**
	 * Build test data with Membership since date at the rating Detail Reports Tab
	 * @param testData
	 * @return
	 */
	public static TestData addMembershipSinceDateToTestData(TestData testData) {
		// Workaround for latest membership changes
		// Start of  Rating DetailReports Tab
		TestData addMemberSinceDialog = new SimpleDataProvider()
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click");

		TestData aaaMembershipReportRow = new SimpleDataProvider()
				.adjust("Action", "Add Member Since")
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), addMemberSinceDialog);

		TestData ratingDetailsReportTab = testData.getTestData(ratingDetailReportsTab.getMetaKey())
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);

		// Adjust Rating details report tab
		return testData.adjust(ratingDetailReportsTab.getMetaKey(), ratingDetailsReportTab).resolveLinks();
	}

	protected TestData getVehicleMotorHomeTestData() {
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

		return testDataVehicleTabMotorHome;
	}

	public TestData getMSRPTestDataTwoVehicles(TestData testData) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData secondVehicle = new SimpleDataProvider().adjust(getPolicyTD().getTestData(vehicleTab.getMetaKey()));

		testData.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		// Build VehicleTab + two vehicles
		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(testDataVehicleTabMotorHome);
		listVehicleTab.add(secondVehicle);

		return testData.adjust(vehicleTab.getMetaKey(), listVehicleTab);
	}

	/* DB Helpers */

	public Map<String, String> getPolicyInfoByNumber(String quoteNumber) {
		return DBService.get().getRow(
				String.format(
						"Select ps.policynumber, B.Vehidentificationno, R.Vinmatched, R.Vinmatchedind, b.vehtypecd, i.compsymbol, i.collsymbol, i.stat, i.biSymbol, i.pdsymbol, i.umsymbol, i.mpsymbol, I.*\n"
								+ "From Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd Where R.Ratinginfo_Id = I.Id And B.Id = R.Baseinfo_Id And\n"
								+ "ps.policydetail_id = pd.id and pd.id = r.policydetail_id and policynumber = '%s'", quoteNumber));
	}

	protected void addMotorHomeVehicleToDB_AutoSS() {
		//// Expire MSRP_2000 for AAA_SS product
		//DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION, 20150101, getState(), "MSRP_2000"));
//
		//// Add new VEHICLEREFDATAVINCONTROL version
		//BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();
//
		//DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_VERSION,
		//		getUniqId, "AAA_SS", null, getState(), "SYMBOL_2000", 20250102, 20500102, AUTO_SS_PPA_VEH_MSRP_VERSION));

		// Add new MSRP version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2025, 9999, "Motor", AUTO_SS_PPA_VEH_MSRP_VERSION, 4));
	}

	protected void addPPAVehicleToDBAutoSS() {
		//// Expire MSRP_2000 for AAA_SS product
		//DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION, 20150101, getState(), "MSRP_2000"));
//
		//// Add new VEHICLEREFDATAVINCONTROL version
		//BigInteger getUniqId = getAvailableIdFromVehicleDataVinControl();
//
		//DBService.get().executeUpdate(String.format(INSERT_VEHICLEREFDATAVINCONTROL_VERSION,
		//		getUniqId, "AAA_SS", null, getState(), "SYMBOL_2000", 20150102, 20500102, AUTO_SS_PPA_VEH_MSRP_VERSION));

		// Add new MSRP version
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2025, 9999, "PPA", AUTO_SS_PPA_VEH_MSRP_VERSION, 4));
	}

	/* DB cleaning methods */

	public void resetMsrpHomeVehHelper() {
		//resetDefaultMSRPVersionAtVinControlTable();
		// Reset 'default' msrp version
		//resetMsrpVersionMSRPCompCollControlTable();
		// DELETE new VEHICLEREFDATAVINCONTROL version
		//deleteVersionFromVehicleControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION);
		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		deleteAddedMsrpVersionFormMsrpControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION, "Motor");
	}

	public void resetMsrpPPAVeh() {
		// Reset 'default' msrp version
		//resetMsrpVersionMSRPCompCollControlTable();
		// DELETE new VEHICLEREFDATAVINCONTROL version
		//deleteVersionFromVehicleControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION);
		// DELETE new MSRP version pas730_VehicleTypePPA
		deleteAddedMsrpVersionFormMsrpControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION, "PPA");
	}

	private void deleteAddedMsrpVersionFormMsrpControlTable(String autoSsMotorhomeVehMsrpVersion, String motor) {
		DBService.get().executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, autoSsMotorhomeVehMsrpVersion, 4, motor));
	}

	private void deleteVersionFromVehicleControlTable(String vehicleMsrpVersion) {
		DBService.get().executeUpdate(String.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD, vehicleMsrpVersion, getState()));
	}

	private void resetMsrpVersionMSRPCompCollControlTable() {
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX, 9999, "MSRP_2000"));
	}

	// Used in After suite method, cause of cross-test interruptions
	public void resetDefaultMSRPVersionAtVinControlTable() {
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE));
	}

}