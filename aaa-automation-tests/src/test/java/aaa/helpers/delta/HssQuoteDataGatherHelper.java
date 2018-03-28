package aaa.helpers.delta;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;

public class HssQuoteDataGatherHelper extends BaseTest {
	
	public static void verifyLOVsOfImmediatePriorCarrier(ArrayList<String> optionsOfImmediatePriorCarrier) {
		GeneralTab generalTab = new GeneralTab();
		ComboBox immediatePriorCarrier = generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER);
		assertThat(immediatePriorCarrier).containsAllOptions(optionsOfImmediatePriorCarrier);
	}

	public static void verifyBestFRScoreNotApplied(TestData td, String scoreInRatingDetails, ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(scoreInRatingDetails);
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		if (getState().equals("CO")) {
			softly.assertThat(reportsTab.lblAdversalyImpactedMessage).isPresent(false);
		}
		else {
			softly.assertThat(reportsTab.lblELCMessage).isPresent(false);
		}
	}
	
	public static void verifyBestFRScoreApplied(TestData td, String scoreInRatingDetails, String messageOnReportsTab, ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(scoreInRatingDetails);
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		if (getState().equals("CO")) {
			softly.assertThat(reportsTab.lblAdversalyImpactedMessage.getValue()).as("Adversely Impacted message is not displaying on Reports Tab").isEqualTo(messageOnReportsTab);
		}
		else {
			softly.assertThat(reportsTab.lblELCMessage.getValue()).as("Extraordinary life circumstance message is not displaying on Reports Tab").isEqualTo(messageOnReportsTab);
		}		
	}

	public static void verifyHailResistanceRatingNotApplied() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING)).isPresent();
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).setValue("No");
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Hail Resistive Rating")).as("Hail Resistive Rating: wrong value in Rating Details").isEqualTo("No");
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Hail zone flag")).as("Hail zone flag: wrong value in Rating Details").isEqualTo("No");
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}
	
	public static void verifyHailResistanceRatingApplied(TestData td_hailResistanceRating) {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td_hailResistanceRating);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).setAnyValueExcept("No");
		String hailResistanceRating = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).getValue();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Hail Resistive Rating")).as("Hail Resistive Rating: wrong value in Rating Details").isEqualTo(hailResistanceRating);
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Hail zone flag")).as("Hail zone flag: wrong value in Rating Details").isEqualTo("Yes");
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}
	
	public static void verifyHailResistanceRatingNotDisplaying() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING)).isPresent(false);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getLabel("Hail Resistive Rating"))
				.as("Hail Resistive Rating is present in Rating Details").isPresent(false);
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getLabel("Hail zone flag"))
				.as("Hail zone flag is present in Rating Details").isPresent(false);
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();	
	}
	
	public static void verifyErrorForIneligibleRoofType(TestData td, ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorsPresent(errorCode);
		errorTab.cancel();
	}
	
	public static void verifyErrorOnBindForPropertyInfoTab(TestData td, ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorsPresent(errorCode);
		errorTab.cancel();
	}
	
	public static void fillPropertyInfoTabWithCorrectData(TestData td) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td);
	}

	
	public static void verifyDaysOfNotice(String daysOfNotice, int days, String err_message1, String err_message2) {
		CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();	
		
		String cancelEffDate_default = DateTimeUtils.getCurrentDateTime().plusDays(days).format(DateTimeUtils.MM_DD_YYYY);
		
		assertThat(cancelNoticeTab.getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE).getValue()).as("'Days of Notice' has wrong value on Cancel Notice tab").isEqualTo(daysOfNotice);
		assertThat(cancelNoticeTab.getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE).getValue()).as("'Cancellation Effective date' has wrong value on Cancel Notice Tab").isEqualTo(cancelEffDate_default);
		
		//cancelNoticeTab.fillTab(td); 
		cancelNoticeTab.getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE).setValue(
				DateTimeUtils.getCurrentDateTime().plusDays(days-1).format(DateTimeUtils.MM_DD_YYYY));
		cancelNoticeTab.verifyFieldHasMessage(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel(), err_message1); 
		
		cancelNoticeTab.getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE).setValue(
				DateTimeUtils.getCurrentDateTime().plusDays(367).format(DateTimeUtils.MM_DD_YYYY));
		cancelNoticeTab.verifyFieldHasMessage(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel(), err_message2); 
	}
	
}






