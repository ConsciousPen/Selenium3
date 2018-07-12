package aaa.helpers.listeners;

import java.lang.reflect.Method;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.IAlterSuiteListener;
import org.testng.annotations.Test;
import org.testng.collections.Maps;
import org.testng.xml.*;
import aaa.common.enums.Constants;
import aaa.utils.StateList;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.logging.CustomLogger;

public class AlterSuiteListener implements IAlterSuiteListener {
	@Override
	public void alter(List<XmlSuite> suites) {

		for (XmlSuite suite : suites) {
			List<XmlSuite> childSuites = suite.getChildSuites();
			if (childSuites.isEmpty()) {
				alterSuite(suite);
			} else {
				alter(childSuites);
			}
		}
	}

	private List<String> getStates(XmlTest test) {
		String usState = PropertyProvider.getProperty("test.usstate");
		String temp = test.getParameter("state");
		List<String> states = new ArrayList<>();
		if (StringUtils.isNotBlank(temp)) {
			states = parseStates(temp);
		} else if (StringUtils.isNotBlank(usState)) {
			states = parseStates(usState);
		} else {
			states.add(Constants.States.UT);
		}
		return states;
	}

	private List<String> parseStates(String input) {
		LinkedList<String> statesList = new LinkedList<>();
		for (String state : input.split(",")) {
			statesList.add(state.trim().toUpperCase());
		}
		return statesList;
	}

	private Boolean isCAProduct(XmlClass xmlClass) {
		return xmlClass != null && xmlClass.getName().contains("_ca.");
	}

	private Boolean isSSProduct(XmlClass xmlClass) {
		return xmlClass != null && xmlClass.getName().contains("_ss.");
	}

	private Boolean isPUPProduct(XmlClass xmlClass) {
		return xmlClass != null && xmlClass.getName().contains(".pup.");
	}

	private XmlSuite alterSuite(XmlSuite suite) {
		String customSuiteName = PropertyProvider.getProperty("test.suitename");
		if (!customSuiteName.isEmpty()) {
			suite.setName(customSuiteName);
		}
		XmlTest newCATest = new XmlTest();
		CustomLogger.getInstance().info(suite.toXml());
		List<XmlTest> newTests = new LinkedList<>();
		List<XmlTest> tests = suite.getTests();
		for (XmlTest test : tests) {
			List<XmlClass> classes = getClasses(test);
			newCATest = createTest(test, Constants.States.CA);
			List<String> states = getStates(test);
			for (String state : states) {
				XmlTest xmlTest = createTest(test, state);
				for (XmlClass xmlClass : classes) {
					XmlClass xmlClassNew = parseClass(xmlClass, state);
					if (xmlClassNew == null || xmlClassNew.getIncludedMethods() == null || xmlClassNew.getIncludedMethods().isEmpty()) {
						break;
					}
					if ((isSSProduct(xmlClassNew) || isPUPProduct(xmlClassNew)) && !state.equalsIgnoreCase(Constants.States.CA)) {
						xmlTest.getClasses().add(xmlClassNew);
						xmlClassNew.setXmlTest(xmlTest);
					} else if ((isCAProduct(xmlClassNew) || isPUPProduct(xmlClassNew)) && state.equalsIgnoreCase(Constants.States.CA)) {
						xmlTest.getClasses().add(xmlClassNew);
						xmlClassNew.setXmlTest(xmlTest);
					} else if (isCAProduct(xmlClassNew) && !states.contains(Constants.States.CA)) {
						newCATest.getClasses().add(xmlClassNew);
						xmlClassNew.setXmlTest(newCATest);
					} else {
						xmlTest.getClasses().add(xmlClassNew);
						xmlClassNew.setXmlTest(xmlTest);
					}
				}
				if (!xmlTest.getClasses().isEmpty()) {
					newTests.add(xmlTest);
				}
			}
			if (!newCATest.getClasses().isEmpty()) {
				newTests.add(newCATest);
			}
		}
		suite.setTests(newTests);
		for (XmlTest newTest : newTests) {
			newTest.setSuite(suite);
		}
		CustomLogger.getInstance().info(suite.toXml());
		return suite;
	}

	private XmlClass parseClass(XmlClass xmlClass, String state) {
		XmlClass resultXmlClass = createClass(xmlClass);
		try {
			Class clazz = Class.forName(resultXmlClass.getName());
			List<XmlInclude> xmlInclude = xmlClass.getIncludedMethods();
			List<String> xmlExclude = xmlClass.getExcludedMethods();

			if (xmlInclude != null && !xmlInclude.isEmpty()) {
				List<XmlInclude> resultInclude = new LinkedList<>();
				for (XmlInclude include : xmlInclude) {
					checkTestAnnotation(clazz, include.getName());
					if (isMethodMatch(clazz, include.getName(), state)) {
						resultInclude.add(include);
					}
				}
				resultXmlClass.setIncludedMethods(resultInclude);
			} else {
				List<XmlInclude> resultInclude = new LinkedList<>();
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Test.class) && !xmlExclude.contains(method.getName())) {
						if (isMethodMatch(clazz, method.getName(), state)) {
							resultInclude.add(new XmlInclude(method.getName()));
						}
					}
					resultXmlClass.setIncludedMethods(resultInclude);
				}
			}

		} catch (ClassNotFoundException e) {
			throw new IstfException("Malformed suite: ", e.getCause());
		}
		return resultXmlClass;
	}

	private StateList getAnnotation(Class clazz, String methodName) {
		StateList statesAnn = null;
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, String.class);
		} catch (NoSuchMethodException e) {
			throw new IstfException(String.format("Malformed suite. Check suite xml %s:%s ", clazz.getName(), methodName), e.getCause());
		}
		if (method.isAnnotationPresent(StateList.class)) {
			statesAnn = method.getAnnotation(StateList.class);
		} else if (clazz.isAnnotationPresent(StateList.class)) {
			statesAnn = (StateList) clazz.getDeclaringClass().getAnnotation(StateList.class);
		}
		return statesAnn;
	}

	private XmlTest createTest(XmlTest test, String state) {
		XmlTest xmlTest = new XmlTest();
		String testName = test.getName();
		if (!testName.startsWith(state) || !testName.contains(state.toUpperCase())) {
			xmlTest.setName(state + " " + test.getName());
		} else {
			xmlTest.setName(test.getName());
		}
		xmlTest.setVerbose(test.getVerbose());
		xmlTest.setPreserveOrder(test.getPreserveOrder());
		xmlTest.setIncludedGroups(test.getIncludedGroups());
		xmlTest.setExcludedGroups(test.getExcludedGroups());
		xmlTest.setParallel(test.getParallel());
		xmlTest.setThreadCount(test.getThreadCount());
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("state", state);
		xmlTest.setParameters(parameters);
		return xmlTest;
	}

	private List<XmlClass> getClasses(XmlTest test) {
		List<XmlClass> resultClasses = new LinkedList<>();
		resultClasses = test.getClasses();
		for (XmlPackage xmlPackage : test.getPackages()) {
			resultClasses.addAll(xmlPackage.getXmlClasses());
		}
		return resultClasses;
	}

	private XmlClass createClass(XmlClass sourceClass) {
		XmlClass resultClass = new XmlClass();
		resultClass.setName(sourceClass.getName());
		resultClass.setClass(sourceClass.getClass());
		resultClass.setIndex(sourceClass.getIndex());
		//	resultClass.setIncludedMethods(sourceClass.getIncludedMethods());
		//	resultClass.setExcludedMethods(sourceClass.getExcludedMethods());
		return resultClass;
	}

	private void checkTestAnnotation(Class clazz, String method) {
		try {
			if (!clazz.getDeclaredMethod(method, String.class).isAnnotationPresent(Test.class)) {
				Assert.fail(String.format("Malformed Suite!!! Method %s:%s is doesn't have @Test annotation", clazz.getName(), method));
			}
		} catch (NoSuchMethodException e) {
			Assert.fail(String.format("Malformed Suite!!! No such method in class -> %s:%s", clazz.getName(), method));
		}
	}

	private Boolean isMethodMatch(Class clazz, String methodName, String state) {
		List<XmlInclude> list = new LinkedList<>();
		StateList statesAnn = getAnnotation(clazz, methodName);
		if (statesAnn != null) {
			return Arrays.asList(statesAnn.states()).contains(state) && !Arrays.asList(statesAnn.statesExcept()).contains(state);
		}
		return false;
	}
}