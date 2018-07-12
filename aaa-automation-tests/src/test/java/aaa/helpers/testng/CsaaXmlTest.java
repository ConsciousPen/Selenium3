package aaa.helpers.testng;

import java.util.*;
import org.testng.collections.Maps;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlTest;

public class CsaaXmlTest {

	private static Map<String, XmlTest> testMap = new LinkedHashMap<>();
	private XmlTest xmlTest;
	private String key;

	public CsaaXmlTest(XmlTest xmlTest, String state, String policyType) {
		key = state.concat(" ").concat(policyType);
		if (!testMap.containsKey(key)) {
			this.xmlTest = createTest(xmlTest, state, policyType);
		} else {
			this.xmlTest = testMap.get(key);
		}
	}

	public static List<XmlTest> getAllTests() {
		return new ArrayList<>(testMap.values());
	}

	public static List<XmlClass> getClasses(XmlTest test) {
		List<XmlClass> resultClasses = new LinkedList<>();
		resultClasses = test.getClasses();
		for (XmlPackage xmlPackage : test.getPackages()) {
			resultClasses.addAll(xmlPackage.getXmlClasses());
		}
		return resultClasses;
	}

	public static void clearTests() {
		testMap.clear();
	}

	public XmlTest get() {
		return this.xmlTest;
	}

	private XmlTest createTest(XmlTest test, String state, String policyType) {
		XmlTest xmlTest = new XmlTest();
		/*String testName = test.getName();
		if (!testName.startsWith(state) || !testName.contains(state.toUpperCase())) {
			xmlTest.setName(policyType.concat(" ").concat(state).concat(" ").concat(test.getName()));
		} else {
			xmlTest.setName(test.getName());
		}*/
		xmlTest.setName(key.concat(" Regression Tests"));
		xmlTest.setVerbose(test.getVerbose());
		xmlTest.setPreserveOrder(test.getPreserveOrder());
		xmlTest.setIncludedGroups(test.getIncludedGroups());
		xmlTest.setExcludedGroups(test.getExcludedGroups());
		xmlTest.setParallel(test.getParallel());
		xmlTest.setThreadCount(test.getThreadCount());
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("state", state);
		xmlTest.setParameters(parameters);
		testMap.put(key, xmlTest);
		return xmlTest;
	}

}
