package aaa.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import aaa.common.enums.Constants;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

public class DataProviderClass {
	private static String usState = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);

	@DataProvider(name = "state", parallel = true)
	public Object[][] getState(ITestContext tc, Method method) {
		List<String> states = new ArrayList<>();
		String temp = tc.getCurrentXmlTest().getParameter("state");
		if (StringUtils.isNotBlank(temp)) {
			states = parseStates(temp);
		} else if (StringUtils.isNotBlank(usState)) {
			states = parseStates(usState);
		} else {
			states.add(Constants.States.UT);
		}
		String className = method.getDeclaringClass().getName();

		Object[][] object;
		if (className.contains(".home_ca.") || className.contains(".auto_ca.")) {
			states = new ArrayList<>();
			states.add(Constants.States.CA);
		} else if (states.contains(Constants.States.CA) && (className.contains(".auto_ss.") || className.contains(".home_ss."))) {
			List<String> newList = new ArrayList<>();
			for (String str : states) {
				if (!str.equals(Constants.States.CA)) {
					newList.add(str);
				}
			}
			states = newList;
		}
		object = new Object[states.size()][1];
		for (int i = 0; i < states.size(); i++) {
			object[i][0] = states.get(i);
		}
		return object;
	}

	@DataProvider(name = "CA", parallel = true)
	public Object[][] getCAState() {
		return new Object[][]{
				{Constants.States.CA},
		};
	}

	@DataProvider(name = "CCL", parallel = true)
	public Object[][] getCCLState(ITestContext tc) {
		List<String> states = getStates(tc);

		Object[][] object = new Object[states.size()][1];
		for (int i = 0; i < states.size(); i++) {
			object[i][0] = states.get(i);
		}
		return object;
	}

	@DataProvider(name = "CL", parallel = true)
	public Object[][] getCLState(ITestContext tc) {
		List<String> states = getStates(tc);
		List<String> newList = new ArrayList<>();
		if (states.contains(Constants.States.CA)) {
			for (String str : states) {
				if (!str.equals(Constants.States.CA)) {
					newList.add(str);
				}
			}
		}
		Object[][] object = new Object[newList.size()][1];
		for (int i = 0; i < newList.size(); i++) {
			object[i][0] = newList.get(i);
		}
		return object;
	}


	private static List<String> parseStates(String input) {
		return Arrays.asList(input.split(","));
	}

	private List<String> getStates(ITestContext tc) {
		List<String> states = new ArrayList<>();
		String temp = tc.getCurrentXmlTest().getParameter("state");
		if (StringUtils.isNotBlank(temp)) {
			states = parseStates(temp);
		} else if (StringUtils.isNotBlank(usState)) {
			states = parseStates(usState);
		} else {
			states.add(Constants.States.UT);
		}

		return states;
	}

}
