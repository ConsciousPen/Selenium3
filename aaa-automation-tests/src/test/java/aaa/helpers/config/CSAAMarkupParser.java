package aaa.helpers.config;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.DefaultMarkupParser;

public class CSAAMarkupParser extends DefaultMarkupParser {

	public CSAAMarkupParser() {
		registerProcessor("startTime", args -> processDateTime(TimeSetterUtil.getInstance().getStartTime(), args));
	}
}
