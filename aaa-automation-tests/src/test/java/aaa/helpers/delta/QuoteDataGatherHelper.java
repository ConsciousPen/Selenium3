package aaa.helpers.delta;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class QuoteDataGatherHelper extends BaseTest {
	
	public static void verifyBestFRScoreNotApplied(TestData td, String scoreInRatingDetails) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		if (getState().equals("CO")) {
			reportsTab.lblAdversalyImpactedMessage.verify.present(false);
		}
		else {
			reportsTab.lblELCMessage.verify.present(false);
		}
	}
	
	public static void verifyBestFRScoreApplied(TestData td, String scoreInRatingDetails, String messageOnReportsTab) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		if (getState().equals("CO")) {
			CustomAssert.assertTrue("Adversely Impacted message is not displaing on Reports Tab",
				reportsTab.lblAdversalyImpactedMessage.getValue().equals(messageOnReportsTab));	
		}
		else {
			CustomAssert.assertTrue("Extraordinary life circumstance message is not displaing on Reports Tab",
				reportsTab.lblELCMessage.getValue().equals(messageOnReportsTab));		
		}		
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
		errorTab.verify.errorPresent(errorCode);
		errorTab.cancel();
	}
	
	public static void fillPropertyInfoTabWithCorrectData(TestData td) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td);
	}

}

