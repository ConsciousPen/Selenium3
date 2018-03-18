package aaa.modules.regression.service.helper;

import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.CardSubTypeEnum.CREDIT;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.CardSubTypeEnum.DEBIT;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.EligibilityStatusEnum.REFUNDABLE;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.PaymentMethodEnum.CRDC;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.PaymentMethodEnum.EFT;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.PaymentMethodSubTypeEnum.MC;
import static aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData.PaymentMethodSubTypeEnum.VISA;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.service.helper.wiremock.HelperWireMockStub;
import aaa.modules.regression.service.helper.wiremock.dto.LastPaymentTemplateData;
import toolkit.config.PropertyProvider;

public class HelperWireMockLastPaymentMethod {
	private static final String WIRE_MOCK_URL = PropertyProvider.getProperty(CustomTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/__admin/mappings";

	public HelperWireMockStub getHelperWireMockStubACH(String policyNumber, String refundAmountACH) throws IllegalAccessException {
		LastPaymentTemplateData dataACH = LastPaymentTemplateData.create(policyNumber, refundAmountACH, "REFUNDABLE", "refundable", EFT.get(), null, null, "1542", null);
		HelperWireMockStub stubRequestACH = HelperWireMockStub.create("last-payment-200", dataACH).mock();
		return stubRequestACH;
	}

	public HelperWireMockStub getHelperWireMockStubCC(String policyNumber, String refundAmountCC) throws IllegalAccessException {
		LastPaymentTemplateData dataCC = LastPaymentTemplateData.create(policyNumber, refundAmountCC, "REFUNDABLE", "refundable", CRDC.get(), VISA.get(), CREDIT.get(), "4113", "01-2022");
		HelperWireMockStub stubRequestCC = HelperWireMockStub.create("last-payment-200", dataCC).mock();
		return stubRequestCC;
	}

	public HelperWireMockStub getHelperWireMockStubDC(String policyNumber, String refundAmountDC) throws IllegalAccessException {
		LastPaymentTemplateData dataDC = LastPaymentTemplateData.create(policyNumber, refundAmountDC, "REFUNDABLE", "refundable", CRDC.get(), MC.get(), DEBIT.get(), "4444", "05-2020");
		HelperWireMockStub stubRequestDC = HelperWireMockStub.create("last-payment-200", dataDC).mock();
		return stubRequestDC;
	}


	/**The test is used to check LastPaymentMethod stub functionality and availability
	 * @throws IllegalAccessException
	 */
	@Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
	public void pas111_wireMockExampleTest() throws IllegalAccessException {

		String policyNumber = "aaaaa";
		String approvedRefundAmount = "500";
		//CC
		LastPaymentTemplateData dataCC = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE.get(), "refundable", "CRDC", "VISA", "CREDIT", "5555", "11-2021");
		HelperWireMockStub stubRequestCC = HelperWireMockStub.create("last-payment-200", dataCC).mock();


		stubRequestCC.cleanUp();

		//DC
		LastPaymentTemplateData dataDC = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE.get(), "refundable", "CRDC", "VISA", "DEBIT", "4444", "11-2021");
		HelperWireMockStub stubRequestDC = HelperWireMockStub.create("last-payment-200", dataDC).mock();


		stubRequestDC.cleanUp();

		//ACH
		LastPaymentTemplateData dataACH = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE.get(), "refundable", "EFT", null, null, "1234", null);
		HelperWireMockStub stubRequestACH = HelperWireMockStub.create("last-payment-200", dataACH).mock();


		stubRequestACH.cleanUp();
	}
}
