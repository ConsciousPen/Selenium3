package aaa.helpers.conversion;

import aaa.helpers.jobs.Job;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.mifmif.common.regex.Generex;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ConversionPolicyData {

	protected ConversionType conversionType;

	protected File file;
	protected HashMap<String, String> values;
	public ConversionPolicyData(ConversionType type, String fileName, LocalDateTime effectiveDate) {
		this.conversionType = type;
		this.file = new File(type.getLocalTemplatesFolder(), fileName);
		values = new HashMap<>();

		values.put("//effective", effectiveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00");
		values.put("//expiration", effectiveDate.plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00");

		String randomNum = new Generex("\\d{9}").random();
		if (type.equals(ConversionType.MAIG)) {
			values.put("//sourcePolicyNum", randomNum);
		} else if (type.equals(ConversionType.FOXPRO)) {
			values.put("//preConversionPolicyNumber", "ICAH"+randomNum);
			values.put("//sourcePolicyNum", "ICAH"+randomNum);
		} else {
			values.put("//preConversionPolicyNumber", randomNum);
		}
	}

	public ConversionType getConversionType() {
		return conversionType;
	}

	public File getFile() {
		return file;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValue(String key, String value) {
		values.put(key, value);
	}
}
