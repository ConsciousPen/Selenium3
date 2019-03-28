package aaa.modules.regression.service.helper.preconditions;

public class TestMiniServicesNonPremiumBearingAbstractPreconditions {
	public static final String ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_ADD_WY = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd,  EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'XXXX', 'FALSE', 'AAA_SS', 'WY', (select to_char(to_date(SYSDATE-10, 'DD-MM-YYYY')) from dual), (select to_char(to_date(SYSDATE-5, 'DD-MM-YYYY')) from dual),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentMethods'))";

	public static final String ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_CHANGE_WY = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd,  EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'pciDebitCard', 'FALSE', 'AAA_SS', 'WY', (select to_char(to_date(SYSDATE-4, 'DD-MM-YYYY')) from dual), null,\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentMethods'))";

	public static final String DELETE_ADDED_PAYMENT_METHOD_CONFIGS_WY = "delete lookupvalue\n"
			+ "where 1=1\n"
			+ "and lookuplist_id = (select id from lookuplist where lookupname = 'AAAeValueQualifyingPaymentMethods')\n"
			+ "and riskstatecd = 'WY'";

	public static final String ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_ADD_WY = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'XXXX', 'FALSE', 'AAA_SS', 'WY', (select to_char(to_date(SYSDATE-10, 'DD-MM-YYYY')) from dual), (select to_char(to_date(SYSDATE-5, 'DD-MM-YYYY')) from dual),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentPlans'))";

	public static final String ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_CHANGE_WY = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'annualSS', 'FALSE', 'AAA_SS', 'WY', (select to_char(to_date(SYSDATE-4, 'DD-MM-YYYY')) from dual), null,\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentPlans'))";

	public static final String DELETE_ADDED_PAYMENT_PLANS_CONFIGS_WY = "delete lookupvalue\n"
			+ "where 1=1\n"
			+ "and lookuplist_id = (select id from lookuplist where lookupname = 'AAAeValueQualifyingPaymentPlans')\n"
			+ "and riskstatecd = 'WY'";

}
