package aaa.helpers.db.queries;

import aaa.helpers.constants.Groups;
import org.testng.annotations.Test;
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
			"values ('VehicleStatCodeLookupValue','AN','000','Small car','AAA_SS','ID','02-JUN-18 12.00.00.000000000 AM',null,'PPA',null,0)";

	public static final String UPDATE_VEHICLE_STATE_CODE_VALUES = "update lookupValue set LOOKUPLIST_ID = (select id from LOOKUPLIST where LOOKUPLIST.LOOKUPNAME = 'AAAVehicleStatCode')\n"+
			"where LOOKUPLIST_ID is null";

	@Test(description = "Insert Values For Vehicle STAT Code", groups = {Groups.PRECONDITION})
	public static void insertStatCodeValues() {
		DBService.get().executeUpdate(INSERT_VEHICLE_STATE_CODE_LOOKUP);
		DBService.get().executeUpdate(UPDATE_VEHICLE_STATE_CODE_VALUES);
	}
}
