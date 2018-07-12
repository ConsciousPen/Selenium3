package aaa.helpers.testng;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import aaa.utils.StateList;
import toolkit.exceptions.IstfException;

public class CsaaXmlClass {

	private XmlClass xmlClass;

	public CsaaXmlClass(XmlClass sourceClass, String state) {
		xmlClass = parseClass(sourceClass, state);
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

	private XmlClass parseClass(XmlClass xmlClass, String state) {
		XmlClass resultXmlClass = createClass(xmlClass);
		try {
			Class clazz = Class.forName(xmlClass.getName());
			List<XmlInclude> xmlInclude = xmlClass.getIncludedMethods();
			List<String> xmlExclude = xmlClass.getExcludedMethods();
			List<XmlInclude> resultInclude = new LinkedList<>();

			if (xmlInclude != null && !xmlInclude.isEmpty()) {

				for (XmlInclude include : xmlInclude) {
					if (isMethodMatch(clazz, include.getName(), state)) {
						resultInclude.add(include);
					}
				}
				resultXmlClass.setIncludedMethods(resultInclude);
			} else {
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Test.class) && !xmlExclude.contains(method.getName())) {
						if (isMethodMatch(clazz, method.getName(), state)) {
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

	private StateList getAnnotation(Class clazz, String methodName) {
		StateList statesAnn = null;
		Method method = null;
		for (Method classMethod : clazz.getDeclaredMethods()) {
			if (classMethod.getName().equals(methodName)) {
				method = classMethod;
			}
			break;
		}
		if (!method.isAnnotationPresent(Test.class)) {
			Assert.fail(String.format("Malformed Suite!!! Method %s doesn't have @Test annotation", method));
		}
		if (method.isAnnotationPresent(StateList.class)) {
			statesAnn = method.getAnnotation(StateList.class);
		} else if (clazz.isAnnotationPresent(StateList.class)) {
			statesAnn = (StateList) clazz.getDeclaringClass().getAnnotation(StateList.class);
		}
		return statesAnn;
	}

	private Boolean isMethodMatch(Class clazz, String methodName, String state) {
		List<XmlInclude> list = new LinkedList<>();
		StateList statesAnn = getAnnotation(clazz, methodName);
		if (statesAnn != null) {
			return Arrays.asList(statesAnn.states()).contains(state) && !Arrays.asList(statesAnn.statesExcept()).contains(state);
		} else {
			return true;
		}
	}

}
