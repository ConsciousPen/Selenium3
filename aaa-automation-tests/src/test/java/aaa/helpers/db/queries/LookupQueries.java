package aaa.helpers.db.queries;

import toolkit.db.DBService;

public class LookupQueries {
	public static final String UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP =
			"UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAAMembershipConfigLookup' AND RISKSTATECD = '%2$s')";

	// debug queries
	public static final String SELECT_AAA_ROLLOUT_ELIGIBILITY_LOOKUP_VIN_REFRESH =
			"select * from LOOKUPVALUE where LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	public static final String SELECT_AAA_MEMBERSHIP_CONFIG_LOOKUP =
			"select * from LOOKUPVALUE where LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAAMembershipConfigLookup')";

	//PAS-12879 Ensure STAT Code is "refreshed" as part of Vehicle/MSRP Refresh

	public static final String INSERT_VEHICLE_STATE_CODE_LOOKUP = "Insert into Lookupvalue (DTYPE,CODE,DESCRIPTION,DISPLAYVALUE,PRODUCTCD,RISKSTATECD,EFFECTIVE,EXPIRATION,TYPECD,TRAILERTYPECD,OLDER) \n" +
			"values ('VehicleStatCodeLookupValue','AN','AX','Small car','AAA_SS','ID','20-MAY-18 12.00.00.000000000 AM',null,'PPA',null,0)";

	public static final String UPDATE_VEHICLE_STATE_CODE_VALUES = "update lookupValue set LOOKUPLIST_ID = (select id from LOOKUPLIST where LOOKUPLIST.LOOKUPNAME = 'AAAVehicleStatCode')\n"+
			"where LOOKUPLIST_ID is null";

	/**
	 * Returns the AAA Membership Member Since Date from DB.
	 * @param quoteOrPolicyNumber
	 * @return
	 */
	public static java.util.Optional<String> GetAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber){

		String quoteOrPolicyColumn = quoteOrPolicyNumber.toUpperCase().startsWith("Q") ? "quotenumber" : "policynumber";

		String columnToJoinOn = String.format("AND ps.%1s ='%2s' ", quoteOrPolicyColumn, quoteOrPolicyNumber);

		String query =
				"SELECT MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE " +
						"FROM policysummary ps " +
						"JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' " +
						columnToJoinOn +
						"JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID";

		return DBService.get().getValue(query);
	}

	public static void insertStatCodeValues() {
		DBService.get().executeUpdate(INSERT_VEHICLE_STATE_CODE_LOOKUP);
		DBService.get().executeUpdate(UPDATE_VEHICLE_STATE_CODE_VALUES);
	}
}