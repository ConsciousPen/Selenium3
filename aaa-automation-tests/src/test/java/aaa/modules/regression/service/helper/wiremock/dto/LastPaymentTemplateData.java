package aaa.modules.regression.service.helper.wiremock.dto;

import java.util.Random;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.joda.time.DateTime;
import aaa.helpers.config.CustomTestProperties;
import toolkit.config.PropertyProvider;

/**
 * Last payment template data. Contains properties defining each replaceable element in last payment WireMock template.
 */
public class LastPaymentTemplateData implements WireMockTemplateData {

	private static final String URL_PATH_PATTERN = "/%s/billing/lastPayment";
	private static final String MATCHES_JSON_PATH = "$[?(@.agreementNumber == '%s')]";

	private String urlPath;
	private String matchesJsonPath;
	private String eligibilityStatus;
	private String eligibilityStatusDescription;
	private String transactionDateTime;
	private String transactionId;
	private String lineItemAmt;
	private String paymentMethod;
	private String paymentMethodSubType;
	private String paymentAccountLast4;
	private String cardSubType;
	private String cardExpirationDate;

	/**
	 *
	 * @param policyNumber - policy number
	 * @param refundableAmt - amount
	 * @param eligibilityStatus - REFUNDABLE/NON_REFUNDABLE
	 * @param eligibilityStatusDescription - Some free text, like Refund Method is mandatory for Manual Refund
	 * @param paymentMethod - CRDC/EFT
	 * @param paymentMethodSubType - VISA/MC/DISCOVER/AMEX , null for EFT
	 * @param cardSubType - CREDIT/DEBIT , null for EFT
	 * @param last4 - last 4 digits of CC/DC or EFT number
	 * @param cardExpirationDate - format DD-YYYY with a Dash
	 * @return
	 */
	public static LastPaymentTemplateData create(String policyNumber, String refundableAmt,
			String eligibilityStatus, String eligibilityStatusDescription,
			String paymentMethod, String paymentMethodSubType, String cardSubType,
			String last4, String cardExpirationDate) {
		final LastPaymentTemplateData data = new LastPaymentTemplateData();
		data.urlPath = String.format(URL_PATH_PATTERN, PropertyProvider.getProperty(CustomTestProperties.APP_HOST));
		data.matchesJsonPath = String.format(MATCHES_JSON_PATH, policyNumber);
		data.eligibilityStatus = eligibilityStatus;
		data.eligibilityStatusDescription = eligibilityStatusDescription;
		data.transactionDateTime = ISO8601DateFormat.getDateTimeInstance().format(DateTime.now().toDate());
		final Random rnd = new Random();
		data.transactionId = Integer.valueOf(100000 + rnd.nextInt(900000)).toString();
		data.lineItemAmt = refundableAmt;
		data.paymentMethod = paymentMethod;
		data.paymentMethodSubType = paymentMethodSubType;
		data.paymentAccountLast4 = last4;
		data.cardSubType = cardSubType;
		data.cardExpirationDate = cardExpirationDate;
		return data;
	}
}
