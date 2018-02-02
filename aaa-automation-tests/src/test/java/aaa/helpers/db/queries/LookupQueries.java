package aaa.helpers.db.queries;

public class LookupQueries {
	public static final String UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP =
			"UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAAMembershipConfigLookup' AND RISKSTATECD = '%2$s')";

	// debug queries
	public static final String SELECT_AAA_ROLLOUT_ELIGIBILITY_LOOKUP_VIN_REFRESH =
			"select * from LOOKUPVALUE where LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	public static final String SELECT_AAA_MEMBERSHIP_CONFIG_LOOKUP =
			"select * from LOOKUPVALUE where LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAAMembershipConfigLookup')";
}
