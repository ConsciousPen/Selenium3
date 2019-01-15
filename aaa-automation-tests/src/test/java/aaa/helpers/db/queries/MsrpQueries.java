package aaa.helpers.db.queries;

import static aaa.helpers.db.DbAwaitHelper.getQueryResult;
import java.math.BigInteger;
import java.util.Random;

public class MsrpQueries {
	public static final String UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX = "UPDATE MSRPCompCollCONTROL SET VEHICLEYEARMAX = %1$d WHERE MSRPVERSION = '%2$s'";
	public static final String UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_BY_VERSION_VEHICLEYEARMIN = "UPDATE MSRPCompCollCONTROL SET VEHICLEYEARMAX = %1$d WHERE MSRPVERSION = '%2$s' and VEHICLEYEARMIN = %3$d";
	public static final String UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_VEHICLEYEARMIN_KEY =
			"UPDATE MSRPCompCollCONTROL SET VEHICLEYEARMAX = %1$d WHERE VEHICLEYEARMIN = %2$d AND KEY = %3$d";

	public static final String INSERT_VERSION_INTO_MSRP_COMP_COLL_CONTROL = "insert into MsrpCompCollControl(vehicleyearmin, vehicleyearmax, vehicletype, liabilitysymbol, msrpversion, key)"
			+ "values(%1$d,%2$d,'%3$s',null,'%4$s',%5$d)";
	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION =
			"UPDATE vehiclerefdatavincontrol SET expirationdate='%1$d' WHERE STATECD='%2$s' AND MSRP_VERSION='%3$s'";
	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_MSRPVERSION_FORMTYPE =
			"UPDATE vehiclerefdatavincontrol SET expirationdate='%1$d' WHERE STATECD='%2$s' AND MSRP_VERSION='%3$s' AND FORMTYPE = '%4$s'";
	public static final String SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID = "SELECT MAX(id) FROM VEHICLEREFDATAVINCONTROL";

	public static final String INSERT_MSRPCOMPCOLLCONTROL_VERSION = "insert into MsrpCompCollControl(vehicleyearmin, vehicleyearmax, vehicletype, liabilitysymbol, msrpversion, key) values "
			+ "(%1$d,%2$d,'%3$s',null,'%4$s',%5$d)";

	public static final String INSERT_MSRPCOMPCOLLCONTROL_VERSION_SELECT = "insert into MsrpCompCollControl(vehicleyearmin, vehicleyearmax, vehicletype, liabilitysymbol, msrpversion, key) values "
			+ "(%1$d,%2$d,'%3$s','%4$s','%5$s',%6$d)";

	public static final String DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY = "DELETE from MSRPCompCollCONTROL WHERE MSRPVERSION = '%1$s' AND KEY = %2$d AND VEHICLETYPE = '%3$s'";
	public static final String DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD = "DELETE FROM VEHICLEREFDATAVINCONTROL WHERE MSRP_VERSION = '%1$s' AND STATECD = '%2$s'";

	public static final String AUTO_SS_PPA_VEH_MSRP_VERSION = "MSRP_2017";

	public static final String CA_CHOICE_REGULAR_VEH_MSRP_VERSION = "MSRP_2000_CHOICE";

	public static final String CA_SELECT_REGULAR_VEH_MSRP_VERSION = "MSRP_2000_SELECT";

	public static BigInteger getAvailableIdFromVehicleDataVinControl() {
		Long uniqId = Long.parseLong(getQueryResult(SELECT_VEHICLEREFDATAVINCONTROL_MAX_ID, 5));
		return BigInteger.valueOf(uniqId).add(BigInteger.valueOf(new Random().nextInt(100)));
	}
}
