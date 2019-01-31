package aaa.modules.regression.sales.template;

import static aaa.helpers.db.queries.MsrpQueries.*;
import static aaa.helpers.db.queries.VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE;
import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsVehicles;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.*;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;

public class VinUploadAutoSSHelper extends PolicyBaseTest {
	protected String defaultVersion = DefaultVinVersions.DefaultVersions.CaliforniaSelect.get();
	protected static VehicleTab vehicleTab = new VehicleTab();
	protected static UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	protected static PurchaseTab purchaseTab = new PurchaseTab();
	private static RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	int AutoSSPPAVehicleMSRPKey = 4;

	protected void verifyAutomatedRenewal(String vinNumber, String policyNumber, LocalDateTime policyExpirationDate) {
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		moveTimeAndRunRenewJobs(policyExpirationDate);
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
//		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("VIN data has been updated for the following vehicle(s): %s", vinNumber));
		//4. System rates renewal image according to renewal timeline
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//5. Validate vehicle was updated
		vehicleTabChecks_527_533_2716();
		//  Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		checkVehicleInfo_pas2716();
	}

	protected void verifyVinRefreshWhenVersionIsNotCurrent(String policyNumber, LocalDateTime timeShiftedDate, ETCSCoreSoftAssertions softly) {
		//1. Move time to renewal time point and
		moveTimeAndRunRenewJobs(timeShiftedDate);
		//2. Retrieve the policy
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//3. Get Renewal Date for below comparison
		LocalDateTime renewalDate = PolicySummaryPage.getExpirationDate();

		//4. Verify VIN Refresh Activity and user Notes Entry
		if (timeShiftedDate.equals(renewalDate.minusDays(35))) {
			NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "has been updated for the following vehicle").getCell("Description")).isPresent();
			log.info("Activities and User Note present");
		} else {
			log.info("Not Checking Activities and User Notes");
		}

		//5. Initiate a new renewal version
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		//6. Validate vehicle information in VRD
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();

		if (Arrays.asList(renewalDate.minusDays(46), renewalDate.minusDays(25)).contains(timeShiftedDate)) {
			log.info("Renewal date is : " + renewalDate);
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).as("Vehicle Make").isNotEqualTo("TOYOTA");
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).as("Vehicle Model").isNotEqualTo("Gt");
		} else if (Arrays.asList(renewalDate.minusDays(40), renewalDate.minusDays(35)).contains(timeShiftedDate)) {
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).as("Vehicle Year").isEqualTo("2018");
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).as("Vehicle Make").isEqualTo("TOYOTA");
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).as("Vehicle Model").isEqualTo("Gt");
				log.info("TEST PASSED at R-35");
		}
		//R-45 triggers a refresh so it is not enough to just check that the Y/M/Mo is NOT  equal to a given value, like the R-45 and R-25 checks
		else if (timeShiftedDate.equals(renewalDate.minusDays(45))) {
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2).getValue()).as("Vehicle Year").isEqualTo("2007");
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).as("Vehicle Make").isEqualTo("UT_SS_R45");
				softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2).getValue()).as("Vehicle Model").isEqualTo("Gt_R45");
		} else {
			log.info("CONDITION TO JUST MAKE RENEWAL PREP or post.");
		}

		PremiumAndCoveragesTab.RatingDetailsView.close();
	}

	protected void checkVehicleInfo_pas2716() {
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertSoftly(softly -> {
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Year").getCell(2)).hasValue("2018");
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2)).hasValue("TOYOTA");
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(2)).as("Row with VALID=Y and oldest Entry Date should be used").hasValue("Gt");
		});
		PremiumAndCoveragesTab.RatingDetailsView.close();
	}

	protected void vehicleTabChecks_527_533_2716() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			//Verification of tableVehicleList instead of Vehicle page field (to avoid cache issue)
			softly.assertThat(VehicleTab.tableVehicleList.getRow("Make", "TOYOTA")).exists();
			//PAS-6576 Update "individual VIN retrieval" logic to use ENTRY DATE and VALID
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL)).as("Row with VALID=Y and oldest Entry Date should be used").hasValue("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE)).hasValue("UT_SS");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR)).hasValue("2018");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL).isPresent()).isEqualTo(false);
		});
	}

	protected void pas12872_VINRefreshOnRenewal(String vinNumber, String vehYear, String vehMake, String vehModel, String vehSeries, String vehBodyStyle, String vinTableFile, String expectedYear, String expectedMake, String expectedModel) {
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		//1. Create a policy with VIN no matched data and save the expiration data
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		//2. Upload vin data with updated Y/M/M/S/S
		adminApp().open();
		new UploadToVINTableTab().uploadVinTable(vinTableFile);
		//3. Generate automated renewal image according to renewal timeline
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		//4. Move time to renewal time point
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		//5. Retrieve the policy
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//6. System rates renewal image according to renewal timeline
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//7. Navigate to Premium and Coverages tab and calculate premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();
		//8. Check for the updated Y/M/M values in View Rating Details table
		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Year").getCell(2)).hasValue(expectedYear);
		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2)).hasValue(expectedMake);
		softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(2)).hasValue(expectedModel);

		PremiumAndCoveragesTab.RatingDetailsView.close();
	}

	protected void pas18969_restrictVehicleRefreshOnRenewal(TestData testData, String vinTableFile) {
		mainApp().open();
		createCustomerIndividual();

		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);
		premiumAndCoveragesTab.submitTab();
		new ErrorTab().overrideAllErrors();
		new ErrorTab().override();
		premiumAndCoveragesTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		DriverActivityReportsTab driverActivityReportTab = new DriverActivityReportsTab();
		driverActivityReportTab.fillTab(testData);
		if (driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isPresent()) {
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
			driverActivityReportTab.submitTab();		}

		new DocumentsAndBindTab().fillTab(testData).submitTab();
		new PurchaseTab().fillTab(testData).submitTab();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		// Upload vin data
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFile);
		// Generate automated renewal image according to renewal timeline
		// Move time to renewal time point
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
		// Retrieve the policy
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		// Navigate to Premium and Coverages tab and calculate premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.RatingDetailsView.open();
	}


	protected void compCollSymbolCheck_pas730(String compSymbol, String collSymbol, boolean isPPAType) {
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertSoftly(softly -> {
			if(isPPAType){
				softly.assertThat(getCompSymbolFromVRD()).isNotEqualTo(compSymbol);
				softly.assertThat(getCollSymbolFromVRD()).isNotEqualTo(collSymbol);
			} else {
				softly.assertThat(getCompSymbolFromVRD()).isEqualTo(compSymbol);
				softly.assertThat(getCollSymbolFromVRD()).isEqualTo(collSymbol);
			}
		});
		PremiumAndCoveragesTab.RatingDetailsView.close();
	}

	protected void verifyVehicleInfo_pas2453(ETCSCoreSoftAssertions softly) {
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE)).hasValue("Conversion Van");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED)).hasValue("No");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE)).hasValue("Custom Van");
	}

	protected String getCompSymbolFromVRD() {
		return tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
	}

	protected String getCollSymbolFromVRD() {
		return tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
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

	/**
	 *
	 * @param policyExpirationDate !!!
	 */
	protected void createProposedRenewal(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	/**
	 *
	 * @param policyExpirationDate !!!
	 */
	protected void createRenewalImage(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	/**
	 *
	 * @param policyExpirationDate !!!
	 */
	protected void createRenewalPreview(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	/**
	 *
	 * @param policyExpirationDate !!!
	 */
	protected void createRenewalOffer(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
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
				//.adjust("InformationNoticeDialog", testDataInformationNoticeDialog)
				.adjust(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "5FDEU15H7KL055795")
				.adjust(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "index=1")
				.adjust(AutoSSMetaData.VehicleTab.YEAR.getLabel(), "2025")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "Other Make")
				.adjust(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "Other Model")
				.adjust(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "10000");

		return testDataVehicleTabMotorHome;
	}

	protected TestData getMSRPTestDataTwoVehicles(TestData testData) {
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

	protected Map<String, String> getPolicyInfoByNumber(String quoteNumber) {
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
		//		getUniqId, "AAA_SS", null, getState(), DefaultVinVersions.DefaultVersions.CaliforniaSelect.get(), 20250102, 20500102, AUTO_SS_PPA_VEH_MSRP_VERSION));

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
		DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, 2025, 9999, "PPA", AUTO_SS_PPA_VEH_MSRP_VERSION, AutoSSPPAVehicleMSRPKey));
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

	public void cleanAfter_PAS730_VehicleTypePPA() {
		// Reset 'default' msrp version
		//resetMsrpVersionMSRPCompCollControlTable();
		// DELETE new VEHICLEREFDATAVINCONTROL version
		//deleteVersionFromVehicleControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION);
		// DELETE new MSRP version pas730_VehicleTypePPA
		deleteAddedMsrpVersionFormMsrpControlTable(AUTO_SS_PPA_VEH_MSRP_VERSION, "PPA");
	}

	private void deleteAddedMsrpVersionFormMsrpControlTable(String autoSsMotorhomeVehMsrpVersion, String vehicleType) {
		DBService.get().executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, autoSsMotorhomeVehMsrpVersion, AutoSSPPAVehicleMSRPKey, vehicleType));
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

	public HashMap<String, String> getPolicySymbols() {
		HashMap<String,String> policySymbols = new HashMap<>(); // in fact it is duplication of symbols from NewVIN_UT_SS.xlsx, New3VIN_UT_SS.xlsx

		policySymbols.put("BI Symbol","BI001");
		policySymbols.put("PD Symbol","PD001");
		policySymbols.put("UM Symbol","UM001");
		policySymbols.put("MP Symbol","MP001");
		return policySymbols;
	}
}
