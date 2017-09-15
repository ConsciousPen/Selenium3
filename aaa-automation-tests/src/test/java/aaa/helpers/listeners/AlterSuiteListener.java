package aaa.helpers.listeners;

import aaa.common.enums.Constants;
import org.apache.commons.lang.StringUtils;
import org.testng.IAlterSuiteListener;
import org.testng.collections.Maps;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import toolkit.config.PropertyProvider;
import toolkit.utils.logging.CustomLogger;

import java.util.*;

public class AlterSuiteListener implements IAlterSuiteListener {
	@Override
	public void alter(List<XmlSuite> suites) {

		for (XmlSuite suite : suites) {
			XmlTest newCATest = new XmlTest();
			CustomLogger.getInstance().info(suite.toXml());
			List<XmlTest> newTests = new LinkedList<>();
			List<XmlTest> tests = suite.getTests();
			for (XmlTest test : tests) {
				newCATest = createTest(test, Constants.States.CA);
				List<String> states = getStates(test);

				for (String state : states) {

					XmlTest xmlTest = createTest(test, state);
					List<XmlClass> classes = test.getClasses();
					for (XmlClass xmlClass : classes) {
						if ((isSSProduct(xmlClass) || isPUPProduct(xmlClass)) && !state.equalsIgnoreCase(Constants.States.CA)) {
							xmlTest.getClasses().add(xmlClass);
						} else if ((isCAProduct(xmlClass) || isPUPProduct(xmlClass)) && state.equalsIgnoreCase(Constants.States.CA)) {
							xmlTest.getClasses().add(xmlClass);
						} else if (isCAProduct(xmlClass) && !states.contains(Constants.States.CA)) {
							newCATest.getClasses().add(xmlClass);
						}
					}
					if(!xmlTest.getClasses().isEmpty())
					newTests.add(xmlTest);
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
		return Arrays.asList(input.toUpperCase().trim().split(","));
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
		xmlTest.setName(state + " " + test.getName());
		xmlTest.setVerbose(test.getVerbose());
		xmlTest.setPreserveOrder(test.getPreserveOrder());
		xmlTest.setIncludedGroups(test.getIncludedGroups());
		xmlTest.setExcludedGroups(test.getExcludedGroups());
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("state", state);
		xmlTest.setParameters(parameters);
		return xmlTest;
	}
}