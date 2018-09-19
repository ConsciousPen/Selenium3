package aaa.helpers.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.testng.*;
import com.exigen.ipb.etcsa.utils.RetrySuiteGenerator;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
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

	/*private String getState(ITestResult result) {
		String state = "";
		Method method = result.getMethod().getConstructorOrMethod().getMethod();
		if (method.isAnnotationPresent(Parameters.class) && Arrays.asList(method.getAnnotation(Parameters.class).value()).stream().anyMatch(p -> "state".equals(p))) {
			Parameter stateParam = Arrays.asList(method.getParameters()).stream().filter(p -> p.isAnnotationPresent(Optional.class)).findFirst().orElse(null);
			if (stateParam != null) {
				if (isCAProduct(result)) {
					state = Constants.States.CA;
				} else if (StringUtils.isNotBlank(PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE))) {
					state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
				} else if (!stateParam.getAnnotation(Optional.class).value().isEmpty()) {
					state = stateParam.getAnnotation(Optional.class).value();
				}
			}
		}
		return state;
	}*/
	/*	&& stateParam.isAnnotationPresent(Optional.class) && !stateParam.getAnnotation(Optional.class).value().isEmpty())
			setState(stateParam.getAnnotation(Optional.class).value());
		} else if (isStateCA()) {
			setState(Constants.States.CA);
		} else if (StringUtils.isNotBlank(usState)) {
			setState(usState);
		} else {
			setState(Constants.States.UT);
		}
		if (params != null && params.length != 0 && "".equals(Arrays.asList(params[0]).get(0))) {
			if (isCAProduct(result)) {
				params = createParams(params, Constants.States.CA);
			} else if (StringUtils.isNotBlank(PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE))) {
				String state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
				params = createParams(params, state);
			} else {
				params = createParams(params, Constants.States.UT);
			}
		}
		return params;
	}*/

/*	private Boolean isCAProduct(ITestResult result) {
		return result.getMethod() != null && result.getMethod().getTestClass().getName().contains("_ca.");
	}*/

/*	private Object[] createParams(Object[] inputParams, String state) {
		List<Object> list = Arrays.asList(inputParams);
		list.set(0, state);
		return list.toArray();
	}*/
}
