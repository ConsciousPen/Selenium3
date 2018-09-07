package aaa.modules.bct.service;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import aaa.common.Tab;
import aaa.config.CsaaTestProperties;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;

public class EndorsementTemplate extends BackwardCompatibilityBaseTest {

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
		}
		log.info(" DataProvider got state: {}", state);
		List<String> policyNumbers = getPoliciesForEmptyEndorsementTests(m.getName(), date1, date2, state);
		log.info(" DataProvider got policies: {}", policyNumbers);
		String finalState = state;
		List<Object[]> data = policyNumbers.stream().map(policy -> new String[] {finalState, policy}).collect(Collectors.toList());
		return data.iterator();
	}

	public void performNonBearingEndorsement(PolicyType policy, String testDataName) {
		TestData testDataEnd = getTestSpecificTD(testDataName);

		policy.get().endorse().perform(testDataEnd);
		policy.get().dataGather().getView()
				.fillFromTo(testDataEnd, GeneralTab.class, ReportsTab.class, false);

		reorderReports();

		policy.get().dataGather().getView()
				.fillFromTo(testDataEnd, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
	}

	public void checkAbilityToOpenAllTabsInInquiryMode(PolicyType policy, String testData, Tab fillFromTab, Tab fillToTab) {
		policy.get().policyInquiry().start();
		policy.get().policyInquiry().getView()
				.fillFromTo(getTestSpecificTD(testData), fillFromTab.getClass(), fillToTab.getClass(), false);
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

	public List<String> getPoliciesForEmptyEndorsementTests(String testName, String date1, String date2, String state) {
		date1 = getCUSTOM_DATE1(date1);
		date2 = getCUSTOM_DATE2(date2);

		return getEmptyEndorsementPolicies(testName, date1, date2, state);
		//return getPoliciesWithDateRangeByQuery(testName, date1, date2).get(0);
	}

	public void reorderReports() {
		switch (getPolicyType().getShortName()) {
			case "HomeSS_HO3":
			case "HomeSS_HO4":
			case "HomeSS_HO6":
			case "HomeSS_DP3":
				new aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab().reorderReports();
				break;
			case "HomeCA_HO3":
			case "HomeCA_HO4":
			case "HomeCA_HO6":
			case "HomeCA_DP3":
				new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab().reorderReports();
				break;
		}
	}

}
