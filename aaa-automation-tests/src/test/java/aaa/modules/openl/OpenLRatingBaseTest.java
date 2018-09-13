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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.MapDifference;
import aaa.common.pages.MainPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.Groups;
import aaa.helpers.listeners.RatingEngineLogsGrabber;
import aaa.helpers.listeners.RatingEngineLogsHolder;
import aaa.helpers.openl.OpenLTestInfo;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;

public abstract class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);

	private static final Object TESTS_PREPARATIONS_LOCK = new Object();
	private static final Object RATING_LOCK = new Object();
	private static final String DATA_PROVIDER_NAME = "openLTestDataProvider";
	private static final String OPENL_GRAB_RATING_LOGS = PropertyProvider.getProperty(CsaaTestProperties.OPENL_ATTACH_RATING_LOGS);
	private static final boolean ARCHIVE_RATING_LOGS = Boolean.valueOf(PropertyProvider.getProperty(CsaaTestProperties.OPENL_ARCHIVE_RATING_LOGS));
	private static OpenLTestsManager openLTestsManager;
	private static RatingEngineLogsGrabber ratingEngineLogsGrabber = new RatingEngineLogsGrabber();

	/**
	 * Get base policy creation TestData which will be used as second argument in appropriate {@link TestDataGenerator#TestDataGenerator(String, TestData)} constructor.
	 * It should be used to add common test data to test specific rating test data generated by {@link TestDataGenerator#getRatingData(OpenLPolicy)}
	 * By default it returns {@code getPolicyTD("DataGather", "TestData")} test data but should be overridden in test class level if needed (e.g. to mask some tabs)
	 *
	 * @return base policy creation TestData
	 */
	protected TestData getRatingDataPattern() {
		return getPolicyTD();
	}

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

	@DataProvider(name = DATA_PROVIDER_NAME)
	protected Object[][] getOpenLTestData(ITestContext context) {
		OpenLTestInfo<P> testInfo = openLTestsManager.getTestInfo(context);
		if (testInfo.isFailed()) {
			Assert.fail(String.format("OpenL test preparation for file \"%s\" has been failed", testInfo.getOpenLFilePath()), testInfo.getException());
		}
		testInfo.setTestContext(context);

		//Sort policies list by effective date for further valid time shifts
		return testInfo.getOpenLPolicies().stream().sorted(Comparator.comparing(OpenLPolicy::getEffectiveDate))
				.map(p -> new Object[] {p.getState(), testInfo.getOpenLFilePath(), p.getNumber()}).toArray(Object[][]::new);
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
	public void totalPremiumVerificationTest(@Optional String state, String filePath, int policyNumber) {
		OpenLTestInfo<P> testInfo = openLTestsManager.getTestInfo(filePath);
		P openLPolicy = testInfo.getOpenLPolicy(policyNumber);
		Dollar actualPremium;

		TimeSetterUtil.getInstance().confirmDateIsAfter(openLPolicy.getEffectiveDate().atStartOfDay());
		mainApp().open();
		createOrOpenExistingCustomer(testInfo);

		log.info("Premium calculation verification initiated for test #{} and expected premium {} from \"{}\" OpenL file (pas-rating branch: {})",
				policyNumber, openLPolicy.getExpectedPremium(), filePath, testInfo.getOpenLFileBranch());
		String quoteNumber = createQuote(openLPolicy);
		log.info("Quote/policy created: {}", quoteNumber);

		synchronized (RATING_LOCK) {
			actualPremium = calculatePremium(openLPolicy);
			if (!openLPolicy.getExpectedPremium().equals(actualPremium)) {
				RatingEngineLogsHolder ratingLogs = ratingEngineLogsGrabber.grabRatingLogs();
				compareOpenLFieldsValues(ratingLogs, openLPolicy, quoteNumber);
				saveLogs(ratingLogs, testInfo.getTestContext(), openLPolicy.getNumber(), false);
			} else {
				grabAndSaveLogs(testInfo.getTestContext(), openLPolicy.getNumber(), true);
			}
		}
		assertThat(actualPremium).as("Total premium for quote/policy number %s is not equal to expected one", quoteNumber).isEqualTo(openLPolicy.getExpectedPremium());
	}

	/**
	 * Creates customer individual for set of tests from provided <b>testInfo</b> or opens existing one if it was already created
	 *
	 * @param testInfo OpenL tests holder with customer number and policy objects to be executed using this customer number
	 */
	protected void createOrOpenExistingCustomer(OpenLTestInfo<P> testInfo) {
		if (testInfo.getCustomerNumber() == null) {
			String customerNumber = createCustomerIndividual();
			testInfo.setCustomerNumber(customerNumber);
		} else {
			MainPage.QuickSearch.buttonSearchPlus.click();
			if (Page.dialogConfirmation.isPresent()) { // may occur if previous failed test started quote creation and didn't save it
				Page.dialogConfirmation.confirm();
			}
			SearchPage.openCustomer(testInfo.getCustomerNumber());
		}
	}

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

		/*Map<String, String> requestOpenLFields = getOpenLFieldsMapFromRequest(ratingLogsHolder);
		//Map<String, String> requestOpenLFields = getOpenLFieldsMapFromRequest(ratingLogsHolder, openLPolicy);
		if (MapUtils.isEmpty(requestOpenLFields)) {
			log.warn("OpenL fields values map from request log is empty, further analysis has been skipped");
			return;
		}
		Map<String, String> testOpenLFields = getOpenLFieldsMapFromTest(openLPolicy);*/

		//MapDifference<String, String> differences = Maps.difference(requestOpenLFields, testOpenLFields);
		@SuppressWarnings("unchecked")
		P openLPolicyFromRequest = (P) ratingLogsHolder.getRequestLog().getOpenLPolicyObject(openLPolicy.getClass());
		MapDifference<String, String> differences = openLPolicy.diff(openLPolicyFromRequest);

		if (differences.entriesDiffering().isEmpty()) {
			log.info("All common OpenL fields from rating json request and {} object from test excel file have same values", openLPolicy.getClass().getSimpleName());
		} else {
			StringBuilder diffMessage = new StringBuilder("There are differences between values of same OpenL fields from rating json request and test excel file. "
					+ "OpenL.Field.Path=[\"value from request\", \"value from test\"]:\n");
			for (Map.Entry<String, MapDifference.ValueDifference<String>> diff : differences.entriesDiffering().entrySet()) {
				diffMessage.append("    ").append(diff.getKey()).append("=[\"").append(diff.getValue().leftValue()).append("\", \"").append(diff.getValue().rightValue()).append("\"]\n");
			}
			log.warn(diffMessage.toString());

			if (!differences.entriesOnlyOnLeft().isEmpty()) {
				StringBuilder missedFieldsMessage = new StringBuilder("There are missed OpenL fields in policy object from test excel file:\n");
				for (Map.Entry<String, String> missedFields : differences.entriesOnlyOnLeft().entrySet()) {
					missedFieldsMessage.append("    ").append(missedFields.getKey()).append("=\"").append(missedFields.getValue()).append("\"\n");
				}
				log.warn(missedFieldsMessage.toString());
			}

			if (!differences.entriesOnlyOnRight().isEmpty()) {
				StringBuilder missedFieldsMessage = new StringBuilder("There are missed OpenL fields in rating json request:\n");
				for (Map.Entry<String, String> missedFields : differences.entriesOnlyOnRight().entrySet()) {
					missedFieldsMessage.append("    ").append(missedFields.getKey()).append("=\"").append(missedFields.getValue()).append("\"\n");
				}
				log.warn(missedFieldsMessage.toString());
			}
		}
	}

	/*@SuppressWarnings("unchecked")
	private Map<String, String> getOpenLFieldsMapFromRequest(RatingEngineLogsHolder ratingLogsHolder, P openLPolicy) {
		P openLPolicyFromRequest = (P) openLPolicy.createFrom(ratingLogsHolder.getRequestLog().getJsonElement());
		//openLPolicyFromRequest.sortInnerObjectsAccordingTo(openLPolicy);
		Map<String, String> openLFieldsMap = new HashMap<>(openLPolicyFromRequest.getOpenLFieldsMap());
		openLFieldsMap.entrySet().removeIf(e -> e.getKey().startsWith("runtimeContext.") || e.getKey().startsWith("variationPack."));
		return openLFieldsMap;
	}*/

	/*protected Map<String, String> getOpenLFieldsMapFromRequest(RatingEngineLogsHolder ratingLogsHolder) {
		Map<String, String> openLFieldsMap = new HashMap<>(ratingLogsHolder.getRequestLog().getOpenLFieldsMap());
		openLFieldsMap.entrySet().removeIf(e -> e.getKey().startsWith("runtimeContext.") || e.getKey().startsWith("variationPack."));
		return openLFieldsMap;
	}*/

	/*protected Map<String, String> getOpenLFieldsMapFromTest(P openLPolicy) {
		Map<String, String> openLFieldsMap = openLPolicy.getOpenLFieldsMap();
		openLFieldsMap.entrySet().removeIf(e -> e.getValue() == null || "null".equalsIgnoreCase(e.getValue())); // usually we don't care about null values of OpenL fields in test file during comparision
		openLFieldsMap.remove("policy.policyNumber"); // policy number in test always differs from value in rating request log
		return openLFieldsMap;
	}*/

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
}
