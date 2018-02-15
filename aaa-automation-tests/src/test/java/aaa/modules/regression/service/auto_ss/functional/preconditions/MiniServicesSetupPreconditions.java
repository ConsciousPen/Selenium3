package aaa.modules.regression.service.auto_ss.functional.preconditions;

public class MiniServicesSetupPreconditions {

	public static final String AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
			+ "        values ('BaseProductLookupValue', 'MyPolicy', '0', 'AAA_SS', 'AZ',(select to_char(to_date(SYSDATE, 'DD-MM-YYYY')) from dual), (select to_char(to_date(SYSDATE+10, 'DD-MM-YYYY')) from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAACustomerEndorsementDays'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'MyPolicy', '5', 'AAA_SS', 'AZ',(select to_char(to_date(SYSDATE+10, 'DD-MM-YYYY')) from dual), null ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAACustomerEndorsementDays'))\n"
			+ "Select * from dual";

	public static final String AAA_CUSTOMER_ENDORSMENT_DAYS_DEFAULD_CONFIG_UPDATE = "update lookupvalue\n"
			+ "set displayvalue = 2\n"
			+ "where 1=1\n"
			+ "    and lookuplist_id in (select id from lookuplist where lookupname = 'AAACustomerEndorsementDays')\n"
			+ "    and code = 'MyPolicy'\n"
			+ "    and DISPLAYVALUE = 0\n"
			+ "    and productCd is null\n"
			+ "    and RISKSTATECD is null\n"
			+ "    and EFFECTIVE is null\n"
			+ "    and expiration is null";

	public static final String AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_CHECK = "select dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id\n"
			+ "from lookupvalue \n"
			+ "where 1=1\n"
			+ "    and lookuplist_id in (select id from lookuplist where lookupname = 'AAACustomerEndorsementDays')\n"
			+ "    and code = 'MyPolicy'\n"
			+ "    and DISPLAYVALUE = %s\n"
			+ "    and RISKSTATECD %s";



	public static final String MY_POLICY_USER_ENABLE_UPDATE = "update s_principal set enabled = 1 where name = 'MyPolicy'";

	public static final String MY_POLICY_USER_ADD_ALL_PRIVILEGES_UPDATE = "INSERT INTO s_permissions (principal_ID, ALLOWED, AUTH_ID) \n"
			+ "values ((SELECT ID FROM S_PRINCIPAL WHERE NAME = 'MyPolicy'), '1',\n"
			+ "(SELECT ID FROM S_authority WHERE NAME = 'all' AND DTYPE = 'ROLE' AND DOMAIN = 'corporate'))";
}
