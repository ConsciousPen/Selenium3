package aaa.modules.regression.sales.auto_ss.functional.preconditions;

import aaa.helpers.config.CustomTestProperties;
import toolkit.config.PropertyProvider;

public interface TestEValueMembershipProcessPreConditions {

	String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);

	String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK = "select  value\n"
			+ "from PROPERTYCONFIGURERENTITY\n"
			+ "where propertyname ='retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	String MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null";

	String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK = "select displayValue from \n"
			+ "(SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null)";

	String EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'membershipEligibility', 'TRUE', 'AAA_SS', 'VA', null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE = "update LOOKUPVALUE\n"
			+ "set displayValue = '%s'\n"
			+ "WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n"
			+ "and EXPIRATION is null";
}
