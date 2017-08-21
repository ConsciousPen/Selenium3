package com.exigen.istf.exec.testng.impl;

import com.exigen.istf.exec.core.TestCoordinatorException;
import com.exigen.istf.exec.core.TimedTestContext;
import com.exigen.istf.exec.core.coordinator.MainCoordinator;
import com.exigen.istf.exec.core.impl.TestOutcome;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Coordinator for TestNG (Singleton)
 */
public final class TestNGCoordinator implements IExecutionListener, IConfigurationListener, ISuiteListener, ITestListener { // NOPMD

	private static final Logger log = LoggerFactory.getLogger(TestNGCoordinator.class);
	/**
	 * The name of parameter for timeshift scenario mode
	 */
	private static final String TIMESHIFT_SCENARIO_MODE_PARAMETER = "timeshift-scenario-mode";
	/**
	 * The value for timeshift suite parallel mode
	 */
	private static final String TIMESHIFT_SUITE_PARALLEL_MODE = "tests";
	/**
	 * The instance lock
	 */
	private static final Object INSTANCE_LOCK = new Object();
	/**
	 * The TestNGCoordinator instance (within 'INSTANCE_LOCK')
	 */
	private static volatile TestNGCoordinator instance;
	/**
	 * The start TestNG lock
	 */
	private static final Object START_TESTNG_LOCK = new Object();
	/**
	 * Flag indicating that suite has started (within 'START_TESTNG_LOCK')
	 */
	private static volatile boolean testNGStarted;

	/**
	 * MainCoordinator
	 */
	private final MainCoordinator mainCoordinator;
	/**
	 * The start suites lock
	 */
	private final Object startSuitesLock = new Object();
	/**
	 * The started suites (within 'startSuitesLock')
	 */
	private final Map<ISuite, Boolean> startedSuites = new IdentityHashMap<ISuite, Boolean>(); // TODO

	/**
	 * The lock used to track test coordination
	 */
	private final Object testContextLock = new Object();
	/**
	 * The context associated with the test (within 'testLock')
	 */
	private final Map<Long, ThreadContext> testContexts = new HashMap<Long, ThreadContext>();
	/**
	 * The test counter, that allows to distinguish different tests in the logs (within 'testLock')
	 */
	private long testCounter;
	/**
	 * The started methods (within 'testLock')
	 */
	private final Set<String> methodExpanded = new HashSet<String>();
	/**
	 * The finished tests (within 'testLock')
	 */
	private final Set<ITestResult> finishedTests = new HashSet<ITestResult>();

	/**
	 * The private constructor. It ensures that only one instance is created
	 */
	private TestNGCoordinator() {
		try {
			mainCoordinator = new MainCoordinator();
			mainCoordinator.start();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TestCoordinatorException(e.getMessage(), e);
		}
	}

	/**
	 * Initialize and get TestNGCoordinator instance
	 * @return the TestNGCoordinator (singleton) instance
	 */
	public static TestNGCoordinator getInstance() {
		if (instance == null) {
			synchronized (INSTANCE_LOCK) {
				if (instance == null) {
					instance = new TestNGCoordinator();
				}
			}
		}
		return instance;
	}

	/**
	 * Get TestNGCoordinator instance
	 * @return the TestNGCoordinator (singleton) instance
	 */
	public static TestNGCoordinator get() {
		return instance;
	}

	public static boolean isTestNGStarted() {
		return testNGStarted;
	}

	// {{{ IExecutionListener implementation
	@SuppressWarnings({"unchecked", "deprecation"})
	@SuppressFBWarnings(value = "DP_DO_INSIDE_DO_PRIVILEGED")
	@Override
	public void onExecutionStart() {
		synchronized (START_TESTNG_LOCK) {
			if (testNGStarted) {
				throw new TestCoordinatorException("TestNG was started");
			}

			int suiteCount;
			try {
				TestNG testNG = TestNG.getDefault();
				testNG.setThreadCount(Integer.MAX_VALUE);
				testNG.setSuiteThreadPoolSize(Integer.MAX_VALUE);
				System.out.println("THREAD POOL SIZE IS!!!!!!!!! " + testNG.getSuiteThreadPoolSize());

				Field suitesField = TestNG.class.getDeclaredField("m_suites");
				suitesField.setAccessible(true);
				List<XmlSuite> xmlSuites = (List<XmlSuite>) suitesField.get(testNG);

				suiteCount = countSuiteFiles(new HashSet<String>(), new HashMap<String, String>(), xmlSuites);

			} catch (Throwable t) { // NOPMD
				log.error(t.getMessage(), t);
				throw new TestCoordinatorException("Failed to acquire list of of suites", t);
			}

			mainCoordinator.getTestCoordinator().beforeAllTests(suiteCount);

			testNGStarted = true;
		}
	}

	/**
	 * Get all suite files (recursive)
	 *
	 * @param suiteFiles the set of suite file names
	 * @param suiteNames the set of suite names
	 * @param xmlSuites  the suite list
	 * @return the amount of suite files
	 */
	private int countSuiteFiles(Set<String> suiteFiles, Map<String, String> suiteNames, List<XmlSuite> xmlSuites) { // TODO
		int count = 0;

		for (XmlSuite xmlSuite : xmlSuites) {
			if (suiteFiles.contains(xmlSuite.getFileName())) {
				continue;
			}
			suiteFiles.add(xmlSuite.getFileName());

			count += countSuiteFiles(suiteFiles, suiteNames, xmlSuite.getChildSuites());

			if (suiteNames.containsKey(xmlSuite.getName())) {
				log.warn("The suite file {} has name '{}' that was also used by the suite file {}",
						xmlSuite.getFileName(), xmlSuite.getName(), suiteNames.get(xmlSuite.getName()));
				continue;
			}
			suiteNames.put(xmlSuite.getName(), xmlSuite.getFileName());

			if (TimeShiftScenarioMode.getMode(xmlSuite, false) != TimeShiftScenarioMode.NONE) {
				if (!xmlSuite.getSuiteFiles().isEmpty()) {
					log.warn("The suite '{}' with tests is referencing suite files, the tests will be treated as non-PEF tests: {}",
							xmlSuite.getName(), xmlSuite.getFileName());
					continue;
				}

				count++;
			}
		}

		return count;
	}

	@Override
	public void onExecutionFinish() {
		synchronized (START_TESTNG_LOCK) {
			if (!testNGStarted) {
				throw new TestCoordinatorException("TestNG was not started");
			}

			mainCoordinator.getTestCoordinator().afterAllTests();

			testNGStarted = false;

			mainCoordinator.stop();
		}
	}
	// }}} IExecutionListener implementation


	// {{{ IConfigurationListener implementation
	@Override
	public void onConfigurationSuccess(ITestResult result) {
		synchronized (testContextLock) {
			TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(result.getMethod());
			Long threadId = getCurrentThreadId();
			log.debug("TestNG.onConfigurationSuccess (threadId={}): '{}' (scenario mode: {})", threadId, result.getName(), scenarioMode);

			ThreadContext threadContext = testContexts.get(threadId);

			if (threadContext == null) { // TODO
				log.warn("TestNG.onConfigurationSuccess (threadId={}). ThreadContext == null", threadId);
				return;
			}

			TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

			if (testNgContext == null || testNgContext.isFinished()) {
				throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s)", testNgContext)); // TODO
			}

			if (scenarioMode == TimeShiftScenarioMode.METHOD) {
				handleAfterMethod(result, testNgContext, threadContext);
			}
		}
	}

	@Override
	public void onConfigurationFailure(ITestResult result) {
		synchronized (testContextLock) {
			TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(result.getMethod());
			Long threadId = getCurrentThreadId();
			log.debug("TestNG.onConfigurationFailure (threadId={}): '{}' (scenario mode: {})", threadId, result.getName(), scenarioMode);

			ThreadContext threadContext = testContexts.get(threadId);

			if (threadContext == null) { // TODO
				log.warn("TestNG.onConfigurationFailure (threadId={}). ThreadContext == null", threadId);
				return;
			}

			TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

			if (testNgContext == null || testNgContext.isFinished()) {
				throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s)", testNgContext)); // TODO
			}

			if (testNgContext.getTestOutcome() == TestOutcome.SUCCESS) {
				testNgContext.setTestOutcome(TestOutcome.ERROR);
				testNgContext.setFailure(result.getThrowable());
			}

			if (scenarioMode == TimeShiftScenarioMode.METHOD) {
				handleAfterMethod(result, testNgContext, threadContext);
			}
		}
	}

	@Override
	public void onConfigurationSkip(ITestResult result) {
		synchronized (testContextLock) {
			TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(result.getMethod());
			Long threadId = getCurrentThreadId();
			log.debug("TestNG.onConfigurationSkip (threadId={}): '{}' (scenario mode: {})", threadId, result.getName(), scenarioMode);

			ThreadContext threadContext = testContexts.get(threadId);

			if (threadContext == null) { // TODO
				log.warn("TestNG.onConfigurationSkip (threadId={}). ThreadContext == null", threadId);
				return;
			}

			TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

			if (testNgContext == null || testNgContext.isFinished()) {
				throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s)", testNgContext)); // TODO
			}

			if (scenarioMode == TimeShiftScenarioMode.METHOD) {
				handleAfterMethod(result, testNgContext, threadContext);
			}
		}
	}
	// }}} IConfigurationListener implementation

	/**
	 * Handle after method
	 *
	 * @param testResult  the test result
	 * @param testNgContext the testNgTestContext
	 */
	private void handleAfterMethod(ITestResult testResult, TestNGTestContext testNgContext, ThreadContext threadContext) {
		if (testResult.getMethod().isAfterMethodConfiguration()) {
			testNgContext.decrementAfterMethodCount();
			assert testNgContext.isPossibleAfterMethodCount();
		}
		if (testNgContext.isZeroAfterMethodCount()) {
			processAfterMethod(testNgContext, threadContext);
		}
	}

	private void processAfterMethod(TestNGTestContext testNgContext, ThreadContext threadContext) {
		if (testNgContext.getTestOutcome() == null) {
			log.warn("testNgContext.getTestOutcome() == null, threadId={}, threadContext={}", getCurrentThreadId(), threadContext);
		}
		mainCoordinator.getTestCoordinator().afterTest(testNgContext.getTestContext(), testNgContext.getTestOutcome(), testNgContext.getFailure());
		testNgContext.setFinished();
	}


	// {{{ ISuiteListener implementation
	@Override
	public void onStart(ISuite suite) { // NOPMD
		synchronized (startSuitesLock) {
			if (startedSuites.containsKey(suite)) {
				return;
			}
			startedSuites.put(suite, Boolean.TRUE);
		}

		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(suite);
		Long threadId = getCurrentThreadId();
		log.info("TestNG. Suite started (threadId={}): '{}' (scenario mode: {})", threadId, suite.getName(), scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.METHOD) {
			// this method counts approximate number of the tests, it does not factors in invocation counts and parameters
			// it is later expanded in onTestStart(ITestResult result)
			if (!TIMESHIFT_SUITE_PARALLEL_MODE.equals(suite.getParallel())) {
				log.warn(String.format("TestNG. The suite must have specified parallel='%s'", TIMESHIFT_SUITE_PARALLEL_MODE));
			}

			int testCount = 0;
			for (ITestNGMethod method : suite.getAllMethods()) {
				if (method.isTest()) {
					testCount++;
				}
			}

			if (testCount > 0) {
				mainCoordinator.getTestCoordinator().expandTest(suite.getName(), testCount);
			} else {
				mainCoordinator.getTestCoordinator().skipTests(1);
			}
			return;
		}

		if (scenarioMode == TimeShiftScenarioMode.TEST) {
			// this method counts approximate number of the tests, it does not factors in invocation counts and parameters
			// it is later expanded in onTestStart(ITestResult result)
			if (!TIMESHIFT_SUITE_PARALLEL_MODE.equals(suite.getParallel())) {
				log.warn(String.format("TestNG. The suite must have specified parallel='%s'", TIMESHIFT_SUITE_PARALLEL_MODE));
			}

			int testCount = suite.getXmlSuite().getTests().size();

			if (testCount > 0) {
				mainCoordinator.getTestCoordinator().expandTest(suite.getName(), testCount);
			} else {
				mainCoordinator.getTestCoordinator().skipTests(1);
			}
			return;
		}

		if (scenarioMode == TimeShiftScenarioMode.SUITE) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext != null) {
					log.debug("The threadContext already exists: " + threadContext); // TODO
				}

				TimedTestContext newTimedTestContext = mainCoordinator.getTestCoordinator().beforeTest(suite.getName());

				TestNGTestContext newTestNgContext = new TestNGTestContext(newTimedTestContext, 0);

				if (threadContext == null) {
					testContexts.put(threadId, new ThreadContext(threadId, newTestNgContext));
				} else {
					threadContext.addNewTestNgContext(newTestNgContext);
					log.warn("Multiple TestNGTestContexts (in one thread) (onStart(ISuite) method) threadContext={}", threadContext);
				}
			}
			return;
		}

		if (scenarioMode != TimeShiftScenarioMode.NONE) {
			String errorMsg = String.format("Unknown suite type: %s file=%s", suite.getName(), suite.getXmlSuite().getFileName());
			log.error(errorMsg);
			throw new TestCoordinatorException(errorMsg);
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		synchronized (startSuitesLock) {
			if (startedSuites.containsKey(suite) && !startedSuites.get(suite)) {
				return;
			}
			startedSuites.put(suite, Boolean.FALSE);
		}

		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(suite);
		Long threadId = getCurrentThreadId();
		log.info("TestNG. Suite finished (threadId={}): '{}' (scenario mode: {})", threadId, suite.getName(), scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.SUITE) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext == null) {
					String errorMsg = String.format("The threadContext == null (threadId=%s, ISuite=%s)", threadId, suite.getName());
					log.error(errorMsg);
					throw new TestCoordinatorException(errorMsg); // TODO
				}

				TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

				if (testNgContext == null || testNgContext.isFinished()) {
					throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s) ", testNgContext)); // TODO
				}

				processAfterMethod(testNgContext, threadContext);
			}
		}
	}
	// }}} ISuiteListener implementation


	// {{{ ITestListener implementation
	@Override
	public void onTestStart(ITestResult result) {
		ITestNGMethod method = result.getMethod();
		if (!method.isTest()) {
			log.warn("TestNG. Method is not test: " + method);
			return;
		}

		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(method);
		Long threadId = getCurrentThreadId();
		log.debug("TestNG.onTestStart (threadId={}): '{}' (scenario mode: {})", threadId, result.getName(), scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.METHOD) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext != null) {
					log.debug("The threadContext already exists: " + threadContext); // TODO
				}

				String methodKey = methodKey(method);

				if (!methodExpanded.contains(methodKey)) {
					int newCount = getOriginalInvocationCount(method) * method.getParameterInvocationCount();
					mainCoordinator.getTestCoordinator().expandTest(method.toString(), newCount);
					methodExpanded.add(methodKey);
				}

				TimedTestContext newTimedTestContext = mainCoordinator.getTestCoordinator().beforeTest(generateReportTestName(result));

				TestNGTestContext newTestNgContext = new TestNGTestContext(newTimedTestContext, getAfterMethodCount(method));

				if (threadContext == null) {
					testContexts.put(threadId, new ThreadContext(threadId, newTestNgContext));
				} else {
					threadContext.addNewTestNgContext(newTestNgContext);
					log.warn("Multiple TestNGTestContexts (in one thread) (onTestStart(ITestResult) method) threadContext={}", threadContext);
				}
			}
		}
	}

	/**
	 * Get invocation count that was specified using annotation
	 *
	 * @param method the method to check
	 * @return the invocation count
	 */
	private int getOriginalInvocationCount(ITestNGMethod method) {
		Test annotation = method.getConstructorOrMethod().getMethod().getAnnotation(Test.class);
		return annotation != null ? annotation.invocationCount() : 1;
	}

	/**
	 * Get count for the after methods
	 *
	 * @param method the method to invoke
	 * @return the count of after methods
	 */
	private int getAfterMethodCount(ITestNGMethod method) {
		ITestClass testClass = method.getTestClass();
		int count = 0;

		for (ITestNGMethod afterMethod : testClass.getAfterTestMethods()) {
			if (afterMethod.canRunFromClass(testClass)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * The method key for TestNG tests, it allows to group tests by invocation counts and parameters
	 *
	 * @param method the method to get key for
	 * @return the method key
	 */
	private String methodKey(ITestNGMethod method) {
		String testName = getTestName(method);
		return String.format("%s/%s:%s:%s",
				method.getConstructorOrMethod().getMethod(),
				method.getTestClass().getName(),
				testName,
				method.getTestClass().getXmlTest().getSuite().getFileName());
	}

	/**
	 * Generate test name for reports basing on the test result.
	 *
	 * @param result the test result
	 * @return the generated name
	 */
	private String generateReportTestName(ITestResult result) {
		Object[] parameters = result.getParameters();
		String testName = getTestName(result.getMethod());
		testCounter++;

		return String.format("%s.%s:%s%s%s",
				result.getTestClass().getName(),
				result.getName(),
				testCounter,
				(testName != null) ? " \'" + testName + "\'" : "",
				(parameters != null && parameters.length != 0) ? " " + Arrays.toString(parameters) : "");
	}

	/**
	 * Get test name in the test suite
	 *
	 * @param method the method to examine
	 * @return the test name
	 */
	private String getTestName(ITestNGMethod method) {
		String testName = method.getTestClass().getXmlTest().getName();
		if (testName == null) {
			testName = method.getTestClass().getTestName();
		}
		return testName;
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		onTestFinish(result, "SUCCESS");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		onTestFinish(result, "FAILURE");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		onTestFinish(result, "SKIPPED");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		onTestFinish(result, "FAILED");
	}

	/**
	 * The utility method that is invoked when the test is finished
	 *
	 * @param result the test result to to check
	 */
	private void onTestFinish(ITestResult result, String resultOutput) {
		ITestNGMethod method = result.getMethod();
		if (!method.isTest()) {
			log.warn("TestNG. Method is not test: " + method);
			return;
		}

		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(method);
		Long threadId = getCurrentThreadId();
		log.debug("TestNG.onTestFinish (threadId={}): '{}' (result={}, scenario mode: {})", threadId, result.getName(), resultOutput, scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.METHOD) {
			synchronized (testContextLock) {
				if (finishedTests.contains(result)) {
					log.warn("Test was finished: " + method);
					return;
				}
				finishedTests.add(result);

				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext == null) {
					String errorMsg = String.format("The threadContext == null (threadId=%s, ITestResult=%s)", threadId, result);
					log.error(errorMsg);
					throw new TestCoordinatorException(errorMsg); // TODO
				}

				TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

				if (testNgContext == null || testNgContext.isFinished()) {
					throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s) ", testNgContext)); // TODO
				}

				updateTestOutcome(result, testNgContext);

				if (testNgContext.isZeroAfterMethodCount()) {
					processAfterMethod(testNgContext, threadContext);
				}

				return;
			}
		}

		if (scenarioMode == TimeShiftScenarioMode.SUITE || scenarioMode == TimeShiftScenarioMode.TEST) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext == null) {
					String errorMsg = String.format("The threadContext == null (threadId=%s, ITestResult=%s)", threadId, result);
					log.error(errorMsg);
					throw new TestCoordinatorException(errorMsg); // TODO
				}

				TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

				if (testNgContext == null || testNgContext.isFinished()) {
					throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s) ", testNgContext)); // TODO
				}

				updateTestOutcome(result, testNgContext);
			}
		}
	}

	/**
	 * Update test outcome according to result
	 *
	 * @param result  the result
	 * @param testNgContext the context
	 */
	private void updateTestOutcome(ITestResult result, TestNGTestContext testNgContext) {
		TestOutcome newTestOutcome;
		Throwable newFailure;

		switch (result.getStatus()) {
			case ITestResult.SUCCESS:
				newTestOutcome = TestOutcome.SUCCESS;
				newFailure = null;
				break;
			case ITestResult.FAILURE:
				newTestOutcome = TestOutcome.FAILED;
				newFailure = result.getThrowable();
				break;
			case ITestResult.SKIP:
				newTestOutcome = TestOutcome.SKIPPED;
				newFailure = result.getThrowable();
				break;
			default:
				log.error("Unable to decode test resultStatus: " + result);
				newTestOutcome = TestOutcome.ERROR;
				newFailure = null;
				break;
		}

		TestOutcome contextTestOutcome = testNgContext.getTestOutcome();

		if (contextTestOutcome == null ||
				contextTestOutcome == TestOutcome.SKIPPED ||
				contextTestOutcome == TestOutcome.SUCCESS && newTestOutcome != TestOutcome.SKIPPED) {
			testNgContext.setTestOutcome(newTestOutcome);
		}

		if (testNgContext.getFailure() == null) {
			testNgContext.setFailure(newFailure);
		}
	}

	/**
	 * Finish executing the method if no after methods remaining
	 *
	 * @param context the context
	 */
	@Override
	public void onStart(ITestContext context) {
		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(context.getSuite());
		Long threadId = getCurrentThreadId();
		log.debug("TestNG.onTestContextStart (threadId={}): '{}' (scenario mode: {})", threadId, context.getName(), scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.TEST) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext != null) {
					log.debug("The threadContext already exists: " + threadContext); // TODO
				}

				String testName = String.format("%s # %s", context.getSuite().getName(), context.getName());
				TimedTestContext newTimedTestContext = mainCoordinator.getTestCoordinator().beforeTest(testName);

				TestNGTestContext newTestNgContext = new TestNGTestContext(newTimedTestContext, 0);

				if (threadContext == null) {
					testContexts.put(threadId, new ThreadContext(threadId, newTestNgContext));
				} else {
					threadContext.addNewTestNgContext(newTestNgContext);
					log.warn("Multiple TestNGTestContexts (in one thread) (onStart(ITestContext) method) threadContext={}", threadContext);
				}
			}
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		TimeShiftScenarioMode scenarioMode = TimeShiftScenarioMode.getMode(context.getSuite());
		Long threadId = getCurrentThreadId();
		log.debug("TestNG.onTestContextFinish (threadId={}): '{}' (scenario mode: {})", threadId, context.getName(), scenarioMode);

		if (scenarioMode == TimeShiftScenarioMode.TEST) {
			synchronized (testContextLock) {
				ThreadContext threadContext = testContexts.get(threadId);

				if (threadContext == null) {
					String errorMsg = String.format("The threadContext == null (threadId=%s, ITestContext=%s)", threadId, context);
					log.error(errorMsg);
					throw new TestCoordinatorException(errorMsg); // TODO
				}

				TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

				if (testNgContext == null || testNgContext.isFinished()) {
					throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s) ", testNgContext)); // TODO
				}

				processAfterMethod(testNgContext, threadContext);
			}
		}
	}
	// }}} ITestListener implementation

	/**
	 * @return the start time for tests
	 */
	public DateTime getStartTime() {
		return mainCoordinator.getPhaseCoordinator().getGlobalStartTime();
	}

	/**
	 * @return the context associated with the test
	 */
	public TimedTestContext getContext() {
		return getContext(getCurrentThreadId());
	}

	/**
	 * @param testContextThreadId  the testContext threadId
	 * @return the context associated with the test
	 */
	public TimedTestContext getContext(long testContextThreadId) {
		synchronized (testContextLock) {
			ThreadContext threadContext = testContexts.get(testContextThreadId);

			if (threadContext == null) {
				String errorMsg = "The context could not be obtained for the test. " +
						"Make sure that you have added a TimeShiftTestNGListener in suite.xml file. " +
						"(testContextThreadId=" + testContextThreadId + ")";
				log.error(errorMsg);
				throw new TestCoordinatorException(errorMsg);
			}

			TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

			if (testNgContext == null || testNgContext.isFinished()) {
				throw new TestCoordinatorException(String.format("The testNgContext == null (or finished) (%s) ", testNgContext)); // TODO
			}

			return testNgContext.getTestContext();
		}
	}

	/**
	 * @return the available test context
	 */
	public boolean isTestContextAvailable() {
		return isTestContextAvailable(getCurrentThreadId());
	}

	/**
	 * @param testContextThreadId  the testContext threadId
	 * @return the available test context
	 */
	public boolean isTestContextAvailable(long testContextThreadId) {
		synchronized (testContextLock) {
			ThreadContext threadContext = testContexts.get(testContextThreadId);

			if (threadContext == null) {
				return false;
			}

			TestNGTestContext testNgContext = threadContext.getCurrentTestNgContext();

			return testNgContext != null && !testNgContext.isFinished();
		}
	}

	public long getCurrentThreadId() {
		return Thread.currentThread().getId();
	}


	/**
	 * The timeshift scenario mode
	 */
	private enum TimeShiftScenarioMode {
		/**
		 * The suite mode means that the entire suite is treated as a single scenario
		 */
		SUITE,
		/**
		 * The test level parallelism
		 */
		TEST,
		/**
		 * The method mode means that each method in test suites is treated as independent scenario
		 */
		METHOD,
		/**
		 * The empty suite that is treated as container for other suites,
		 * or suite that is ignored with respect to time shift coordination.
		 */
		NONE;

		/**
		 * Get scenario mode for the suite
		 *
		 * @param xmlSuite the suite
		 * @return the scenario mode
		 */
		public static TimeShiftScenarioMode getMode(XmlSuite xmlSuite, boolean isAllowChildSuites) {
			String mode = System.getProperty(TIMESHIFT_SCENARIO_MODE_PARAMETER);

			if (StringUtils.isBlank(mode)) {
				if (xmlSuite.getTests().isEmpty()) {
					return TimeShiftScenarioMode.NONE;
				}

				if (isAllowChildSuites && !xmlSuite.getChildSuites().isEmpty()) { // TODO
					return TimeShiftScenarioMode.NONE;
				}

				mode = xmlSuite.getParameter(TIMESHIFT_SCENARIO_MODE_PARAMETER);
				if (mode == null) {
					return TimeShiftScenarioMode.NONE;
				}
			} else {
				System.out.println("HOOOORA Mode is set!!!!! " + mode);
			}

			try {
				return TimeShiftScenarioMode.valueOf(mode.trim().toUpperCase());
			} catch (Exception e) {
				log.error(String.format("Failed to parse mode for the suite '%s' using %s mode",
						xmlSuite.getName(), TimeShiftScenarioMode.METHOD), e); // TODO
				return TimeShiftScenarioMode.METHOD;
			}
		}

		/**
		 * Get scenario mode for the suite
		 *
		 * @param suite the suite
		 * @return the scenario mode
		 */
		public static TimeShiftScenarioMode getMode(ISuite suite) {
			return getMode(suite.getXmlSuite(), true);
		}

		/**
		 * Get scenario mode
		 *
		 * @param method the method
		 * @return the scenario mode
		 */
		public static TimeShiftScenarioMode getMode(ITestNGMethod method) {
			return getMode(method.getTestClass().getXmlTest().getSuite(), true);
		}
	}

	/**
	 * The information about the current class being run
	 */
	private static class ThreadContext {
		/**
		 * Thread Id
		 */
		private final long threadId;
		/**
		 * The testNg testContext list
		 */
		private final List<TestNGTestContext> testNgContexts = new ArrayList<TestNGTestContext>();
		/**
		 * Remaining amount for after methods
		 */
		private int currentTestNgContextIndex;

		public ThreadContext(long threadId, TestNGTestContext firstTestNgContext) {
			this.threadId = threadId;
			this.testNgContexts.add(firstTestNgContext);
		}

		public TestNGTestContext getCurrentTestNgContext() {
			return testNgContexts.get(currentTestNgContextIndex);
		}

		@SuppressWarnings("unused")
		public TestNGTestContext getTestNgContext(int index) {
			return testNgContexts.get(index);
		}

		public void addNewTestNgContext(TestNGTestContext newTestNgContext) {
			TestNGTestContext testNgContext = getCurrentTestNgContext();
			testNgContext.setFinished();
			testNgContexts.add(newTestNgContext);
			currentTestNgContextIndex++;
		}

		@Override
		public String toString() {
			return "ThreadContext(" + threadId + ")["
					+ "currentIndex=" + currentTestNgContextIndex
					+ ",testNgContexts(" + testNgContexts.size() + ")=" + testNgContexts
					+ "]";
		}
	}

	/**
	 * The information about the current class being run
	 */
	private static class TestNGTestContext {
		/**
		 * The test context
		 */
		private final TimedTestContext testContext;
		/**
		 * Remaining amount for after methods
		 */
		private int afterMethodCount;
		/**
		 * The test outcome
		 */
		private TestOutcome testOutcome;
		/**
		 * The throwable from the test if it is available
		 */
		private Throwable failure;
		/**
		 * The throwable from the test if it is available
		 */
		private boolean isFinished;

		/**
		 * The constructor from context
		 *
		 * @param testContext      the timed test context
		 * @param afterMethodCount the remaining after methods
		 */
		public TestNGTestContext(TimedTestContext testContext, int afterMethodCount) {
			this.testContext = testContext;
			this.afterMethodCount = afterMethodCount;
		}

		public void decrementAfterMethodCount() {
			afterMethodCount--;
		}

		public boolean isZeroAfterMethodCount() {
			return afterMethodCount == 0;
		}

		public boolean isPossibleAfterMethodCount() {
			return afterMethodCount >= 0;
		}

		public TimedTestContext getTestContext() {
			return testContext;
		}

		public TestOutcome getTestOutcome() {
			return testOutcome;
		}

		public void setTestOutcome(TestOutcome testOutcome) {
			this.testOutcome = testOutcome;
		}

		public Throwable getFailure() {
			return failure;
		}

		public void setFailure(Throwable failure) {
			this.failure = failure;
		}

		public boolean isFinished() {
			return isFinished;
		}

		public void setFinished() {
			if (!isFinished) {
				isFinished = true;
			}
		}

		@Override
		public String toString() {
			return "TestNGTestContext["
					+ "testContext=" + testContext
					+ ",afterMethodCount=" + afterMethodCount
					+ ",testOutcome=" + testOutcome
					+ ",failure=" + (failure != null ? failure.getMessage() : null)
					+ ",isFinished=" + isFinished
					+ "]";
		}
	}
}