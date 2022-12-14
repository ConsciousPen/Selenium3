/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.*;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.eisa.base.app.impl.AdminApplication;
import com.exigen.ipb.eisa.base.app.impl.MainApplication;
import com.exigen.ipb.eisa.base.app.impl.OperationalReportApplication;
import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import aaa.common.enums.Constants;
import aaa.common.metadata.LoginPageMeta;
import aaa.common.pages.LoginPage;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataManager;
import aaa.helpers.TimePoints;
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
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;

@Listeners({AaaTestListener.class})
public class BaseTest {

	protected static final String TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = Constants.STATE_PARAM;
	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);
	private static TestData loginUsers;
	private static TestData tdCustomerIndividual;
	private static TestData tdCustomerNonIndividual;
	private static TestData tdOperationalReports;
	private static ThreadLocal<String> state = new ThreadLocal<>();
	private static String usState = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
	public String customerNumber;
	protected Customer customer = new Customer();
	protected TestDataManager testDataManager;
	protected ITestContext context;
	private String userGroup;
	private TestData tdSpecific;
	private boolean isCiModeEnabled = Boolean.parseBoolean(PropertyProvider.getProperty(CsaaTestProperties.IS_CI_MODE, "true"));

	static {
		tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		tdCustomerNonIndividual = new TestDataManager().customer.get(CustomerType.NON_INDIVIDUAL);
		tdOperationalReports = new TestDataManager().operationalReports.get(OperationalReportType.OPERATIONAL_REPORT);
		loginUsers = new TestDataManager().loginUsers;
	}

	public BaseTest() {
		testDataManager = new TestDataManager();
		initTestDataForTest();
	}

	public static String getState() {
		return state.get();
	}

	public void setState(String newState) {
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
		String customerNum = null;
		//remember customer if it was created in test
		if (CustomerSummaryPage.labelCustomerNumber.isPresent()) {
			customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		}
		mainApp().close();
		String state = getState().intern();
		Map<String, String> returnValue = new LinkedHashMap<>();
		synchronized (state) {
			if (state.equals(Constants.States.CA)) {
				returnValue.put("Primary_HO3", getDefaultPolicy(PolicyType.HOME_CA_HO3));
				returnValue.put("Primary_Auto", getDefaultPolicy(PolicyType.AUTO_CA_SELECT));
			} else {
				returnValue.put("Primary_HO3", getDefaultPolicy(PolicyType.HOME_SS_HO3));
			}
		}
		//open Customer if it was created in test
		mainApp().open();
		if (customerNum != null) {
			SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customerNum);
		}
		return returnValue;

	}

	protected TimePoints getTimePoints() {
		return new TimePoints(testDataManager.timepoint.get(getPolicyType()).getTestData(getStateTestDataName("TestData")), getPolicyType(), getState());
	}

	protected PolicyType getPolicyType() {
		return null;
	}

	/**
	 * Gets default quote number and makes quote copy. If default quote doesn't
	 * exist - created it first
	 *
	 * @return Copied quote number
	 */
	protected String getCopiedQuote() {
		createCustomerIndividual();
		return createQuote();
	}

	/**
	 * Copy default policy with test's type and purchase it (Customer of default
	 * policy will be used) Note: California Earthquake can not be copied
	 *
	 * @return policy number
	 */
	protected String getCopiedPolicy() {
		createCustomerIndividual();
		return createPolicy();

	}

	protected TestData getPolicyDefaultTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	protected TestData getConversionPolicyDefaultTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	protected String getUserGroup() {
		if (StringUtils.isNotBlank(userGroup)) {
			return userGroup;
		}
		return Constants.UserGroups.QA.get();
	}

	protected TestData getLoginTD() {
		return getLoginTD(Constants.UserGroups.valueOf(getUserGroup()));
	}

	protected boolean isStateCA() {
		return getPolicyType() != null && getPolicyType().isCaProduct();
	}

	protected boolean isAutoCA() {
		return getPolicyType() != null && getPolicyType().isCaProduct() && getPolicyType().isAutoPolicy();
	}

	protected TestData getManualConversionInitiationTd() {
		return getStateTestData(testDataManager.policy.get(getPolicyType()), CustomerActions.InitiateRenewalEntry.class.getSimpleName(), "TestData");
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

	@Parameters("login")
	@BeforeMethod(alwaysRun = true)
	public void stateConfiguration(@Optional("") String login, Method method, ITestContext context) {
		this.context = context;
		if (method.isAnnotationPresent(Test.class)) {
			String state = "";
			if (StringUtils.isNotBlank(context.getCurrentXmlTest().getParameter(Constants.STATE_PARAM))) {
				state = context.getCurrentXmlTest().getParameter(Constants.STATE_PARAM);
			} else {
				if (method.isAnnotationPresent(Parameters.class) && Arrays.asList(method.getAnnotation(Parameters.class).value()).stream().anyMatch(p -> "state".equals(p))) {
					Parameter stateParam = Arrays.asList(method.getParameters()).stream().filter(p -> p.isAnnotationPresent(Optional.class)).findFirst().orElse(null);
					if (stateParam != null) {
						if (!stateParam.getAnnotation(Optional.class).value().isEmpty()) {
							state = stateParam.getAnnotation(Optional.class).value();
						} else if (StringUtils.isNotBlank(PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE))) {
							state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
						} else if (isStateCA()) {
							state = Constants.States.CA;
						}
					}
				}
				context.getCurrentXmlTest().addParameter(Constants.STATE_PARAM, state);
			}
			setState(state);
		}
		this.userGroup = login;
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
		CSAAApplicationFactory.get().mainApp().setLogin(new LoginPage(getLoginTD()));
		return CSAAApplicationFactory.get().mainApp();
	}

	/**
	 * Login to the application and open admin page
	 */
	public AdminApplication adminApp() {
		CSAAApplicationFactory.get().adminApp().setLogin(new LoginPage(getLoginTD()));
		return CSAAApplicationFactory.get().adminApp();
	}

	/**
	 * Login to the application and open reports app
	 */
	protected OperationalReportApplication opReportApp() {
		CSAAApplicationFactory.get().opReportApp().setLogin(new LoginPage(getLoginTD()));
		return CSAAApplicationFactory.get().opReportApp();
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

	protected TestData getStateTestData(TestData td, String fileName, String tdName) {
		if (!td.containsKey(fileName)) {
			throw new TestDataException("Can't get test data file " + fileName);
		}
		return getStateTestData(td.getTestData(fileName), tdName);
	}

	protected TestData getStateTestData(TestData td, String tdName) {
		if (td == null) {
			throw new TestDataException(String.format("Can't get TestData '%s', parrent TestData is null", tdName));
		}
		String tdNameState = getStateTestDataName(tdName);
		if (!td.containsKey(tdNameState)) {
			tdNameState = tdName;
		}
		td = td.getTestData(tdNameState);
		log.info(String.format("==== Entered Test Data: '%s'. State Test Data: '%s'. Using Test Data: '%s'. ====", tdName, getStateTestDataName(tdName), tdNameState));
		return td;
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
	 * Should be used for creation of custom Underlying Home or Auto policies to use them during PUP policy creation.\
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
	 * @param tdPolicy - policy testdata
	 * @return policy number
	 */
	protected String createConversionPolicy(TestData tdPolicy) {
		return createConversionPolicy(getManualConversionInitiationTd(), tdPolicy);
	}

	/**
	 * Create Conversion Policy
	 *
	 * @param tdManualConversionInitiation - 'Initiate Manual Renewal Entry' action testdata
	 * @param tdPolicy - policy testdata
	 * @return policy number
	 */
	protected String createConversionPolicy(TestData tdManualConversionInitiation, TestData tdPolicy) {
		assertThat(getPolicyType()).as("PolicyType is not set").isNotNull();
		customer.initiateRenewalEntry().perform(tdManualConversionInitiation);
		log.info("Policy Creation Started...");
		getPolicyType().get().getDefaultView().fill(tdPolicy);
		if (PolicySummaryPage.buttonBackFromRenewals.isEnabled()) {
			PolicySummaryPage.buttonBackFromRenewals.click();
		}
		String policyNumber = PolicySummaryPage.labellinkPolicy.getValue();
		log.info("CONVERSION POLICY CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
		return policyNumber;
	}

	/**
	 * Create Conversion Policy using default TestData
	 *
	 * @return policy number
	 */
	protected String createConversionPolicy() {
		return createConversionPolicy(getManualConversionInitiationTd(), getConversionPolicyDefaultTD());
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

	protected TestData getLoginTD(Constants.UserGroups userGroups) {
		return loginUsers.getTestData(userGroups.get()).adjust(LoginPageMeta.STATES.getLabel(), getState());
	}

	private String getDefaultPolicy(PolicyType policyType) {
		assertThat(policyType).as("PolicyType is not set").isNotNull();
		String key = EntitiesHolder.makeDefaultPolicyKey(policyType, getState());
		String policyNumber = "";
		mainApp().close();
		synchronized (key) {
			if (EntitiesHolder.isEntityPresent(key)) {
				policyNumber = EntitiesHolder.getEntity(key);

			} else {
				mainApp().open();
				policyNumber = createNewDefaultPolicy(policyType);
				mainApp().close();
			}
		}
		return policyNumber;
	}

	private String createNewDefaultPolicy(PolicyType policyType) {
		assertThat(policyType).as("PolicyType is not set").isNotNull();
		String key = EntitiesHolder.makeDefaultPolicyKey(policyType, getState());
		String policyNumber;
		createCustomerIndividual();
		TestData td = getStateTestData(testDataManager.policy.get(policyType), "DataGather", "TestData");
		policyType.get().createPolicy(td);
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(key, policyNumber);
		return policyNumber;
	}

	private String getStateTestDataName(String tdName) {
		if (StringUtils.isNotBlank(getState())) {
			tdName = tdName + "_" + getState();
		} else {
			tdName = tdName + "_" + Constants.States.UT;
		}
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
