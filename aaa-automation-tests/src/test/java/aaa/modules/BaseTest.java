/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.exigen.ipb.etcsa.base.app.ApplicationFactory;
import com.exigen.ipb.etcsa.base.app.MainApplication;
import com.exigen.ipb.etcsa.base.app.OperationalReportApplication;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;

import aaa.EntityLogger;
import aaa.common.Constants.States;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.metadata.LoginPageMeta;
import aaa.common.pages.LoginPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataManager;
import aaa.helpers.TimePoints;
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
import toolkit.verification.CustomAssert;
import toolkit.verification.CustomAssert.AssertDriverType;

@Listeners({ com.exigen.ipb.etcsa.utils.listener.ETCSAListener.class })
public class BaseTest {

	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);

	protected static TestData tdCustomerIndividual;
	protected static TestData tdCustomerNonIndividual;
	private static Map<String, String> entities;

	private String quoteNumber;
	public String customerNumber;
	private String key;
	private static ThreadLocal<String> state = new ThreadLocal<String>();
	protected Customer customer = new Customer();

	static {
		CustomAssert.initDriver(AssertDriverType.TESTNG);
		tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		tdCustomerNonIndividual = new TestDataManager().customer.get(CustomerType.NON_INDIVIDUAL);
		if (StringUtils.isNotBlank(PropertyProvider.getProperty("test.usstate")))
			setState(PropertyProvider.getProperty("test.usstate"));
	}

	protected TestData tdSpecific;
	protected TestDataManager testDataManager;

	public BaseTest() {
		testDataManager = new TestDataManager();
		initTestDataForTest();
	}

	private void initTestDataForTest() {
		try {
			tdSpecific = testDataManager.getDefault(this.getClass());
		} catch (TestDataException tde) {
			log.debug(String.format("Specified TestData for test is absent: %s", tde.getMessage()));
		}
	}

	@Parameters({ "state" })
	@BeforeClass
	public void beforeClassConfiguration(@Optional("UT") String state) {
		if (getPolicyType().equals(PolicyType.HOME_CA) || getPolicyType().equals(PolicyType.AUTO_CA) || getPolicyType().equals(PolicyType.CEA))
			setState(States.CA.get());
		else
			setState(state);
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
		String key = EntitiesHolder.makeCustomerKey(CustomerType.INDIVIDUAL, getState());
		// EntitiesHolder.addNewEntiry(key, "700032098");
		if (!EntitiesHolder.isEntityPresent(key)) {
			customer.create(td);
			customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
			EntitiesHolder.addNewEntity(key, customerNumber);
			return customerNumber;
		} else {
			customerNumber = EntitiesHolder.getEntity(key);
			SearchPage.search(SearchFor.CUSTOMER, SearchBy.CUSTOMER, customerNumber);
			log.info("Use existing " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
		}

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
		key = EntitiesHolder.makeCustomerKey(CustomerType.NON_INDIVIDUAL, getState());
		if (!EntitiesHolder.isEntityPresent(key)) {
			customer.create(td);
			customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
			EntitiesHolder.addNewEntity(key, customerNumber);
			return customerNumber;
		} else {
			customerNumber = EntitiesHolder.getEntity(key);
			SearchPage.search(SearchFor.CUSTOMER, SearchBy.CUSTOMER, customerNumber);
			log.info("Use existing " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
		}

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
	 */
	protected void createQuote(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		createCustomerIndividual();
		log.info("Quote Creation Started...");
		getPolicyType().get().createQuote(td);
		// return PolicySummaryPage.labelPolicyNumber.getValue();
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
		createCustomerIndividual();
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
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		String key = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState());
		if (EntitiesHolder.isEntityPresent(key)) {
			SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, EntitiesHolder.getEntity(key));
		} else {
			createPolicy();
			EntitiesHolder.addNewEntity(key, PolicySummaryPage.labelPolicyNumber.getValue());
		}
		getPolicyType().get().copyPolicy(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	protected PolicyType getPolicyType() {
		return null;
	}

	protected PolicyRestImpl getPolicyRest() {
		return getPolicyType().getPolicyRest().createInstance(customerNumber, quoteNumber);
	}

	/**
	 * Login to the application
	 */
	public static MainApplication mainApp() {
		return ApplicationFactory.get().mainApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Login to the application and open admin page
	 */
	public static MainApplication adminApp() {
		return ApplicationFactory.get().adminApp(new LoginPage(initiateLoginTD()));
	}

	/**
	 * Login to the application and open reports app
	 */
	protected OperationalReportApplication opReportApp() {
		return ApplicationFactory.get().opReportApp(new LoginPage(initiateLoginTD()));
	}

	@AfterMethod(alwaysRun = true)
	public void logout() {
		if (TimeShiftTestUtil.isContextAvailable()) {
			mainApp().close();
			opReportApp().close();
		}
	}

	@AfterClass(alwaysRun = true)
	protected void closeBrowser() {
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		if (Boolean.parseBoolean(PropertyProvider.getProperty("isCiMode", "true"))) {
			mainApp().close();
			opReportApp().close();
		}
	}

	protected static String getState() {
		return state.get();
	}

	private static void setState(String newState) {
		BaseTest.state.set(newState);
	}

	protected static synchronized Map<String, String> getEntities() {
		entities = EntitiesHolder.getEntities();
		Map<String, String> returnValue = entities;
		return returnValue;
	}

	protected TestData getStateTestData(TestData td, String fileName, String tdName) {
		TestData returnTD = td.getTestData(fileName);
		if (returnTD.containsKey(getStateTestDataName(tdName))) {
			returnTD = returnTD.getTestData(getStateTestDataName(tdName));
			log.info(String.format("==== %s Test Data is used: %s:%s ====", getState(), fileName, getStateTestDataName(tdName)));
		} else {
			returnTD = returnTD.getTestData(tdName);
			log.info(String.format("==== Default state UT Test Data is used. Requested Test Data %s:%s is missing ====", fileName, getStateTestDataName(tdName)));
		}
		return returnTD;
	}

	protected String getStateTestDataName(String tdName) {
		String state = getState();
		// if (!state.equals(States.UT) && !state.equals(States.CA))
		tdName = tdName + "_" + state;
		return tdName;
	}

	protected TimePoints getTimePoints() {
		return new TimePoints(testDataManager.timepoint.get(getPolicyType()).getTestData(getStateTestDataName("TestData")));
	}

	protected static TestData initiateLoginTD() {
		Map<String, Object> td = new LinkedHashMap<String, Object>();
		td.put(LoginPageMeta.USER.getLabel(), PropertyProvider.getProperty(TestProperties.EU_USER));
		td.put(LoginPageMeta.PASSWORD.getLabel(), PropertyProvider.getProperty(TestProperties.EU_PASSWORD));
		td.put(LoginPageMeta.STATES.getLabel(), getState());
		return new SimpleDataProvider(td);
	}
}
