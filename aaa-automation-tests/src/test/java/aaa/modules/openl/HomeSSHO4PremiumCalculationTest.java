package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeSSTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSHO4PremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	@Override
	protected Dollar createAndRateQuote(HomeSSOpenLPolicy openLPolicy) {
		HomeSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());
		boolean isLegacyConvPolicy = false;
		if (TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			renewalEntryData.adjust(TestData.makeKeyPath(new InitiateRenewalEntryActionTab().getMetaKey(), CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel()), openLPolicy.getCappingDetails().getPreviousPolicyPremium().toString());
			
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}
		
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy, isLegacyConvPolicy);
		
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		
		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0492".equals(c.getFormCode()))) {
			TestData formHS0492Data = tdGenerator.getFormHS0492Data(openLPolicy);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.getDefaultView().fillUpTo(formHS0492Data, PremiumsAndCoveragesQuoteTab.class, false);
		}
		
		TestData documentsProofData = tdGenerator.getDocumentsProofData(openLPolicy);
		if (!documentsProofData.equals(DataProviderFactory.emptyData())) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
			policy.getDefaultView().fill(documentsProofData);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}
		
		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		premiumsAndCoveragesQuoteTab.fillTab(quoteRatingData);
		
		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0904".equals(c.getFormCode())) && !TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			premiumsAndCoveragesQuoteTab.submitTab();
			TestData policyIssueData = tdGenerator.getPolicyIssueData(openLPolicy);
			
			policy.getDefaultView().fillUpTo(policyIssueData, PurchaseTab.class, false);
			ErrorTab errorTab = new ErrorTab();
			if (errorTab.isVisible()) {
				errorTab.overrideAllErrors();
				errorTab.submitTab();
			}
			policy.getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), getPolicyTD().getTestData(PurchaseTab.class.getSimpleName())));
			
			TestData endorsementData = tdGenerator.getEndorsementData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
			}
			policy.endorse().performAndFill(endorsementData);
			new PremiumsAndCoveragesQuoteTab().calculatePremium();
		}
		
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
	}
}