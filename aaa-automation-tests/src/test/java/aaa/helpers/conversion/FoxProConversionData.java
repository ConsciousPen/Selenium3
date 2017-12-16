package aaa.helpers.conversion;

import com.mifmif.common.regex.Generex;

import java.time.LocalDateTime;

public class FoxProConversionData extends ConversionPolicyData {

	public FoxProConversionData(String fileName, LocalDateTime effectiveDate, String ho3Policy, String autoPolicy) {
		super(ConversionType.FOXPRO, fileName, effectiveDate);
		conversionType = ConversionType.FOXPRO;

		String randomNum = new Generex("\\d{9}").random();
		values.put("//preConversionPolicyNumber", "ICAH"+randomNum);
		values.put("//sourcePolicyNum", "ICAH"+randomNum);

		values.put("//agentCd", "70683577");
		values.put("//subProducerCd", "70683577");

		values.put("//AAAPupPrefill[policyType[.='HO3']]/policyNumber", ho3Policy);
		values.put("//AAAPupPrefill[policyType[.='Auto']]/policyNumber", autoPolicy);
	}
}
