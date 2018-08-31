package aaa.modules.bct.service;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class EndorsementTest extends BackwardCompatibilityBaseTest {

	private String date1 = "Date1 isn't specified";
	private String date2 = "Date2 isn't specified";

	/* HOME CA */
	private aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab generalTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.BindTab bindTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.BindTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab reportsTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab();
	private static final String TESTDATA_NAME_INQUIRY_HOME_CA = "TestDataInquiryHomeCA";
	private static final String TESTDATA_NAME_ENDORSE_HOME_CA = "TestDataEndorseHomeCA";

	@Parameters({"state"})
	@Test
	@StateList(states = {NJ})
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
		AssetDescriptor[] labelsAreNotDisplayed = {
				HomeSSMetaData.PropertyInfoTab.COVERAGE_A_DWELLING_LIMIT,
				HomeSSMetaData.PropertyInfoTab.PLUMBING_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.ELECTRICAL_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.ROOF_RENOVATION,
				HomeSSMetaData.PropertyInfoTab.HEATING_COOLING_RENOVATION
		};
		assertThat(policy.dataGather().getView().getTab(PropertyInfoTab.class).getAssetList().getAssets(labelsAreNotDisplayed)).extractingResultOf("isPresent").containsOnly(false);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementAutoSS(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_AAA_SS", date1, date2);
		IPolicy policy = PolicyType.AUTO_SS.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryAutoSS"), GeneralTab.class, DocumentsAndBindTab.class, false);
		assertThat(new DocumentsAndBindTab().getDocumentPrintingDetailsAssetList().isVisible()).isTrue();
		new DocumentsAndBindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoSS");
		policy.endorse().perform(td);
		new GeneralTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getActualPremium());
		new PremiumAndCoveragesTab().calculatePremium();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumAndCoveragesTab.getActualPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSDp3(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Dp3_SS", date1, date2);
		IPolicy policy = PolicyType.HOME_SS_DP3.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
		assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
		new BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseHomeSS");
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, ReportsTab.class, false);
		new ReportsTab().reorderReports();
		policy.dataGather().getView()
				.fillFromTo(td, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo3(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Ho3_SS", date1, date2);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquirySSHo3"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
		assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
		new BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseSSHo3");
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, ReportsTab.class, false);
		new ReportsTab().reorderReports();
		policy.dataGather().getView()
				.fillFromTo(td, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo4(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Ho4_SS", date1, date2);
		IPolicy policy = PolicyType.HOME_SS_HO4.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
		assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
		new BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseHomeSS");
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, ReportsTab.class, false);
		new ReportsTab().reorderReports();
		policy.dataGather().getView()
				.fillFromTo(td, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo6(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Ho6_SS", date1, date2);
		IPolicy policy = PolicyType.HOME_SS_HO6.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
		assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
		new BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseHomeSS");

		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, ReportsTab.class, false);
		new ReportsTab().reorderReports();
		policy.dataGather().getView()
				.fillFromTo(td, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementPUP(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_PUP", date1, date2);
		IPolicy policy = PolicyType.PUP.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryPUP"), PrefillTab.class, aaa.main.modules.policy.pup.defaulttabs.BindTab.class, false);
		new aaa.main.modules.policy.pup.defaulttabs.BindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorsePUP");
		policy.endorse().perform(td);
		policy.dataGather().getView().fillFromTo(td, PrefillTab.class, PremiumAndCoveragesQuoteTab.class, false);
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());
		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState()).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyActualPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_EmptyEndorsementAutoCAChoice(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Auto_CA_Choice", date1, date2);
		IPolicy policy = PolicyType.AUTO_CA_CHOICE.get();
		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryAutoCA"), aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab.class, false);
		assertThat(new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().btnPurchase.isPresent()).isTrue();
		new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoCA");
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.class, false);
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
		new aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab().calculatePremium();
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_EmptyEndorsementAutoCASelect(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPolicy("BCT_Empty_Endorsement_Auto_CA_Select", date1, date2);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		Dollar policyPremium = getPreEndorsementPremium(policy, policyNumber);

		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD("TestDataInquiryAutoCA"), aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab.class, false);
		assertThat(new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().btnPurchase.isPresent()).isTrue();
		new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoCA");
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.class, false);
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
		new aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab().calculatePremium();
		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPolicies")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCADp3(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_DP3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		getToTheBindTabInquiry(policy.get(),TESTDATA_NAME_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);
		assertThat(bindTabHomeCa.btnPurchase.isPresent()).isTrue();
		bindTabHomeCa.cancel();

		proceedEndorsement(policy, TESTDATA_NAME_ENDORSE_HOME_CA , generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		log.info(String.format("Endorsement Premium: '%s'", PropertyQuoteTab.getPolicyTermPremium()));
		assertThat(policyPremium).isEqualTo(PropertyQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPolicies")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo3(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		getToTheBindTabInquiry(policy.get(),TESTDATA_NAME_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);
		assertThat(bindTabHomeCa.btnPurchase.isPresent()).isTrue();
		bindTabHomeCa.cancel();

		proceedEndorsement(policy, TESTDATA_NAME_ENDORSE_HOME_CA , generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		Dollar postEndorsementPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", postEndorsementPremium));
		assertThat(policyPremium).isEqualTo(postEndorsementPremium);
	}

	@Test(dataProvider = "getPolicies")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo4(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO4;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		getToTheBindTabInquiry(policy.get(),TESTDATA_NAME_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);
		assertThat(bindTabHomeCa.btnPurchase.isPresent()).isTrue();
		bindTabHomeCa.cancel();

		proceedEndorsement(policy, TESTDATA_NAME_ENDORSE_HOME_CA , generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		log.info(String.format("Endorsement Premium: '%s'", PropertyQuoteTab.getPolicyTermPremium()));
		assertThat(policyPremium).isEqualTo(PropertyQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPolicies")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo6(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO6;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		getToTheBindTabInquiry(policy.get(),TESTDATA_NAME_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);
		assertThat(bindTabHomeCa.btnPurchase.isPresent()).isTrue();
		bindTabHomeCa.cancel();

		proceedEndorsement(policy, TESTDATA_NAME_ENDORSE_HOME_CA , generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		log.info(String.format("Endorsement Premium: '%s'", PropertyQuoteTab.getPolicyTermPremium()));
		assertThat(policyPremium).isEqualTo(PropertyQuoteTab.getPolicyTermPremium());
	}

	private void proceedEndorsement(PolicyType policy,String testDataName, Tab generalTab, Tab reportsTab, Tab  premiumsAndCoveragesQuoteTab ) {
		TestData testDataEndorseHomeCA = getTestSpecificTD(testDataName);
		policy.get().endorse().perform(testDataEndorseHomeCA);
		policy.get().dataGather().getView()
				.fillFromTo(testDataEndorseHomeCA, generalTab.getClass(), reportsTab.getClass(), false);

		if(policy.getShortName().equalsIgnoreCase(PolicyType.HOME_CA_HO3.getShortName())){
			new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab().reorderReports();
		}

		policy.get().dataGather().getView()
				.fillFromTo(testDataEndorseHomeCA, reportsTab.getClass(),  premiumsAndCoveragesQuoteTab.getClass(), false);
		PropertyQuoteTab.btnCalculatePremium.click();
	}

	private void getToTheBindTabInquiry(IPolicy policy,String testData, Tab generalTab, Tab bindTab) {
		policy.policyInquiry().start();
		policy.policyInquiry().getView()
				.fillFromTo(getTestSpecificTD(testData), generalTab.getClass(), bindTab.getClass(), false);
	}

	@DataProvider(name = "getPolicies", parallel = true)
	public Iterator<Object[]> getPolicyNumbersFromDataBase(Method m, ITestContext context) {
		String state = context.getCurrentXmlTest().getParameter(CustomTestProperties.TEST_USSTATE);
		if(state == null){
			state = PropertyProvider.getProperty(CustomTestProperties.TEST_USSTATE);
		}
		List<String> policyNumbers = getPolicies(m.getName(), date1, date2);
		String finalState = state;
		List<Object[]> data = policyNumbers.stream().map(policy -> new String[] {finalState, policy}).collect(Collectors.toList());
		return data.iterator();
	}



	public List<String> getPolicies(String testName, String date1, String date2) {
		if (!PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE1).isEmpty() &&
				!PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE2).isEmpty()) {
			date1 = PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE1);
			date2 = PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE2);
		}
		return getPoliciesWithDateRangeByQuery(testName, date1, date2);
	}

	private String getPolicy(String testName, String date1, String date2) {
		if (!PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE1).isEmpty() &&
				!PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE2).isEmpty()) {
			date1 = PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE1);
			date2 = PropertyProvider.getProperty(CustomTestProperties.CUSTOM_DATE2);
		}
		return getPoliciesWithDateRangeByQuery(testName, date1, date2).get(0);
	}

}
