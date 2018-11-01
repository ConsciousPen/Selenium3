package aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions;

public interface TestRefundProcessPreConditions {

	String REFUND_CONFIG_CHECK = "select * from LOOKUPVALUE " +
			" WHERE LOOKUPLIST_ID IN (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME LIKE '%Rollout%' and CODE='eRefunds' and DISPLAYVALUE='TRUE' )";

	String LAST_PAYMENT_METHOD_STUB_END_POINT_CHECK = "select value from PROPERTYCONFIGURERENTITY " +
			" where propertyname = 'lastPaymentService.lastPaymentServiceUrl'"
			+ " and VALUE like '%%%s%%'";


	String PENDING_REFUND_PAYMENT_METHOD_CONFIG_CHECK = "select defaultrefundmethod from BILLINGREFUNDPAYMENTMETHOD\n"
			+ "where id = (select id from BILLINGREFUNDPAYMENTMETHOD)";

	String AUTHENTICATION_STUB_END_POINT_CHECK = "SELECT value FROM PROPERTYCONFIGURERENTITY" +
			" WHERE PROPERTYNAME = 'oAuthClient.oAuthPingUri' and lower(value) = lower('http://%s%sws/local/authentication')";
}
