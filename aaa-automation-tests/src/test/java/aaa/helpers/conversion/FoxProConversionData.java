package aaa.helpers.conversion;

import com.mifmif.common.regex.Generex;

import java.time.LocalDateTime;

public class FoxProConversionData extends ConversionPolicyData {

	public FoxProConversionData(String fileName, LocalDateTime effectiveDate) {
		super(ConversionType.FOXPRO, fileName, effectiveDate);
		conversionType = ConversionType.FOXPRO;

		String randomNum = new Generex("\\d{9}").random();
		values.put("//preConversionPolicyNumber", "ICAH"+randomNum);
		values.put("//sourcePolicyNum", "ICAH"+randomNum);

		values.put("//agentCd", "70683577");
		values.put("//subProducerCd", "70683577");
	}
}
