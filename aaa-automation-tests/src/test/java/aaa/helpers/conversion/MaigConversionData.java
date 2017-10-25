package aaa.helpers.conversion;

import com.mifmif.common.regex.Generex;

import java.time.LocalDateTime;

public class MaigConversionData extends ConversionPolicyData {

	public MaigConversionData(String fileName, LocalDateTime effectiveDate) {
		super(ConversionType.MAIG, fileName, effectiveDate);
		conversionType = ConversionType.MAIG;

		String randomNum = new Generex("\\d{9}").random();
		values.put("//sourcePolicyNum", randomNum);
	}
}
