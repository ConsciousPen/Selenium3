package aaa.helpers.conversion;

import com.mifmif.common.regex.Generex;

import java.time.LocalDateTime;

public class SisConversionData extends ConversionPolicyData {

	public SisConversionData(String fileName, LocalDateTime effectiveDate) {
		super(ConversionType.SIS, fileName, effectiveDate);
		conversionType = ConversionType.SIS;

		String randomNum = new Generex("\\d{9}").random();
		values.put("//preConversionPolicyNumber", randomNum);

		values.put("//agentCd", "70683577");
		values.put("//subProducerCd", "70683577");
	}
}
