package aaa.helpers.listeners;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.testng.IAlterSuiteListener;
import org.testng.collections.Maps;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import aaa.common.enums.Constants;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
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
		String usState = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
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
		Map<String, String> parameters = Maps.newHashMap(test.getLocalParameters());
		parameters.put("state", state);
		xmlTest.setParameters(parameters);
		return xmlTest;
	}

	private List<XmlClass> getClasses(XmlTest test) {
		List<XmlClass> resultClasses;
		resultClasses = test.getClasses();
		for (XmlPackage xmlPackage : test.getPackages()) {
			resultClasses.addAll(xmlPackage.getXmlClasses());
		}
		return resultClasses;
	}

	private XmlSuite alterSuite(XmlSuite suite) {
		String customSuiteName = PropertyProvider.getProperty("test.suitename");
		if (!customSuiteName.isEmpty()) {
			suite.setName(customSuiteName);
		}
		XmlTest newCATest;
		CustomLogger.getInstance().info(suite.toXml());
		List<XmlTest> newTests = new LinkedList<>();
		List<XmlTest> tests = suite.getTests();
		for (XmlTest test : tests) {
			newCATest = createTest(test, Constants.States.CA);
			List<String> states = getStates(test);

			for (String state : states) {

				XmlTest xmlTest = createTest(test, state);
				List<XmlClass> classes = getClasses(test);
				for (XmlClass xmlClass : classes) {
					if ((isSSProduct(xmlClass) || isPUPProduct(xmlClass)) && !state.equalsIgnoreCase(Constants.States.CA)) {
						xmlTest.getClasses().add(xmlClass);
					} else if ((isCAProduct(xmlClass) || isPUPProduct(xmlClass)) && state.equalsIgnoreCase(Constants.States.CA)) {
						xmlTest.getClasses().add(xmlClass);
					} else if (isCAProduct(xmlClass) && !states.contains(Constants.States.CA)) {
						newCATest.getClasses().add(xmlClass);
					} else {
						xmlTest.getClasses().add(xmlClass);
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
}
