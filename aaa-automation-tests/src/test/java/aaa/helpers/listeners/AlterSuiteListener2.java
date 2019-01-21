package aaa.helpers.listeners;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import aaa.common.enums.Constants;
import aaa.helpers.testng.CsaaXmlClass;
import aaa.helpers.testng.CsaaXmlTest;
import toolkit.config.PropertyProvider;
import toolkit.utils.logging.CustomLogger;

public class AlterSuiteListener2 implements IAlterSuiteListener {
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

	private XmlSuite alterSuite(XmlSuite suite) {
		String customSuiteName = PropertyProvider.getProperty("test.suitename");
		if (!customSuiteName.isEmpty()) {
			suite.setName(customSuiteName);
		}
		CustomLogger.getInstance().info(suite.toXml());
		List<XmlTest> newTests = new LinkedList<>();

		List<XmlTest> tests = suite.getTests();
		for (XmlTest test : tests) {
			List<XmlClass> classes = CsaaXmlTest.getClasses(test);
			List<String> states = getStates(test);
			for (String state : states) {
				List<XmlClass> newClasses = new LinkedList<>();
				for (XmlClass xmlClass : classes) {
					CsaaXmlClass xmlClassNew = new CsaaXmlClass(test, xmlClass, state);
					if (xmlClassNew.get() != null && xmlClassNew.get().getIncludedMethods() != null && !xmlClassNew.get().getIncludedMethods().isEmpty()) {

						CsaaXmlTest xmlTestNew = new CsaaXmlTest(test, state, xmlClassNew.getPolicyType());

						if (xmlTestNew.get().getXmlClasses().stream().noneMatch(s -> s.getName().equals(xmlClassNew.get().getName()))) {
							xmlTestNew.get().getXmlClasses().add(xmlClassNew.get());
							xmlClassNew.get().setXmlTest(xmlTestNew.get());
						} else {
							xmlTestNew.get().getXmlClasses().stream().filter(c -> c.getName().equals(xmlClassNew.get().getName())).forEach(cl -> cl.getIncludedMethods().addAll(xmlClassNew.get().getIncludedMethods()));
						}

					}

					if (xmlClassNew.getNoParams() != null && xmlClassNew.getNoParams().getIncludedMethods() != null && !xmlClassNew.getNoParams().getIncludedMethods().isEmpty()) {

						CsaaXmlTest xmlTestNew = new CsaaXmlTest(test, null, xmlClassNew.getPolicyType());
						if (xmlTestNew.get().getXmlClasses().stream().noneMatch(s -> s.getName().equals(xmlClassNew.getNoParams().getName()))) {
							xmlTestNew.get().getXmlClasses().add(xmlClassNew.getNoParams());
							xmlClassNew.getNoParams().setXmlTest(xmlTestNew.get());
						} else {
							xmlTestNew.get().getXmlClasses().stream().filter(c -> c.getName().equals(xmlClassNew.getNoParams().getName())).forEach(cl -> cl.getIncludedMethods().addAll(xmlClassNew.getNoParams().getIncludedMethods()));
						}
					}
				}
			}
		}
		suite.setTests(CsaaXmlTest.getAllTests());
		for (XmlTest newTest : CsaaXmlTest.getAllTests()) {
			newTest.setXmlSuite(suite);
		}
		CsaaXmlTest.clearTests();
		CustomLogger.getInstance().info(suite.toXml());
		return suite;
	}

}