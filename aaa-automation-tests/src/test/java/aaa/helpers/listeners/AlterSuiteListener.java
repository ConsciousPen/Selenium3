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
			CustomLogger.getInstance().info(suite.toXml());
			List<XmlTest> newTests = new LinkedList<>();
			List<XmlTest> tests = suite.getTests();
			for (XmlTest test : tests) {
				List<String> states = getStates(test);

				for (String state : states) {
					XmlTest xmlTest = new XmlTest();
					//xmlTest = test;
					xmlTest.setName(state + " " + test.getName());
					xmlTest.setVerbose(test.getVerbose());
					xmlTest.setPreserveOrder(test.getPreserveOrder());
					//xmlTest.setThreadCount(test.getThreadCount());
					//xmlTest.setParallel(test.getParallel());
					xmlTest.setIncludedGroups(test.getIncludedGroups());
					xmlTest.setExcludedGroups(test.getExcludedGroups());
					//xmlTest.setPackages(test.getPackages());
					Map<String, String> parameters = Maps.newHashMap();
					parameters.put("state", state);
					xmlTest.setParameters(parameters);
					List<XmlClass> classes = test.getClasses();
					for (XmlClass xmlClass : classes) {
						xmlTest.getClasses().add(xmlClass);
					}
					newTests.add(xmlTest);
				}
			}
			suite.setTests(newTests);
			for(XmlTest newTest : newTests){
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

	private static List<String> parseStates(String input) {
		return Arrays.asList(input.split(","));
	}
}