package aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions;

public interface TestRefundProcessPreConditions {

	String REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL = "select dtype, code, displayValue, productCd, riskStateCd, lookuplist_id from LOOKUPVALUE " +
			"where lookuplist_ID = (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup') " +
			"and code = 'pcDisbursementEngine' ";

	String REFUND_CONFIG_CHECK = "select * from LOOKUPVALUE " +
			" WHERE LOOKUPLIST_ID IN (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME LIKE '%Rollout%' and CODE='eRefunds' and DISPLAYVALUE='TRUE' )";

	String LAST_PAYMENT_METHOD_STUB_END_POINT_CHECK = "select value from PROPERTYCONFIGURERENTITY " +
			" where propertyname = 'lastPaymentService.lastPaymentServiceUrl' and value = 'http://%s:9098/aaa-external-stub-services-app/ws/billing/lastPayment'";

	String PENDING_REFUND_PAYMENT_METHOD_CONFIG_CHECK = "select defaultrefundmethod from BILLINGREFUNDPAYMENTMETHOD\n"
			+ "where id = (select id from BILLINGREFUNDPAYMENTMETHOD)";

	String AUTHENTICATION_STUB_END_POINT_CHECK = "SELECT value FROM PROPERTYCONFIGURERENTITY" +
			" WHERE PROPERTYNAME = 'oAuthClient.oAuthPingUri' and value = 'http://%s:9098/aaa-external-stub-services-app/ws/local/authentication'";
}
