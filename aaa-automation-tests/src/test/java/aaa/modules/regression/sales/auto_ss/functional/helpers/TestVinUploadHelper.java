package aaa.modules.regression.sales.auto_ss.functional.helpers;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestVinUploadHelper extends AutoSSBaseTest {
	protected static VehicleTab vehicleTab = new VehicleTab();
	protected static UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	protected static PurchaseTab purchaseTab = new PurchaseTab();
	protected static RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();

	/**
	 * Fills Non existing vehicle + membership since date
	 * @param vin non - existing
	 * @return
	 */
	protected TestData getAdjustedTestData(String vin) {
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), vin);

		testData.adjust(ratingDetailReportsTab.getMetaKey(), getTestDataWithMembershipSinceDate(testData)).resolveLinks();
		return testData;
	}

	/**
	 * Build test data with Membership since date at the rating Detail Reports Tab
	 * @param testData
	 * @return
	 */
	protected TestData getTestDataWithMembershipSinceDate(TestData testData) {
		// Workaround for latest membership changes
		// Start of  Rating DetailReports Tab
		TestData addMemberSinceDialog = new SimpleDataProvider()
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click");
		TestData aaaMembershipReportRow = new SimpleDataProvider()
				.adjust("Action", "Add Member Since")
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), addMemberSinceDialog);
		// Adjust Rating details report tab
		return testData.getTestData(ratingDetailReportsTab.getMetaKey())
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);
	}

	protected void pas527_NewVinAddedCommonVehicleChecks(ETCSCoreSoftAssertions softly) {
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue()).isEqualTo("UT_SS");
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel()).isVisible()).isEqualTo(false);
		// PAS-1487  No Match to Match but Year Doesn't Match
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue()).isEqualTo("2005");
		// PAS-1551 Refresh Unbound/Quote - No Match to Match Flag not Updated
		softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN_MATCHED.getLabel()).getValue()).isEqualTo("Yes");
	}

	public void precondsTestVINUpload(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	protected void createAndRateRenewal(String policyNumber) {
		LocalDateTime policyExpDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpDate));
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
