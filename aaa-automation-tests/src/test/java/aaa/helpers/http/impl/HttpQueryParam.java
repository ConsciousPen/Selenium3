package aaa.helpers.http.impl;

import org.apache.commons.lang.StringUtils;

import aaa.helpers.http.impl.HttpQueryParam;

public class HttpQueryParam {

	private static final String MACROS_LEFT_DELIMITER = "{{";
	private static final String MACROS_RIGHT_DELIMITER = "}}";
	private static final String KEY_VALUE_DELIEMITER = "=";
	private static final String ELEMENT_TYPE_DELIMITER = "\\|";

	private String name;
	private String value;
	private boolean isMacros;
	private Element element;

	public HttpQueryParam(String param) {
		parseParam(param);
	}

	private HttpQueryParam parseParam(String param) {
		if (!StringUtils.isEmpty(param.trim())) {
			String[] paramParts = param.split(KEY_VALUE_DELIEMITER);
			name = paramParts[0];
			if (paramParts.length == 1) {
				value = "";
				isMacros = false;
			} else if (paramParts[1].trim().startsWith(MACROS_LEFT_DELIMITER)) {
				String macros = paramParts[1].trim()
						.replace(MACROS_LEFT_DELIMITER, "")
						.replace(MACROS_RIGHT_DELIMITER, "");
				String[] macrosParts = macros.split(ELEMENT_TYPE_DELIMITER);
				if (macrosParts.length == 2) {
					value = macrosParts[0];
					element = Element.valueOf(macrosParts[1].toUpperCase());
				} else {
					value = macros;
					element = Element.DEFAULT;
				}
				isMacros = true;
			}else {
				value = paramParts[1];
			}
		}
		return this;
	}

	public String mergeNameAndValue() {
		return name + KEY_VALUE_DELIEMITER + value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void replaceValue(String newValue) {
		value = newValue;
	}

	public boolean isMacros() {
		return isMacros;
	}

	public Element getElement() {
		return element;
	}

	public enum Element {
		DEFAULT(""),
		COMBOBOX("Combobox"),
		RADIOBOOL("RadioBool"),
		RADIO("Radio"),
		FIELD("Field");

		String id;
		private Element(String id) {
			this.id = id;
		}

		public String get() {
			return this.id;
		}
	}

	@Override
	public String toString() {
		return name + " / " + value + " / " + element + " / " + isMacros;
	}
}
