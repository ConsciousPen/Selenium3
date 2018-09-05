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
import aaa.config.CsaaTestProperties;
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
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class EndorsementTest extends BackwardCompatibilityBaseTest {

	private String date1 = "Date1 isn't specified";
	private String date2 = "Date2 isn't specified";

	private static final String TESTDATA_INQUIRY_HOME_CA = "TestDataInquiryHomeCA";
	private static final String TESTDATA_NAME_ENDORSE_HOME_CA = "TestDataEndorseHomeCA";
	/* HOME CA */
	private aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab generalTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.BindTab bindTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.BindTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab reportsTabHomeCa = new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab();

	/* Auto CA*/
	private aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab generalTabAutoCA = new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab();
	private aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab documentsAndBindTabAutoCA = new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab();
	private aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab premiumAndCoveragesTabAutoCA = new aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab();

	@Parameters({"state"})
	@Test
	@StateList(states = {NJ})
	public void BCT_ONL_079_Endorsement(@Optional("NJ") String state) {
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

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementAutoSS(String state, String policyNumber) {
		PolicyType policy = PolicyType.AUTO_SS;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryAutoSS", new GeneralTab(), new BindTab());

		TestData td = getTestSpecificTD("TestDataEndorseAutoSS");
		policy.get().endorse().perform(td);
		new GeneralTab().fillTab(td);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getActualPremium());

		new PremiumAndCoveragesTab().calculatePremium();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumAndCoveragesTab.getActualPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSDp3(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_SS_DP3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryHomeSS", new GeneralTab(), new BindTab());

		fillEmptyEndorsementHomeSS(policy.get(), getTestSpecificTD("TestDataEndorseHomeSS"));

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo3(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_SS_HO3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquirySSHo3", new GeneralTab(), new BindTab());

		fillEmptyEndorsementHomeSS(policy.get(),  getTestSpecificTD("TestDataEndorseSSHo3"));

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo4(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_SS_HO4;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryHomeSS", new GeneralTab(), new BindTab());

		fillEmptyEndorsementHomeSS(policy.get(), getTestSpecificTD("TestDataEndorseHomeSS"));

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo6(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_SS_HO6;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryHomeSS", new GeneralTab(), new BindTab());

		fillEmptyEndorsementHomeSS(policy.get(), getTestSpecificTD("TestDataEndorseHomeSS"));

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementPUP(String state, String policyNumber) {
		PolicyType policy = PolicyType.PUP;
		TestData td = getTestSpecificTD("TestDataEndorsePUP");

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryPUP", new aaa.main.modules.policy.pup.defaulttabs.PrefillTab(), new aaa.main.modules.policy.pup.defaulttabs.BindTab());

		policy.get().endorse().perform(td);
		policy.get().dataGather().getView().fillFromTo(td, PrefillTab.class, PremiumAndCoveragesQuoteTab.class, false);
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());

		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyActualPremium());
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {CA})
	public void BCT_ONL_EmptyEndorsementAutoCAChoice(String state, String policyNumber) {
		PolicyType policy = PolicyType.AUTO_CA_CHOICE;

		testEmptyEndorsementAutoCA(policyNumber, policy);
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {CA})
	public void BCT_ONL_EmptyEndorsementAutoCASelect(String state, String policyNumber) {
		PolicyType policy = PolicyType.AUTO_CA_SELECT;

		testEmptyEndorsementAutoCA(policyNumber, policy);
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCADp3(String state, String policyNumber, ITestContext context) {
		PolicyType policy = PolicyType.HOME_CA_DP3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy, TESTDATA_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);

		performNonBearingEndorsementHomeCA(policy, generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		Dollar policyTermPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", policyTermPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(policyTermPremium);
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo3(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO3;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy, TESTDATA_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);

		performNonBearingEndorsementHomeCA(policy, generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		Dollar postEndorsementPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", postEndorsementPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(postEndorsementPremium);
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo4(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO4;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy, TESTDATA_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);

		performNonBearingEndorsementHomeCA(policy, generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		Dollar postEndorsementPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", postEndorsementPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(postEndorsementPremium);
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo6(String state, String policyNumber) {
		PolicyType policy = PolicyType.HOME_CA_HO6;

		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy, TESTDATA_INQUIRY_HOME_CA, generalTabHomeCa, bindTabHomeCa);

		performNonBearingEndorsementHomeCA(policy, generalTabHomeCa, reportsTabHomeCa, premiumsAndCoveragesQuoteTabHomeCa);

		Dollar policyTermPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", policyTermPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(policyTermPremium);
	}

	private void testEmptyEndorsementAutoCA(String policyNumber, PolicyType policy) {
		Dollar policyPremium = getPreEndorsementPremium(policy.get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(policy,"TestDataInquiryAutoCA", generalTabAutoCA, documentsAndBindTabAutoCA);

		TestData td = getTestSpecificTD("TestDataEndorseAutoCA");
		policy.get().endorse().perform(td);
		policy.get().dataGather().getView()
				.fillFromTo(td, generalTabAutoCA.getClass(), premiumAndCoveragesTabAutoCA.getClass(), false);

		assertThat(policyPremium).isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
		premiumAndCoveragesTabAutoCA.calculatePremium();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.getPolicyTermPremium());
	}

	private void fillEmptyEndorsementHomeSS(IPolicy policy, TestData td) {
		policy.endorse().perform(td);
		policy.dataGather().getView()
				.fillFromTo(td, GeneralTab.class, ReportsTab.class, false);
		new ReportsTab().reorderReports();
		policy.dataGather().getView()
				.fillFromTo(td, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
	}

	private void performNonBearingEndorsementHomeCA(PolicyType policy,Tab generalTab, Tab reportsTab, Tab premiumsAndCoveragesQuoteTab) {
		TestData testDataEnd = getTestSpecificTD(TESTDATA_NAME_ENDORSE_HOME_CA);
		policy.get().endorse().perform(testDataEnd);
		policy.get().dataGather().getView()
				.fillFromTo(testDataEnd, generalTab.getClass(), reportsTab.getClass(), false);

		if (policy.getShortName().equalsIgnoreCase(PolicyType.HOME_CA_HO3.getShortName())) {
			new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab().reorderReports();
		}

		policy.get().dataGather().getView()
				.fillFromTo(testDataEnd, reportsTab.getClass(), premiumsAndCoveragesQuoteTab.getClass(), false);
		PropertyQuoteTab.btnCalculatePremium.click();
	}

	private void checkAbilityToOpenAllTabsInInquiryMode(PolicyType policy, String testData, Tab generalTab, Tab bindTab) {
		policy.get().policyInquiry().start();
		policy.get().policyInquiry().getView()
				.fillFromTo(getTestSpecificTD(testData), generalTab.getClass(), bindTab.getClass(), false);

		if (policy.getShortName().toLowerCase().contains("home_ca")){
			assertThat(bindTabHomeCa.btnPurchase.isPresent()).isTrue();
			bindTabHomeCa.cancel();
		}
		else if (policy.getShortName().toLowerCase().contains("autoca")){
			assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab.btnPurchase.isPresent()).isTrue();
			new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().cancel();
		}
		else if (policy.getShortName().toLowerCase().contains("pup")){
			new aaa.main.modules.policy.pup.defaulttabs.BindTab().cancel();
		}
		else if (policy.getShortName().toLowerCase().contains("autoss")){
			assertThat(new DocumentsAndBindTab().getDocumentPrintingDetailsAssetList().isVisible()).isTrue();
			new DocumentsAndBindTab().cancel();
		}
		else if (policy.getShortName().toLowerCase().contains("homess")){
			assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
			new BindTab().cancel();
		}
		else{throw new IstfException("Product is not supported" + policy.getShortName());
		}
	}

	@DataProvider(name = "getPoliciesForEmptyEndorsementTests", parallel = true)
	public Iterator<Object[]> getPolicyNumbersFromDB(Method m, ITestContext iTestContext) {
		String state = iTestContext.getCurrentXmlTest().getAllParameters().get("state");
		if(state == null){
			state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
		}
		log.info(" DataProvider got state: {}", state);
		List<String> policyNumbers = getPoliciesForEmptyEndorsementTests(m.getName(), date1, date2);
		log.info(" DataProvider got policies: {}", policyNumbers);
		String finalState = state;
		List<Object[]> data = policyNumbers.stream().map(policy -> new String[] {finalState, policy}).collect(Collectors.toList());
		return data.iterator();
	}

	private String getCUSTOM_DATE1(String date1) {
		if (!PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE1).isEmpty()) {
			date1 = PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE1);
		}
		return date1;
	}

	private String getCUSTOM_DATE2(String date2) {
		if (!PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE2).isEmpty()) {
			date2 = PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE2);
		}
		return date2;
	}

	public List<String> getPoliciesForEmptyEndorsementTests(String testName, String date1, String date2) {
		date1 = getCUSTOM_DATE1(date1);
		date2 = getCUSTOM_DATE2(date2);

		return getEmptyEndorsementPolicies(testName, date1, date2);
		//return getPoliciesWithDateRangeByQuery(testName, date1, date2).get(0);
	}

}
