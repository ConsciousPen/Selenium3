package aaa.modules.bct;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.config.CsaaTestProperties;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import toolkit.config.PropertyProvider;

public class EndorsementTemplate extends BackwardCompatibilityBaseTest {
	protected static Logger log = LoggerFactory.getLogger(EndorsementTemplate.class);

	private String date1 = "Date1 isn't specified";
	private String date2 = "Date2 isn't specified";

	public static final String TESTDATA_NAME_ENDORSE_HOME_SS = "TestDataEndorseHomeSS";
	public static final String TESTDATA_INQUIRY_HOME_SS = "TestDataInquiryHomeSS";

	public static final String TESTDATA_NAME_ENDORSE_HOME_CA = "TestDataEndorseHomeCA";
	public static final String TESTDATA_INQUIRY_HOME_CA = "TestDataInquiryHomeCA";

	@DataProvider(name = "getPoliciesForEmptyEndorsementTests", parallel = false)
	public Iterator<Object[]> getPolicyNumbersFromDB(Method m, ITestContext iTestContext) {
		String state = iTestContext.getCurrentXmlTest().getAllParameters().get("state");
		if (state == null) {
			state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
			iTestContext.getCurrentXmlTest().addParameter(STATE_PARAM, state);
		}
		log.info("DataProvider got state: {}", state);
		List<String> policyNumbers = getPoliciesForEmptyEndorsementTests(m.getName(), date1, date2, state);
		log.info("DataProvider got policies: {}", policyNumbers);
		String finalState = state;
		List<Object[]> data = policyNumbers.stream().map(policyNumber -> new String[] {finalState, policyNumber}).collect(Collectors.toList());
		return data.iterator();
		/*List<Object[]> data = new ArrayList<>();
		data.add(new String[]{"CA", "ja"});
		data.add(new String[]{"CA", "ja_JP.PCK"});
		data.add(new String[]{"CA", "ja_JP.eucJP"});
		return data.iterator() ;*/
	}

	public void emptyEndorsementHomeCA(String policyNumber) {
		aaa.main.modules.policy.home_ca.defaulttabs.BindTab bindTab = new aaa.main.modules.policy.home_ca.defaulttabs.BindTab();
		aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab();

		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);
		log.info(String.format("Policy premium on Policy Summary page: '%s'", policyPremium));

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(), TESTDATA_INQUIRY_HOME_CA, generalTab, bindTab);
		assertThat(bindTab.btnPurchase.isPresent()).isTrue();
		bindTab.cancel();

		performNonBearingEndorsement(TESTDATA_NAME_ENDORSE_HOME_CA);

		aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		Dollar policyTermPremium = aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", policyTermPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(policyTermPremium);
	}

	public void emptyEndorsementHomeSS(String policyNumber) {
		aaa.main.modules.policy.home_ss.defaulttabs.BindTab bindTab = new aaa.main.modules.policy.home_ss.defaulttabs.BindTab();
		aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab();

		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);
		log.info(String.format("Policy premium on Policy Summary page: '%s'", policyPremium));

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(),TESTDATA_INQUIRY_HOME_SS, generalTab, bindTab);
		assertThat(bindTab.btnPurchase.isPresent()).isTrue();
		bindTab.cancel();

		performNonBearingEndorsement(TESTDATA_NAME_ENDORSE_HOME_SS);

		aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		Dollar policyTermPremium = aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", policyTermPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(policyTermPremium);
	}

	public void checkAbilityToOpenAllTabsInInquiryMode(PolicyType policy, String testData, Tab fillFromTab, Tab fillToTab) {
		policy.get().policyInquiry().start();
		policy.get().policyInquiry().getView()
				.fillFromTo(testDataManager.getDefault(EndorsementTemplate.class).getTestData(testData), fillFromTab.getClass(), fillToTab.getClass(), false);
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

	public List<String> getPoliciesForEmptyEndorsementTests(String testName, String date1, String date2,String state) {
		date1 = getCUSTOM_DATE1(date1);
		date2 = getCUSTOM_DATE2(date2);

		return getEmptyEndorsementPolicies(testName, date1, date2, state);
		//return getPoliciesWithDateRangeByQuery(testName, date1, date2).get(0);
	}

	/**
	 * Have to be overridden
	 */
	public void reorderReports() {
	}
	/**
	 * Have to be overridden
	 */
	public void performNonBearingEndorsement(String testDataName) {
	}
}
