package aaa.modules.bct.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class EndorsementTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_079_Endorsement(@Optional("") String state) {
		mainApp().open();
		IPolicy policy = PolicyType.HOME_SS_HO4.get();
		String policyNumber = getPoliciesByQuery("BCT_ONL_079_Endorsement", "SelectPolicy").get(0);

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

	@Parameters({"state"})
	@Test
	public void BCT_ONL_EmptyEndorsementAutoSS(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_Empty_Endorsement_AAA_SS", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		SearchPage.openPolicy(policyNumber);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryAutoSS"), GeneralTab.class, DocumentsAndBindTab.class, false);
		assertThat(new DocumentsAndBindTab().getDocumentPrintingDetailsAssetList().isVisible()).isTrue();
		new DocumentsAndBindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoSS");
		deletePendingEndorsement(policy);
		policy.endorse().perform(td);
		new GeneralTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getActualPremium());
		PremiumAndCoveragesTab.calculatePremium();
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getActualPremium());
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_EmptyEndorsementHomeSS(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_Empty_Endorsement_Home_SS", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO4.get();
		SearchPage.openPolicy(policyNumber);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
		assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
		new BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseHomeSS");
		deletePendingEndorsement(policy);
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(getTestSpecificTD("TestDataEndorseHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_EmptyEndorsementPUP(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_Empty_Endorsement_PUP", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.PUP.get();
		SearchPage.openPolicy(policyNumber);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryPUP"), PrefillTab.class, aaa.main.modules.policy.pup.defaulttabs.BindTab.class, false);
		new aaa.main.modules.policy.pup.defaulttabs.BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorsePUP");
		deletePendingEndorsement(policy);
		policy.endorse().perform(td);
		policy.dataGather().getView().fillFromTo(getTestSpecificTD("TestDataEndorsePUP"), PrefillTab.class, PremiumAndCoveragesQuoteTab.class, false);
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());
		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_EmptyEndorsementAutoCA(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_Empty_Endorsement_Auto_CA", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_CHOICE.get();
		SearchPage.openPolicy(policyNumber);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryAutoCA"), aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab.class, false);
		assertThat(new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().btnPurchase.isPresent()).isTrue();
		new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoCA");
		deletePendingEndorsement(policy);
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(getTestSpecificTD("TestDataEndorseAutoCA"), aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.class, false);
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
		aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.calculatePremium();
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_EmptyEndorsementHomeCA(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_Empty_Endorsement_Home_CA", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_CA_HO6.get();
		SearchPage.openPolicy(policyNumber);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryHomeCA"), aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.home_ca.defaulttabs.BindTab.class, false);
		assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().btnPurchase.isPresent()).isTrue();
		new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseHomeCA");
		deletePendingEndorsement(policy);
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(getTestSpecificTD("TestDataEndorseHomeCA"), aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.class, false);
		aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	private void deletePendingEndorsement(IPolicy policy) {
		if (PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}
	}
}