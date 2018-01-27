package aaa.helpers.db.queries;

public class LookupQueries {
	public static final String UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAAMembershipConfigLookup' AND RISKSTATECD = '%2$s')";
}
