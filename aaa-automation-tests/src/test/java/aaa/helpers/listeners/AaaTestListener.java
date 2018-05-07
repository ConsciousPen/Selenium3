package aaa.helpers.listeners;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestResult;
import org.testng.SkipException;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.utils.StatesList;
import toolkit.config.PropertyProvider;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;
import toolkit.utils.teststoragex.models.Attachment;

public class AaaTestListener extends TestngTestListener2 {

	@Override
	public void onTestFailure(ITestResult result) {
		log.info("Test failure date/time: {}", TimeSetterUtil.getInstance().getCurrentTime());
		super.onTestFailure(result);
	}

	@Override
	protected void createAuxAttachments(ITestResult result) {
		if (result.getTestContext().getAttribute("attachment") != null) {
			createAttachment(result, result.getTestContext().getAttribute("attachment").toString(), Attachment.Type.OTHER);
		}
		String appLogPath = new AppLogGrabber().grabAppLog(result);
		if (appLogPath != null) {
			createAttachment(result, appLogPath, Attachment.Type.APP_LOG);
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		result.setParameters(getState(result));
		super.onTestStart(result);

		Method method = result.getMethod().getConstructorOrMethod().getMethod();

		StatesList statesAnn = null;
		if (method.isAnnotationPresent(StatesList.class)) {
			statesAnn = method.getAnnotation(StatesList.class);
		} else if (method.getDeclaringClass().isAnnotationPresent(StatesList.class)) {
			statesAnn = method.getDeclaringClass().getAnnotation(StatesList.class);
		}
		if (statesAnn != null) {
			List<String> applStates = Arrays.asList(statesAnn.states());
			List<String> exclStates = Arrays.asList(statesAnn.statesExcept());
			String state = result.getParameters()[0].toString();
			if (applStates.size() > 0 && !applStates.contains(state) || exclStates.size() > 0 && exclStates.contains(state)) {
				result.setStatus(3);
				result.setThrowable(new SkipException(String.format("State '%s' is not applicable to this test", state)));
			}
		}
	}

	private Object[] getState(ITestResult result) {
		String stateParam = "state";
		Object[] params = result.getParameters();
		if (params != null && StringUtils.isNotBlank(params[0].toString())) {
			return new Object[] {params[0]};
		} else if (isCAProduct(result)) {
			return new Object[] {Constants.States.CA};
		} else if (StringUtils.isNotBlank(PropertyProvider.getProperty("test.usstate"))) {
			String state = PropertyProvider.getProperty("test.usstate");
			return new Object[] {state};
		} else {
			return new Object[] {Constants.States.UT};
		}
	}

	private Boolean isCAProduct(ITestResult result) {
		return result.getMethod() != null && result.getMethod().getTestClass().getName().contains("_ca.");
	}
}
