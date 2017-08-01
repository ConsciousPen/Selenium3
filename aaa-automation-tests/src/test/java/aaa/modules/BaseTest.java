/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.exigen.ipb.etcsa.base.app.ApplicationFactory;
import com.exigen.ipb.etcsa.base.app.MainApplication;
import com.exigen.ipb.etcsa.base.app.OperationalReportApplication;

import aaa.EntityLogger;
import aaa.common.Constants;
import aaa.common.Constants.States;
import aaa.common.enums.SearchEnum;
import aaa.common.metadata.LoginPageMeta;
import aaa.common.pages.LoginPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataManager;
import aaa.helpers.TimePoints;
import aaa.helpers.config.CustomTestProperties;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.rest.policy.PolicyRestImpl;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;
import toolkit.verification.CustomAssert;

@Listeners({ TestngTestListener2.class })
public class BaseTest {

	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);

	protected static TestData tdCustomerIndividual;
	protected static TestData tdCustomerNonIndividual;
	private static Map<String, String> entities;
	public String customerNumber;
	protected Customer customer = new Customer();
	protected TestData tdSpecific;
	protected TestDataManager testDataManager;
	private String quoteNumber;
	private String key;
	private static ThreadLocal<String> state = new ThreadLocal<>();
	private static String usState = PropertyProvider.getProperty("test.usstate");
	private static Map<String, Integer> policyCount = new HashMap<>();
	private boolean isCImodeEnabled = Boolean.parseBoolean(PropertyProvider.getProperty(CustomTestProperties.isCImode, "true"));

	static {
		CustomAssert.initDriver(CustomAssert.AssertDriverType.TESTNG);
		tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		tdCustomerNonIndividual = new TestDataManager().customer.get(CustomerType.NON_INDIVIDUAL);
	}

	public BaseTest() {
		testDataManager = new TestDataManager();
		initTestDataForTest();
	}

	protected static synchronized Map<String, String> getEntities() {
		entities = EntitiesHolder.getEntities();
		Map<String, String> returnValue = entities;
		return returnValue;
	}

	protected PolicyType getPolicyType() {
		return null;
	}

	protected PolicyRestImpl getPolicyRest() {
		return getPolicyType().getPolicyRest().createInstance(customerNumber, quoteNumber);
	}

	public static String getState() {
		return state.get();
	}

	private void setState(String newState) {
		state.set(newState);
		log.info(getState());
	}

	protected TimePoints getTimePoints() {
		return new TimePoints(testDataManager.timepoint.get(getPolicyType()).getTestData(getStateTestDataName("TestData")));
	}

	@Parameters({ "state" })
	@BeforeMethod(alwaysRun=true)
	public void beforeMethodStateConfiguration(@Optional("") String state) {
		if (isStateCA()) {
			setState(Constants.States.CA.get());
		} else if (StringUtils.isNotBlank(usState) && StringUtils.isBlank(state)) {
			setState(usState);
		} else if (StringUtils.isNotBlank(state)) {
			setState(state);
		} else {
			setState(States.UT.get());
		}
	}
	
	/**
	 * Login to the application
	 */
	public MainApplication mainApp() {
		return ApplicationFactory.get().mainApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Login to the application and open admin page
	 */
	public MainApplication adminApp() {
		return ApplicationFactory.get().adminApp(new LoginPage(initiateLoginTD()));
	}

	@AfterMethod(alwaysRun = true)
	public void logout() {
		if (isCImodeEnabled) {
			closeAllApps();
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		if (isCImodeEnabled) {
			closeAllApps();
		}
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
		EntitiesHolder.addNewEntity(key, customerNumber);

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
		EntitiesHolder.addNewEntity(key, customerNumber);
		return customerNumber;
	}

	/**
	 * Create quote using default TestData
	 */
	protected void createQuote() {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		TestData tdPolicy = testDataManager.policy.get(getPolicyType());
		createQuote(getStateTestData(tdPolicy, "DataGather", "TestData"));
	}

	/**
	 * Create quote using provided TestData Note: Suitable only for quote type
	 * that is returned by test's getPolicyType()
	 *
	 * @param td
	 *            - test data for quote filling
	 * @return
	 */
	protected void createQuote(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		log.info("Quote Creation Started...");
		getPolicyType().get().createQuote(td);
		// return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	/**
	 * Gets default quote number and makes quote copy. If default quote doesn't
	 * exist - created it first
	 * 
	 * @return Copied quote number
	 */
	protected String getCopiedQuote() {
		openDefaultPolicy(getPolicyType(), getState());
		getPolicyType().get().policyCopy().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
		log.info("Quote copied " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	/**
	 * Create Policy using default TestData
	 *
	 * @return policy number
	 */
	protected String createPolicy() {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		TestData tdPolicy = testDataManager.policy.get(getPolicyType());
		return createPolicy(getStateTestData(tdPolicy, "DataGather", "TestData"));
	}

	/**
	 * Create quote using provided TestData Note: Suitable only for policy type
	 * that is returned by test's getPolicyType()
	 *
	 * @param td
	 *            - test data for policy filling and purchase
	 * @return policy number
	 */
	protected String createPolicy(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		log.info("Policy Creation Started...");
		getPolicyType().get().createPolicy(td);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(EntitiesHolder.makePolicyKey(getPolicyType(), getState()), policyNumber);
		return policyNumber;
	}

	/**
	 * Copy default policy with test's type and purchase it (Customer of default
	 * policy will be used) Note: California Earthquake can not be copied
	 *
	 * @return policy number
	 */
	protected String getCopiedPolicy() {
		openDefaultPolicy(getPolicyType(), getState());
		getPolicyType().get().copyPolicy(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private String openDefaultPolicy(PolicyType policyType, String state) {
		Assert.assertNotNull(policyType, "PolicyType is not set");
		String key = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState());
		String policyNumber = "";
		synchronized (key) {
			Integer count = policyCount.get(key);
			if (count == null)
				count = 1;
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

	/**
	 * Login to the application and open reports app
	 */
	protected OperationalReportApplication opReportApp() {
		return ApplicationFactory.get().opReportApp(new LoginPage(initiateLoginTD()));
	}
	
	protected TestData getStateTestData(TestData td, String fileName, String tdName) {
		if (!td.containsKey(fileName)) {
			throw new TestDataException("Can't get test data file " + fileName);
		}
		return getStateTestData(td.getTestData(fileName), tdName);
	}

	protected TestData getStateTestData(TestData td, String tdName) {
		if (td.containsKey(getStateTestDataName(tdName))) {
			td = td.getTestData(getStateTestDataName(tdName));
			log.info(String.format("==== %s Test Data is used: %s ====", getState(), getStateTestDataName(tdName)));
		} else {
			td = td.getTestData(tdName);
			log.info(String.format("==== Default state UT Test Data is used. Requested Test Data: %s is missing ====", getStateTestDataName(tdName)));
		}
		return td;
	}

	private String getStateTestDataName(String tdName) {
		String state = getState();
		// if (!state.equals(States.UT) && !state.equals(States.CA))
		tdName = tdName + "_" + state;
		return tdName;
	}

	protected TestData initiateLoginTD() {
		Map<String, Object> td = new LinkedHashMap<String, Object>();
		td.put(LoginPageMeta.USER.getLabel(), PropertyProvider.getProperty(TestProperties.EU_USER));
		td.put(LoginPageMeta.PASSWORD.getLabel(), PropertyProvider.getProperty(TestProperties.EU_PASSWORD));
		td.put(LoginPageMeta.STATES.getLabel(), getState());
		return new SimpleDataProvider(td);
	}

	protected Boolean isStateCA() {
		return getPolicyType() != null && (getPolicyType().equals(PolicyType.HOME_CA_HO3) || getPolicyType().equals(PolicyType.AUTO_CA_SELECT) || getPolicyType().equals(PolicyType.CEA) || getPolicyType().equals(PolicyType.HOME_CA_DP3) || getPolicyType().equals(PolicyType.HOME_CA_HO4)
				|| getPolicyType().equals(PolicyType.HOME_CA_HO6) || getPolicyType().equals(PolicyType.AUTO_CA_CHOICE));
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
