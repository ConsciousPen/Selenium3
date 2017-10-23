package aaa.helpers;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.DefaultMarkupParser;

public class AaaMarkupParser extends DefaultMarkupParser {
	public final static String CONTAINS_PREFIX = "contains=";


	static {
		expressionRegexp = String.format("(?:%s)?/(\\w+)\\s*(.*)", CONTAINS_PREFIX);
	}

	public AaaMarkupParser() {
		registerProcessor("startTime", args -> processDateTime(TimeSetterUtil.getInstance().getStartTime(), args));
	}

	@Override
	public String parse(String value) {
		boolean hasContainsPrefix = value != null && value.startsWith(CONTAINS_PREFIX);

		String parsedValue = super.parse(value);
		if (hasContainsPrefix && parsedValue != null && !parsedValue.startsWith(CONTAINS_PREFIX)) {
			parsedValue = CONTAINS_PREFIX + parsedValue;
		}

		return parsedValue;
	}
}
