package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.rest.dtoAdmin.InstallmentFeesResponse;
import aaa.modules.policy.PolicyBaseTest;

public abstract class TestInstallmentFeesAbstract extends PolicyBaseTest {

	protected void pas240_installmentFeesServiceBody(String productCode, String state, String eftAmountValue, String ccAmountValue, String directAmountValue, String dcAmountValue) {
		String date = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		InstallmentFeesResponse[] response = HelperCommon.executeInstallmentFeesRequest(productCode, state, date);
		InstallmentFeesResponse eftAmount = Arrays.stream(response).filter(InstallmentFeesResponse -> "eft".equals(InstallmentFeesResponse.code)).findFirst().orElse(null);
		assertThat(eftAmount.amount).isEqualTo(eftAmountValue);
		InstallmentFeesResponse pciCreditCardAmount = Arrays.stream(response).filter(InstallmentFeesResponse -> "pciCreditCard".equals(InstallmentFeesResponse.code)).findFirst().orElse(null);
		assertThat(pciCreditCardAmount.amount).isEqualTo(ccAmountValue);
		InstallmentFeesResponse directAmount = Arrays.stream(response).filter(InstallmentFeesResponse -> "direct".equals(InstallmentFeesResponse.code)).findFirst().orElse(null);
		assertThat(directAmount.amount).isEqualTo(directAmountValue);
		InstallmentFeesResponse pciDebitCardAmount = Arrays.stream(response).filter(InstallmentFeesResponse -> "pciDebitCard".equals(InstallmentFeesResponse.code)).findFirst().orElse(null);
		assertThat(pciDebitCardAmount.amount).isEqualTo(dcAmountValue);
	}

}
