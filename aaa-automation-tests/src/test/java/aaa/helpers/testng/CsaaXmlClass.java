package aaa.helpers.testng;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;
import aaa.utils.StateList;
import toolkit.exceptions.IstfException;
import toolkit.utils.logging.CustomLogger;

public class CsaaXmlClass {

	private XmlClass xmlClass;
	private XmlClass xmlClassNoParams;

	public CsaaXmlClass(XmlTest test, XmlClass sourceClass, String state) {
		parseClass(test, sourceClass, state);
	}

	public String getPolicyType() {
		String className = this.xmlClass.getName();
		if (className.contains(".auto_ca.") || className.contains(".auto_ss.")) {
			return "Auto";
		} else if (className.contains(".home_ca.") || className.contains(".home_ss.")) {
			return "Home";
		} else if (className.contains(".pup.")) {
			return "PUP";
		} else {
			return "Common";
		}
	}

	public XmlClass getNoParams() {
		return this.xmlClassNoParams;
	}

	public XmlClass get() {
		return this.xmlClass;
	}

	private XmlClass createClass(XmlClass sourceClass) {
		XmlClass resultClass = new XmlClass();
		resultClass.setName(sourceClass.getName());
		resultClass.setIndex(sourceClass.getIndex());
		return resultClass;
	}

	private void parseClass(XmlTest test, XmlClass xmlClass, String state) {
		this.xmlClass = createClass(xmlClass);
		this.xmlClassNoParams = createClass(xmlClass);

		try {
			Class clazz = Class.forName(xmlClass.getName());
			List<XmlInclude> xmlInclude = xmlClass.getIncludedMethods();
			List<String> xmlExclude = xmlClass.getExcludedMethods();
			List<XmlInclude> resultInclude = new LinkedList<>();
			List<XmlInclude> resultIncludeNoParams = new LinkedList<>();

			if (xmlInclude != null && !xmlInclude.isEmpty()) {

				for (XmlInclude include : xmlInclude) {
					if (isMethodMatch(test, clazz, include.getName(), state)) {
						if (containsParams(clazz, include.getName())) {
							resultInclude.add(include);
						} else {
							resultIncludeNoParams.add(include);
						}

					}
				}
				this.xmlClass.setIncludedMethods(resultInclude);
				this.xmlClassNoParams.setIncludedMethods(resultIncludeNoParams);
			} else {
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Test.class) && !xmlExclude.contains(method.getName())) {
						if (isMethodMatch(test, clazz, method.getName(), state)) {
							if (containsParams(clazz, method.getName())) {
								resultInclude.add(new XmlInclude(method.getName()));
							} else {
								resultIncludeNoParams.add(new XmlInclude(method.getName()));
							}
						}
					}
				}
			}
			this.xmlClass.setIncludedMethods(resultInclude);
			this.xmlClassNoParams.setIncludedMethods(resultIncludeNoParams);

			for (XmlInclude include : resultInclude) {
				include.setXmlClass(this.xmlClass);
			}
			for (XmlInclude include : resultIncludeNoParams) {
				include.setXmlClass(this.xmlClassNoParams);
			}

		} catch (ClassNotFoundException e) {
			throw new IstfException("Malformed suite: ", e.getCause());
		}
	}

	private <T extends Annotation> T getAnnotation(Class clazz, String methodName, Class<T> annotationClass) {
		T annotation = null;
		Method method = null;
		for (Method classMethod : clazz.getDeclaredMethods()) {
			if (classMethod.getName().equals(methodName)) {
				method = classMethod;
				break;
			}
		}
		if (method != null) {
			if (!method.isAnnotationPresent(Test.class)) {
				Assert.fail(String.format("Malformed Suite!!! Method %s doesn't have @Test annotation", method));
			}
			if (method.isAnnotationPresent(annotationClass)) {
				annotation = method.getAnnotation(annotationClass);
			} else if (clazz.isAnnotationPresent(annotationClass)) {
				annotation = (T) clazz.getAnnotation(annotationClass);
			}
		}
		return annotation;
	}

	private Boolean isMethodMatch(XmlTest test, Class clazz, String methodName, String state) {
		Boolean returnValue = true;

		Test testAnn = getAnnotation(clazz, methodName, Test.class);
		if (testAnn == null) {
			logNotMatchingTests(clazz, methodName, Reasons.NO_TEST_ANNOTATION);
			return false;
		}
		List<String> groups = Arrays.asList(testAnn.groups());

		returnValue = (test.getIncludedGroups().isEmpty() || groups.stream().anyMatch(s -> test.getIncludedGroups().contains(s))) && (test.getExcludedGroups().isEmpty() || groups.stream().noneMatch(s -> test.getExcludedGroups().contains(s)));

		if (returnValue) {
			StateList statesAnn = getAnnotation(clazz, methodName, StateList.class);
			if (statesAnn != null) {
				returnValue = Arrays.asList(statesAnn.states()).contains(state) || !Arrays.asList(statesAnn.statesExcept()).contains(state);
				if (!returnValue) {
					logNotMatchingTests(clazz, methodName, Reasons.NOT_IN_STATE_LIST);
				}
			}
		} else {
			logNotMatchingTests(clazz, methodName, Reasons.NOT_INCLUDED_IN_GROUPS);
		}
		return returnValue;
	}

	private Boolean containsParams(Class clazz, String methodName) {
		return getAnnotation(clazz, methodName, Parameters.class) != null && getAnnotation(clazz, methodName, Parameters.class).value().length != 0;
	}

	private void logNotMatchingTests(Class clazz, String methodName, Reasons message) {
		CustomLogger.getInstance().info("Test {} doesn't match criteria. Reason: {} ", clazz.getName() + "." + methodName, message.get());
	}

	private enum Reasons {
		NO_TEST_ANNOTATION("Test Method doesn't have @Test Annotation or doesn't exist"),
		NOT_INCLUDED_IN_GROUPS("Test Method is not matching Groups selection"),
		NOT_IN_STATE_LIST("Test Method State is not within StateList");

		private String message;

		Reasons(String message) {
			this.message = message;
		}

		public String get() {
			return message;
		}
	}
}
