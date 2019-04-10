package aaa.modules.regression.service.helper;

import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.CardSubTypeEnum.CREDIT;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.CardSubTypeEnum.DEBIT;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.EligibilityStatusEnum.REFUNDABLE;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.PaymentMethodEnum.CRDC;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.PaymentMethodEnum.EFT;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.PaymentMethodSubTypeEnum.MC;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.PaymentMethodSubTypeEnum.VISA;
import org.testng.annotations.Test;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData;
import toolkit.config.PropertyProvider;

public class HelperWireMockLastPaymentMethod {

	public HelperWireMockStub getHelperWireMockStubACH(String policyNumber, String refundAmountACH) {
		LastPaymentTemplateData dataACH = LastPaymentTemplateData.create(policyNumber, refundAmountACH, REFUNDABLE, "refundable", EFT, null, null, "1542", null);
		return HelperWireMockStub.create("last-payment-200", dataACH).mock();
	}

	public HelperWireMockStub getHelperWireMockStubCC(String policyNumber, String refundAmountCC) {
		LastPaymentTemplateData dataCC = LastPaymentTemplateData.create(policyNumber, refundAmountCC, REFUNDABLE, "refundable", CRDC, VISA, CREDIT, "4113", "01-2022");
		return HelperWireMockStub.create("last-payment-200", dataCC).mock();
	}

	public HelperWireMockStub getHelperWireMockStubDC(String policyNumber, String refundAmountDC) {
		LastPaymentTemplateData dataDC = LastPaymentTemplateData.create(policyNumber, refundAmountDC, REFUNDABLE, "refundable", CRDC, MC, DEBIT, "4444", "05-2020");
		return HelperWireMockStub.create("last-payment-200", dataDC).mock();
	}

	public HelperWireMockStub stub404NotFound(String policyNumber) {
		LastPaymentTemplateData templateData = LastPaymentTemplateData.create(policyNumber, null, null, null, null, null, null, null, null);
		return HelperWireMockStub.create("last-payment-error", templateData).mock();
	}


	/**The test is used to check LastPaymentMethod stub functionality and availability
	 */
	@Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
	public void pas111_wireMockExampleTest() {

		String policyNumber = "VASS952918785";
		String approvedRefundAmount = "500";
		//CC
		LastPaymentTemplateData dataCC = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE, "refundable", CRDC, VISA, CREDIT, "5555", "11-2021");
		HelperWireMockStub stubRequestCC = HelperWireMockStub.create("last-payment-200", dataCC).mock();


		stubRequestCC.cleanUp();

		//DC
		LastPaymentTemplateData dataDC = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE, "refundable", CRDC, VISA, DEBIT, "4444", "11-2021");
		HelperWireMockStub stubRequestDC = HelperWireMockStub.create("last-payment-200", dataDC).mock();


		stubRequestDC.cleanUp();

		//ACH
		LastPaymentTemplateData dataACH = LastPaymentTemplateData.create(policyNumber, approvedRefundAmount, REFUNDABLE, "refundable", EFT, null, null, "1234", null);
		HelperWireMockStub stubRequestACH = HelperWireMockStub.create("last-payment-200", dataACH).mock();


		stubRequestACH.cleanUp();
	}
}
