package aaa.helpers.conversion;

import aaa.helpers.jobs.Job;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ConversionPolicyData {

	protected ConversionType conversionType;

	protected File file;
	protected HashMap<String, String> values;

	public ConversionPolicyData(ConversionType type, String fileName) {
		this.conversionType = type;
		this.file = new File(type.getLocalTemplatesFolder(), fileName);
		values = new HashMap<>();

		values.put("//effective", TimeSetterUtil.getInstance().getCurrentTime().plusDays(58).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00");
		values.put("//expiration", TimeSetterUtil.getInstance().getCurrentTime().plusDays(58).plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00");
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
