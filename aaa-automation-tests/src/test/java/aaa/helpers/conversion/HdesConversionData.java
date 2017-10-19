package aaa.helpers.conversion;

import com.mifmif.common.regex.Generex;

import java.time.LocalDateTime;

public class HdesConversionData extends ConversionPolicyData {

	public HdesConversionData(String fileName, LocalDateTime effectiveDate) {
		super(ConversionType.HDES, fileName, effectiveDate);
		conversionType = ConversionType.HDES;

		String randomNum = new Generex("\\d{9}").random();
		values.put("//preConversionPolicyNumber", randomNum);

		values.put("//agentCd", "70683577");
		values.put("//subProducerCd", "70683577");
	}
}
