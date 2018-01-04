package aaa.modules.regression.sales.auto_ss.functional.helpers;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestVinUploadHelper extends AutoSSBaseTest {
	protected static VehicleTab vehicleTab = new VehicleTab();
	protected static UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	protected static PurchaseTab purchaseTab = new PurchaseTab();
	private static RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private static VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(PolicyType.AUTO_SS);

	protected void pas2716_CommonSteps(String vinNumber, String vinTableFile, String controlTableFile, String policyNumber, LocalDateTime policyExpirationDate) {
		//2. Generate automated renewal image (in data gather status) according to renewal timeline
		vinMethods.uploadFiles(controlTableFile, vinTableFile);
		moveTimeAndRunRenewJobs(policyExpirationDate);
		//3. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
		mainApp().open();
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
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Year").getCell(2).getValue()).isEqualTo("2005");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Make").getCell(2).getValue()).isEqualTo("UT_SS");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Model").getCell(2).getValue()).isEqualTo("Gt");
		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	protected String createPreconds(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createPolicy(testData);
	}

	protected void openAndSearchActivePolicy(String policyNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		SearchPage.tableSearchResults.getRow("Status","Policy Active").getCell(1).controls.links.getFirst().click();
	}

	protected void moveTimeAndRunRenewJobs(LocalDateTime nextPhaseDate) {
		TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	protected TestData addSecondVehicle(String vinNumber, TestData testData) {
		TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
				.adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), vinNumber);

		List<TestData> listVehicleTab = new ArrayList<>();
		listVehicleTab.add(getPolicyTD().adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vinNumber));
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

		return getTestDataWithMembershipSinceDate(testData);
	}

	/**
	 * Build test data with Membership since date at the rating Detail Reports Tab
	 * @param testData
	 * @return
	 */
	public static TestData getTestDataWithMembershipSinceDate(TestData testData) {
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

	protected void createAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	protected void pas527_533_2716_VehicleTabCommonChecks() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		assertSoftly(softly -> {
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("UT_SS");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("UT_SS");
			// PAS-1487  No Match to Match but Year Doesn't Match
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
			// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).isPresent()).isEqualTo(false);
		});
	}

	protected void createAndRateRenewal(String policyNumber, LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		PremiumAndCoveragesTab.calculatePremium();
	}

	protected void findAndRateQuote(TestData testData, String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
		policy.getDefaultView().fillFromTo(testData, FormsTab.class, PremiumAndCoveragesTab.class, true);
	}

	protected void pas2453_CommonChecks(ETCSCoreSoftAssertions softly) {
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE.getLabel()).getValue()).isEqualTo("Conversion Van");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("No");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()).getValue()).isEqualTo("AV - Custom Van");
	}
}
