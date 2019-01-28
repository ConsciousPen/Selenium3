package aaa.helpers.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.testng.*;
import com.exigen.ipb.eisa.utils.RetrySuiteGenerator;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.logs.AppLogGrabber;
import aaa.helpers.logs.RatingEngineLogsGrabber;
import aaa.utils.StateList;
import toolkit.metrics.ReportingContext;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;
import toolkit.utils.teststoragex.models.Attachment;
import toolkit.utils.teststoragex.models.Run;
import toolkit.utils.teststoragex.utils.TestNGUtils;

public class AaaTestListener extends TestngTestListener2 implements IExecutionListener {
	private static RetrySuiteGenerator suiteGenerator = new RetrySuiteGenerator();

	@Override
	public void onStart(ISuite suite) {
		super.onStart(suite);
		Run run = new Run(ReportingContext.get().getRunId());
		File file = new File("run_id.properties");
		Properties props = new Properties();
		props.setProperty("runID", run.getId().toString());
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			props.store(fileOut, "Test Analytics Run ID");
			fileOut.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		try {
			log.info("Test failure date/time: {}", TimeSetterUtil.getInstance().getCurrentTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onTestFailure(result);
	}

	@Override
	protected void createAuxAttachments(ITestResult result) {
		if (result.getTestContext().getAttribute("attachment") != null) {
			createAttachment(result, result.getTestContext().getAttribute("attachment").toString(), Attachment.Type.OTHER);
		}

		createRatingEngineLogAttachment(result, RatingEngineLogsGrabber.RATING_REQUEST_TEST_CONTEXT_ATTR_NAME);
		createRatingEngineLogAttachment(result, RatingEngineLogsGrabber.RATING_RESPONSE_TEST_CONTEXT_ATTR_NAME);

		String appLogPath = new AppLogGrabber().grabAppLog(result);
		if (appLogPath != null) {
			createAttachment(result, appLogPath, Attachment.Type.APP_LOG);
		}
	}

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult result) {
		if (method.isTestMethod()) {
			String state = result.getTestContext().getCurrentXmlTest().getParameter(Constants.STATE_PARAM);
			Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();

			StateList statesAnn = null;
			if (testMethod.isAnnotationPresent(StateList.class)) {
				statesAnn = testMethod.getAnnotation(StateList.class);
			} else if (testMethod.getDeclaringClass().isAnnotationPresent(StateList.class)) {
				statesAnn = testMethod.getDeclaringClass().getAnnotation(StateList.class);
			}
			if (statesAnn != null) {
				List<String> applStates = Arrays.asList(statesAnn.states());
				List<String> exclStates = Arrays.asList(statesAnn.statesExcept());
				if (!applStates.isEmpty() && !applStates.contains(state) || !exclStates.isEmpty() && exclStates.contains(state)) {
					throw new SkipException(String.format("State '%s' is not applicable to this test", state));
				}
			}
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		super.afterInvocation(method, testResult);
		if (method.isTestMethod()) {
			suiteGenerator.collectFailedTests(testResult, isFailureCovered());
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		super.onFinish(suite);
		suiteGenerator.setRootSuiteData(TestNGUtils.getRootSuite(suite));
	}

	@Override
	public void onExecutionStart() {
	}

	@Override
	public void onExecutionFinish() {
		suiteGenerator.generateSuite();
	}

	private void createRatingEngineLogAttachment(ITestResult result, String ratingTestContextAttrName) {
		ITestContext context = result.getTestContext();
		if (context.getAttribute(ratingTestContextAttrName) != null) {
			createAttachment(result, context.getAttribute(ratingTestContextAttrName).toString(), Attachment.Type.OTHER);
			context.removeAttribute(ratingTestContextAttrName); // needed to prevent wrong log attachment if rating log gathering will fail for next test
		}
	}
}
