/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import com.exigen.ipb.etcsa.base.app.AdminApplication;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.etcsa.base.app.MainApplication;
import com.exigen.ipb.etcsa.base.app.OperationalReportApplication;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.metadata.LoginPageMeta;
import aaa.common.pages.LoginPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataManager;
import aaa.helpers.TimePoints;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.listeners.AaaTestListener;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.utils.EntityLogger;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;

@Listeners({AaaTestListener.class})
public class BaseTest {

	protected static final String TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = "state";
	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);
	private static TestData tdCustomerIndividual;
	private static TestData tdCustomerNonIndividual;
	private static TestData tdOperationalReports;
	private static ThreadLocal<String> state = new ThreadLocal<>();
	private static String usState = PropertyProvider.getProperty(CustomTestProperties.TEST_USSTATE);
	private static Map<String, Integer> policyCount = new HashMap<>();
	public String customerNumber;
	protected Customer customer = new Customer();
	protected TestDataManager testDataManager;
	private TestData tdSpecific;
	private boolean isCiModeEnabled = Boolean.parseBoolean(PropertyProvider.getProperty(CustomTestProperties.IS_CI_MODE, "true"));

	static {
		CustomAssert.initDriver(CustomAssert.AssertDriverType.TESTNG);
		tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		tdCustomerNonIndividual = new TestDataManager().customer.get(CustomerType.NON_INDIVIDUAL);
		tdOperationalReports = new TestDataManager().operationalReports.get(OperationalReportType.OPERATIONAL_REPORT);
	}

	public BaseTest() {
		testDataManager = new TestDataManager();
		initTestDataForTest();
	}

	public static String getState() {
		return state.get();
	}

	private void setState(String newState) {
		state.set(newState);
	}

	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 */
	protected final Map<String, String> getPrimaryPoliciesForPup() {
		//EntitiesHolder.addNewEntity(EntitiesHolder.makeDefaultPolicyKey(PolicyType.HOME_SS_HO3,
		//getState()), "COH3927438929");
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}
		//remember customer that was created in test
		String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		Map<String, String> returnValue = new LinkedHashMap<>();
		String state = getState().intern();
		synchronized (state) {
			PolicyType type;
			PolicyType typeAuto = null;
			if (state.equals(Constants.States.CA)) {
				type = PolicyType.HOME_CA_HO3;
				typeAuto = PolicyType.AUTO_CA_SELECT;
			} else {
				type = PolicyType.HOME_SS_HO3;
			}
			String key = EntitiesHolder.makeDefaultPolicyKey(type, state);
			if (EntitiesHolder.isEntityPresent(key)) {
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			} else {
				type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
				EntitiesHolder.addNewEntity(key, PolicySummaryPage.labelPolicyNumber.getValue());
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			}

			if (typeAuto != null) {
				String keyAuto = EntitiesHolder.makeDefaultPolicyKey(typeAuto, state);
				if (EntitiesHolder.isEntityPresent(keyAuto)) {
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				} else {
					typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
					EntitiesHolder.addNewEntity(keyAuto, PolicySummaryPage.labelPolicyNumber.getValue());
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				}
			}
			//open Customer that was created in test
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customerNum);
			}
			return returnValue;
		}
	}

	protected PolicyType getPolicyType() {
		return null;
	}

	protected TimePoints getTimePoints() {
		return new TimePoints(testDataManager.timepoint.get(getPolicyType()).getTestData(getStateTestDataName("TestData")), getPolicyType(), getState());
	}

	/**
	 * Gets default quote number and makes quote copy. If default quote doesn't
	 * exist - created it first
	 *
	 * @return Copied quote number
	 */
	protected String getCopiedQuote() {
		openDefaultPolicy(getPolicyType(), getState());
		String key = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState());
		synchronized (key) {
			getPolicyType().get().policyCopy().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
			log.info("Quote copied {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
		}
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	/**
	 * Copy default policy with test's type and purchase it (Customer of default
	 * policy will be used) Note: California Earthquake can not be copied
	 *
	 * @return policy number
	 */
	protected String getCopiedPolicy() {
		openDefaultPolicy(getPolicyType(), getState());
		String key = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState());
		synchronized (key) {
			getPolicyType().get().copyPolicy(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
			log.info("Policy copied {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
		}
		return PolicySummaryPage.labelPolicyNumber.getValue();

	}

	protected TestData getPolicyDefaultTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	protected boolean isStateCA() {
		return getPolicyType() != null && getPolicyType().isCaProduct();
	}

	protected TestData getManualConversionInitiationTd() {
		return getStateTestData(testDataManager.policy.get(getPolicyType()), CustomerActions.InitiateRenewalEntry.class.getSimpleName(), "TestData");
	}

	protected TestData getConversionPolicyDefaultTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	public static void printToLog(String message) {
		log.info("----------------------------------------------------------------");
		log.info(message);
	}

	public static void printToLog(String message, Object... inputValues) {
		log.info("----------------------------------------------------------------");
		String msg = String.format("Message: %1$s", message);
		log.info(String.format(msg, inputValues));
	}

	public static void printToDebugLog(String message) {
		log.debug("----------------------------------------------------------------");
		log.debug(message);
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeMethodStateConfiguration(Object[] parameters) {
		if (parameters != null && parameters.length != 0 && StringUtils.isNotBlank(parameters[0].toString())) {
			setState(parameters[0].toString());
		} else if (isStateCA()) {
			setState(Constants.States.CA);
		} else if (StringUtils.isNotBlank(usState)) {
			setState(usState);
		} else {
			setState(Constants.States.UT);
		}
	}

	@AfterMethod(alwaysRun = true)
	public void logout() {
		if (isCiModeEnabled) {
			closeAllApps();
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		if (isCiModeEnabled) {
			closeAllApps();
		}
	}

	/**
	 * Login to the application
	 */
	public MainApplication mainApp() {
		return CSAAApplicationFactory.get().mainApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Login to the application and open admin page
	 */
	public AdminApplication adminApp() {
		return CSAAApplicationFactory.get().adminApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Login to the application and open reports app
	 */
	protected OperationalReportApplication opReportApp() {
		return CSAAApplicationFactory.get().opReportApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Create individual customer using default TestData
	 */
	protected String createCustomerIndividual() {
		return createCustomerIndividual(getStateTestData(tdCustomerIndividual, "DataGather", "TestData"));
	}

	/**
	 * Create individual customer using provided TestData
	 */
	protected String createCustomerIndividual(TestData td) {
		customer.create(td);
		customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
		return customerNumber;
	}

	/**
	 * Create NonIndividual customer using default TestData
	 */
	protected String createCustomerNonIndividual() {
		return createCustomerNonIndividual(getStateTestData(tdCustomerNonIndividual, "DataGather", "TestData"));
	}

	/**
	 * Create individual customer using provided TestData
	 */
	protected String createCustomerNonIndividual(TestData td) {
		customer.create(td);
		customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
		return customerNumber;
	}

	/**
	 * Create quote using default TestData
	 */
	protected String createQuote() {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return createQuote(td);
	}

	/**
	 * Create quote using provided TestData</br>
	 * Note: Suitable only for quote type that is returned by test's getPolicyType()</br>
	 * PUP policy test data should be adjusted with Home policy number.
	 *
	 * @param td - test data for quote filling
	 * @return
	 */
	protected String createQuote(TestData td) {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		log.info("Quote Creation Started...");
		getPolicyType().get().createQuote(td);
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	/**
	 * Create Policy using default TestData
	 *
	 * @return policy number
	 */
	protected String createPolicy() {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return createPolicy(td);
	}

	/**
	 * Create quote using provided TestData</br>
	 * Note: Suitable only for policy type that is returned by test's getPolicyType()</br>
	 * PUP policy test data should be adjusted with Home policy number.
	 *
	 * @param td - test data for policy filling and purchase
	 * @return policy number
	 */
	protected String createPolicy(TestData td) {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		log.info("Policy Creation Started...");
		getPolicyType().get().createPolicy(td);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(EntitiesHolder.makePolicyKey(getPolicyType(), getState()), policyNumber);
		return policyNumber;
	}

	/**
	 * Should be used for creation of custom Underlying Home or Auto policies to use them durring PUP policy creation.\
	 *
	 * @param tdHomeAdjustment - TestData adjustment for creation of Home HO3 policy (use state specific test data for HOME_CA product)
	 * @param tdAutoAdjustment - TestData adjustment for creation of AUTO_CA policy
	 */
	protected Map<String, String> getPrimaryPoliciesForPup(TestData tdHomeAdjustment, TestData tdAutoAdjustment) {
		Map<String, String> policies = new LinkedHashMap<>();
		String state = getState().intern();
		if (state.equals(Constants.States.CA)) {
			TestData tdHome = testDataManager.policy.get(PolicyType.HOME_CA_HO3);
			TestData tdHomeData = getStateTestData(tdHome, "DataGather", "TestData").adjust(tdHomeAdjustment);
			PolicyType.HOME_CA_HO3.get().createPolicy(tdHomeData);
			policies.put("Primary_HO3", PolicySummaryPage.labelPolicyNumber.getValue());

			if (tdAutoAdjustment != null) {
				TestData tdAuto = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);
				TestData tdAutoData = getStateTestData(tdAuto, "DataGather", "TestData").adjust(tdAutoAdjustment);
				PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAutoData);
				policies.put("Primary_Auto", PolicySummaryPage.labelPolicyNumber.getValue());
			}
		} else {
			TestData tdHome = testDataManager.policy.get(PolicyType.HOME_SS_HO3);
			TestData tdHomeData = getStateTestData(tdHome, "DataGather", "TestData").adjust(tdHomeAdjustment);
			PolicyType.HOME_SS_HO3.get().createPolicy(tdHomeData);
			policies.put("Primary_HO3", PolicySummaryPage.labelPolicyNumber.getValue());
		}
		return policies;
	}

	/**
	 * Create Conversion Policy using default TestData
	 *
	 * @return policy number
	 */
	protected String createConversionPolicy() {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		TestData tdPolicy = getConversionPolicyDefaultTD();
		TestData tdManualConversionInitiation = getManualConversionInitiationTd();
		customer.initiateRenewalEntry().perform(tdManualConversionInitiation);
		log.info("Policy Creation Started...");
		getPolicyType().get().getDefaultView().fill(tdPolicy);
		if(PolicySummaryPage.buttonBackFromRenewals.isEnabled()){
			PolicySummaryPage.buttonBackFromRenewals.click();}
		String policyNumber = PolicySummaryPage.labellinkPolicy.getValue();
		return policyNumber;
	}

	protected TestData getCustomerIndividualTD(String fileName, String tdName) {
		return getStateTestData(tdCustomerIndividual, fileName, tdName);
	}

	protected TestData getCustomerNonIndividualTD(String fileName, String tdName) {
		return getStateTestData(tdCustomerNonIndividual, fileName, tdName);
	}

	protected TestData getOperationalReportsTD(String fileName, String tdName) {
		return getStateTestData(tdOperationalReports, fileName, tdName);
	}

	protected TestData getTestSpecificTD(String tdName) {
		return getStateTestData(tdSpecific, tdName);
	}

	protected TestData getStateTestData(TestData td, String fileName, String tdName) {
		if (!td.containsKey(fileName)) {
			throw new TestDataException("Can't get test data file " + fileName);
		}
		return getStateTestData(td.getTestData(fileName), tdName);
	}

	protected TestData getStateTestData(TestData td, String tdName) {
		if (td == null) {
			throw new RuntimeException(String.format("Can't get TestData '%s', parrent TestData is null", tdName));
		}
		if (td.containsKey(getStateTestDataName(tdName))) {
			td = td.getTestData(getStateTestDataName(tdName));
			log.info(String.format("==== %s Test Data is used: %s ====", getState(), getStateTestDataName(tdName)));
		} else {
			td = td.getTestData(tdName);
			if (getState().equals(Constants.States.CA)) {
				log.info(String.format("==== CA Test Data is used: %s ====", getStateTestDataName(tdName)));
			} else {
				log.info(String.format("==== Default state UT Test Data is used. Requested Test Data: %s is missing ====", getStateTestDataName(tdName)));
			}
		}
		return td;
	}

	protected TestData initiateLoginTD() {
		Map<String, Object> td = new LinkedHashMap<>();
		td.put(LoginPageMeta.USER.getLabel(), PropertyProvider.getProperty(TestProperties.APP_USER));
		td.put(LoginPageMeta.PASSWORD.getLabel(), PropertyProvider.getProperty(TestProperties.APP_PASSWORD));
		td.put(LoginPageMeta.STATES.getLabel(), getState());
		return new SimpleDataProvider(td);
	}

	private String openDefaultPolicy(PolicyType policyType, String state) {
		assertThat(policyType).as("PolicyType is not set").isNotNull();
		String key = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), state);
		String policyNumber;
		synchronized (key) {
			Integer count = policyCount.get(key);
			if (count == null) {
				count = 1;
			}
			if (EntitiesHolder.isEntityPresent(key) && count < 10) {
				count++;
				policyNumber = EntitiesHolder.getEntity(key);
				SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			} else {
				count = 1;
				createCustomerIndividual();
				createPolicy();
				policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
				EntitiesHolder.addNewEntity(key, policyNumber);
			}
			policyCount.put(key, count);
		}
		return policyNumber;
	}

	private String getStateTestDataName(String tdName) {
		String state = getState();
		// if (!state.equals(States.UT) && !state.equals(States.CA))
		tdName = tdName + "_" + state;
		return tdName;
	}

	private void initTestDataForTest() {
		try {
			tdSpecific = testDataManager.getDefault(this.getClass());
		} catch (TestDataException tde) {
			log.debug(String.format("Specified TestData for test is absent: %s", tde.getMessage()));
		}
	}

	private void closeAllApps() {
		mainApp().close();
		adminApp().close();
		opReportApp().close();
	}
}
