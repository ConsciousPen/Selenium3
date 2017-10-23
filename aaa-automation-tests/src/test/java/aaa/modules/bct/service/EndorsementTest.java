package aaa.modules.bct.service;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;


public class EndorsementTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_079_Endorsement(@Optional("") String state) {

		IPolicy policy = PolicyType.HOME_SS_HO4.get();
		String policyNumber = getPoliciesByQuery("BCT_ONL_079_Endorsement", "SelectPolicy").get(0);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		if (PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}
		policy.endorse().perform(getTestSpecificTD("TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		String[] labelsAreNotDisplayed = {
				HomeSSMetaData.PropertyInfoTab.COVERAGE_A_DWELLING_LIMIT.getLabel(),
				HomeSSMetaData.PropertyInfoTab.PLUMBING_RENOVATION.getLabel(),
				HomeSSMetaData.PropertyInfoTab.ELECTRICAL_RENOVATION.getLabel(),
				HomeSSMetaData.PropertyInfoTab.ROOF_RENOVATION.getLabel(),
				HomeSSMetaData.PropertyInfoTab.HEATING_COOLING_RENOVATION.getLabel()
		};
		policy.dataGather().getView().getTab(PropertyInfoTab.class).verifyFieldsAreNotDisplayed(labelsAreNotDisplayed);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
}