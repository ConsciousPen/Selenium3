package aaa.helpers.testng;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;
import aaa.utils.StateList;
import toolkit.exceptions.IstfException;

public class CsaaXmlClass {

	private XmlClass xmlClass;

	public CsaaXmlClass(XmlTest test, XmlClass sourceClass, String state) {
		xmlClass = parseClass(test, sourceClass, state);
		//xmlClass = sourceClass;
	}

	public String getPolicyType() {
		String className = xmlClass.getName();
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

	public XmlClass get() {
		return xmlClass;
	}

	private XmlClass createClass(XmlClass sourceClass) {
		XmlClass resultClass = new XmlClass();
		resultClass.setName(sourceClass.getName());
		resultClass.setIndex(sourceClass.getIndex());
		return resultClass;
	}

	private XmlClass parseClass(XmlTest test, XmlClass xmlClass, String state) {
		XmlClass resultXmlClass = createClass(xmlClass);
		try {
			Class clazz = Class.forName(xmlClass.getName());
			List<XmlInclude> xmlInclude = xmlClass.getIncludedMethods();
			List<String> xmlExclude = xmlClass.getExcludedMethods();
			List<XmlInclude> resultInclude = new LinkedList<>();

			if (xmlInclude != null && !xmlInclude.isEmpty()) {

				for (XmlInclude include : xmlInclude) {
					if (isMethodMatch(test, clazz, include.getName(), state)) {
						resultInclude.add(include);
					}
				}
				resultXmlClass.setIncludedMethods(resultInclude);
			} else {
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Test.class) && !xmlExclude.contains(method.getName())) {
						if (isMethodMatch(test, clazz, method.getName(), state)) {
							resultInclude.add(new XmlInclude(method.getName()));
						}
					}
				}
			}
			resultXmlClass.setIncludedMethods(resultInclude);
			for (XmlInclude include : resultInclude) {
				include.setXmlClass(resultXmlClass);
			}
			resultXmlClass.setExcludedMethods(new LinkedList<>());

		} catch (ClassNotFoundException e) {
			throw new IstfException("Malformed suite: ", e.getCause());
		}
		return resultXmlClass;
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
			return false;
		}
		List<String> groups = Arrays.asList(testAnn.groups());

		returnValue = groups.stream().anyMatch(s -> test.getIncludedGroups().contains(s)) && groups.stream().noneMatch(s -> test.getExcludedGroups().contains(s));

		if (returnValue) {
			StateList statesAnn = getAnnotation(clazz, methodName, StateList.class);
			if (statesAnn != null) {
				returnValue = Arrays.asList(statesAnn.states()).contains(state) && !Arrays.asList(statesAnn.statesExcept()).contains(state);
			} else {
				returnValue = true;
			}
		}

		return returnValue;

	}

}
