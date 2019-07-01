package aaa.modules.openl;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.util.Comparator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.MapDifference;
import aaa.common.pages.MainPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.Groups;
import aaa.helpers.logs.RatingEngineLogsGrabber;
import aaa.helpers.logs.RatingEngineLogsHolder;
import aaa.helpers.openl.OpenLTestInfo;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;

public abstract class OpenLRatingBaseTest<P extends OpenLPolicy> extends BaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);
	protected static ThreadLocal<PolicyType> policyType = new ThreadLocal<>();
	protected static ThreadLocal<IPolicy> policy = new ThreadLocal<>();
	protected static ThreadLocal<TestData> tdPolicy = new ThreadLocal<>();

	private static final Object TESTS_PREPARATIONS_LOCK = new Object();
	private static final Object RATING_LOCK = new Object();
	private static final String DATA_PROVIDER_NAME = "openLTestDataProvider";
	private static final String OPENL_GRAB_RATING_LOGS = PropertyProvider.getProperty(CsaaTestProperties.OPENL_ATTACH_RATING_LOGS);
	private static final boolean ARCHIVE_RATING_LOGS = Boolean.valueOf(PropertyProvider.getProperty(CsaaTestProperties.OPENL_ARCHIVE_RATING_LOGS));
	private static OpenLTestsManager openLTestsManager;
	private static RatingEngineLogsGrabber ratingEngineLogsGrabber = new RatingEngineLogsGrabber();

	/**
	 * Performs test preparations for all suites and their child suites at once such as creation of OpenL policy objects from excel files (remote or local) and mock files updates on endpoint (if needed)
	 *
	 * @param context injected TestNG context with all test run information
	 */
	@BeforeSuite
	protected void testsPreparations(ITestContext context) {
		synchronized (TESTS_PREPARATIONS_LOCK) {
			if (openLTestsManager == null) {
				openLTestsManager = new OpenLTestsManager(context);
				openLTestsManager.updateMocks();
			}
		}
	}

	@Override
	protected PolicyType getPolicyType() {
		return policyType.get();
	}

	protected void setPolicyType(PolicyType type) {
		policyType.set(type);
		policy.set(type.get());
		tdPolicy.set(testDataManager.policy.get(type));
	}

	/**
	 * Get base policy creation TestData which will be used as second argument in appropriate {@link TestDataGenerator#TestDataGenerator(String, TestData)} constructor.
	 * It should be used to add common test data to test specific rating test data generated by {@link TestDataGenerator#getRatingData(OpenLPolicy)}
	 * By default it returns {@code getPolicyTD("DataGather", "TestData")} test data but should be overridden in test class level if needed (e.g. to mask some tabs)
	 *
	 * @return base policy creation TestData
	 */
	protected TestData getRatingDataPattern() {
		return getPolicyTD("DataGather", "TestData");
	}

	protected TestData getPolicyTD(String fileName, String tdName) {
		return getStateTestData(tdPolicy.get(), fileName, tdName);
	}

	@DataProvider(name = DATA_PROVIDER_NAME)
	protected Object[][] getOpenLTestData(ITestContext context) {
		OpenLTestInfo<P> testInfo = openLTestsManager.getTestInfo(context);
		if (testInfo.isFailed()) {
			Assert.fail(String.format("OpenL test preparation for file [%s] has been failed", testInfo.getOpenLFilePath()), testInfo.getException());
		}
		testInfo.setTestContext(context);

		//Sort policies list by effective date for further valid time shifts
		return testInfo.getOpenLPolicies().stream().sorted(Comparator.comparing(OpenLPolicy::getEffectiveDate))
				.map(p -> new Object[] {p.getState(), p.getTestPolicyType(), testInfo.getOpenLFilePath(), p.getNumber()}).toArray(Object[][]::new);
	}

	/**
	 * Test method for policy creation, premium calculation and Total Premium verification
	 *
	 * @param state test state
	 * @param filePath path to the OpenL test file
	 * @param policyNumber policy number from OpenL test file which total premium should be verified
	 */
	@Parameters({"state", "filePath", "policyNumber"})
	@Test(groups = {Groups.OPENL, Groups.HIGH}, dataProvider = DATA_PROVIDER_NAME)
	public void totalPremiumVerificationTest(@Optional String state, PolicyType testPolicyType, String filePath, int policyNumber) {
		OpenLTestInfo<P> testInfo = openLTestsManager.getTestInfo(filePath);
		P openLPolicy = testInfo.getOpenLPolicy(policyNumber);
		setState(openLPolicy.getState());
		setPolicyType(testPolicyType);
		Dollar expectedPremium = openLPolicy.getExpectedPremium();
		Dollar actualPremium;

		TimeSetterUtil.getInstance().confirmDateIsAfter(openLPolicy.getEffectiveDate().atStartOfDay());
		mainApp().open();
		createOrOpenExistingCustomer(testInfo, policyNumber);

		log.info("Premium calculation verification initiated for test #{} and expected premium {} from \"{}\" OpenL {}",
				policyNumber, expectedPremium, filePath, testInfo.isLocalFile() ? "local file" : "remote file, pas-rating branch: " + testInfo.getOpenLFileBranch());
		String quoteNumber = createQuote(openLPolicy);
		log.info("Quote/policy created: {}", quoteNumber);

		synchronized (RATING_LOCK) {
			actualPremium = calculatePremium(openLPolicy);
			log.info("Total premium is calculated\n    ACTUAL:   {}\n    EXPECTED: {}", actualPremium, expectedPremium);
			if (!expectedPremium.equals(actualPremium)) {
				RatingEngineLogsHolder ratingLogs = ratingEngineLogsGrabber.grabRatingLogs();
				compareOpenLFieldsValues(ratingLogs, openLPolicy, quoteNumber);
				saveLogs(ratingLogs, testInfo.getTestContext(), openLPolicy.getNumber(), false);
			} else {
				grabAndSaveLogs(testInfo.getTestContext(), openLPolicy.getNumber(), true);
			}
		}
		assertThat(actualPremium).as("Total premium for quote/policy number %s is not equal to expected one", quoteNumber).isEqualTo(expectedPremium);
	}

	/**
	 * Creates customer individual for set of tests from provided <b>testInfo</b> or opens existing one if it was already created
	 *
	 * @param testInfo OpenL tests holder with customer number and policy objects to be executed using this customer number
	 */
	protected void createOrOpenExistingCustomer(OpenLTestInfo<P> testInfo, int policyNumber) {
		MainPage.QuickSearch.buttonSearchPlus.click();
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}

		P openLPolicy = testInfo.getOpenLPolicy(policyNumber);
		if (testInfo.getCustomerNumber() == null || openLPolicy.getTestPolicyType().isAutoPolicy()) {
			String customerNumber = createCustomerIndividual(openLPolicy);
			testInfo.setCustomerNumber(customerNumber);
		} else {
			SearchPage.openCustomer(testInfo.getCustomerNumber());
		}
	}

	protected abstract String createCustomerIndividual(P openLPolicy);

	/**
	 * This method should generate appropriate test data to create quote or policy (and/or perform endorsement(s), renewal(s) if needed)
	 *
	 * @param openLPolicy openl policy object as a representation of row from {@link OpenLFile#POLICY_SHEET_NAME} excel sheet
	 * @return quote or policy number
	 */
	protected abstract String createQuote(P openLPolicy);

	/**
	 * Retrieves total premium from UI and returns it
	 *
	 * @param openLPolicy openl policy object as a representation of row from {@link OpenLFile#POLICY_SHEET_NAME} excel sheet
	 * @return total premium from UI
	 */
	protected abstract Dollar calculatePremium(P openLPolicy);

	protected void compareOpenLFieldsValues(RatingEngineLogsHolder ratingLogsHolder, P openLPolicy, String quoteNumber) {
		if (!ratingLogsHolder.getRequestLog().getLogContent().contains(quoteNumber)) {
			log.warn("There is no policy number {} in retrieved rating request log, further analysis has been skipped", quoteNumber);
			return;
		}

		@SuppressWarnings("unchecked")
		P openLPolicyFromRequest = (P) ratingLogsHolder.getRequestLog().getOpenLPolicyObject(openLPolicy.getClass());
		MapDifference<String, String> differences = openLPolicyFromRequest.diff(openLPolicy);

		if (differences.entriesDiffering().isEmpty()) {
			log.info("All common OpenL fields from rating json request and {} object from test excel file have same values", openLPolicy.getClass().getSimpleName());
		} else {
			StringBuilder diffMessage = new StringBuilder("There are differences between values of same OpenL fields from rating json request and test excel file. "
					+ "OpenL.Field.Path=[\"value from request\", \"value from excel file\"]:\n");
			for (Map.Entry<String, MapDifference.ValueDifference<String>> diff : differences.entriesDiffering().entrySet()) {
				diffMessage.append("    ").append(diff.getKey()).append("=[\"").append(diff.getValue().leftValue()).append("\", \"").append(diff.getValue().rightValue()).append("\"]\n");
			}
			log.warn(diffMessage.toString());

			if (!differences.entriesOnlyOnLeft().isEmpty()) {
				printMissedFields(differences.entriesOnlyOnLeft(), "There are missed OpenL fields in policy object from test excel file:");
			}

			if (!differences.entriesOnlyOnRight().isEmpty()) {
				printMissedFields(differences.entriesOnlyOnRight(), "There are missed OpenL fields in rating json request:");
			}

			log.info("----------------------------------------------------------------");
			log.info("All OpenL field values from json request:\n{}", openLPolicyFromRequest);
			log.info("All OpenL field values from excel file:\n{}", openLPolicy);
		}
	}

	protected void grabAndSaveLogs(ITestContext testContext, int openLPolicyNumber, boolean isActualEqualToExpectedPremium) {
		saveLogs(null, testContext, openLPolicyNumber, isActualEqualToExpectedPremium);
	}

	protected void saveLogs(RatingEngineLogsHolder ratingLogsHolder, ITestContext testContext, int openLPolicyNumber, boolean isActualEqualToExpectedPremium) {
		if ("true".equalsIgnoreCase(OPENL_GRAB_RATING_LOGS) || "always".equalsIgnoreCase(OPENL_GRAB_RATING_LOGS) || "all".equalsIgnoreCase(OPENL_GRAB_RATING_LOGS) ||
				"failed".equalsIgnoreCase(OPENL_GRAB_RATING_LOGS) && !isActualEqualToExpectedPremium) {

			if (ratingLogsHolder == null) {
				ratingLogsHolder = ratingEngineLogsGrabber.grabRatingLogs();
			}

			String logPath = ratingEngineLogsGrabber.makeDefaultOpenLRequestLogPath(testContext.getCurrentXmlTest(), openLPolicyNumber);
			File ratingRequestLog = ratingLogsHolder.getRequestLog().dump(logPath, ARCHIVE_RATING_LOGS);
			if (ratingRequestLog != null) {
				testContext.setAttribute(RatingEngineLogsGrabber.RATING_REQUEST_TEST_CONTEXT_ATTR_NAME, ratingRequestLog);
			}

			logPath = ratingEngineLogsGrabber.makeDefaultOpenLResponseLogPath(testContext.getCurrentXmlTest(), openLPolicyNumber);
			File ratingResponseLog = ratingLogsHolder.getResponseLog().dump(logPath, ARCHIVE_RATING_LOGS);
			if (ratingRequestLog != null) {
				testContext.setAttribute(RatingEngineLogsGrabber.RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME, ratingResponseLog);
			}
		}
	}

	private void printMissedFields(Map<String, String> missedFieldsMap, String message) {
		StringBuilder missedFieldsMessage = new StringBuilder(message + "\n");
		for (Map.Entry<String, String> missedFields : missedFieldsMap.entrySet()) {
			missedFieldsMessage.append("    ").append(missedFields.getKey()).append("=\"").append(missedFields.getValue()).append("\"\n");
		}
		log.warn(missedFieldsMessage.toString());
	}
}
