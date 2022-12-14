package aaa.helpers.db.queries;

public class VehicleQueries {

	public static final String SELECT_ID_FROM_VEHICLEREFDATAMODEL = "SELECT DM.id FROM vehiclerefdatamodel DM " +
			"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
			"WHERE DV.version IN %1$s";
	public static final String SELECT_VIN_NUMBER_BY_DATA = "select v.vin, v.version, v.valid, m.year, v.make_text make, v.model_text model, v.series_text series, v.bodytype_text bodystyle,"
			+ "v.physicaldamagecollision, v.physicaldamagecomprehensive, v.stat, v.choice_symbol, v.choice_tier, v.bi_symbol, v.pd_symbol, v.um_symbol,"
			+ " v.mp_symbol from vehiclerefdatavin v, vehiclerefdatamodel m where m.id = vehiclerefdatamodelid "
			+ "and year = '%1$s' and v.make_text = '%2$s' and v.model_text = '%3$s' and v.series_text = '%4$s' and v.bodytype_text = '%5$s'";
	public static final String SELECT_FROM_VEHICLERATINGINFO_BY_QUOTE_NUMBER = "SELECT i.%1$s "
			+ "From Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd Where R.Ratinginfo_Id = I.Id And B.Id = R.Baseinfo_Id And "
			+ "ps.policydetail_id = pd.id and pd.id = r.policydetail_id and ps.policynumber = '%2$s'";

	public static final String SELECT_VIN_STUB_ON_QUOTE = "SELECT r.currentVin FROM Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd WHERE R.Ratinginfo_Id = I.Id AND B.Id = R.Baseinfo_Id AND ps.policydetail_id = pd.id AND pd.id = r.policydetail_id AND policynumber LIKE '%1$s'";

	//	VIN STUB QUERIES FOR PAS-12877
	// SORT BY r.ID DESC ORDER.
	public static final String SELECT_VIN_ID_BY_VIN_VERSION = "SELECT v.* FROM VEHICLEREFDATAVIN v WHERE VIN LIKE '%s' and v.VERSION='%s'";
	public static final String SELECT_ID_BY_VIN_VERSION = "SELECT ID FROM VEHICLEREFDATAVIN WHERE VIN LIKE '%s'";
	public static final String SELECT_LATEST_VIN_STUB_ON_QUOTE = "SELECT r.currentVin FROM Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd WHERE R.Ratinginfo_Id = I.Id AND B.Id = R.Baseinfo_Id AND ps.policydetail_id = pd.id AND pd.id = r.policydetail_id AND policynumber LIKE '%1$s' ORDER BY r.ID DESC";
	public static final String SELECT_LATEST_VIN_STUB_WITH_SYMBOLS_ON_QUOTE = "SELECT r.currentVin,I.COMPSYMBOL, I.COLLSYMBOL , R.version FROM Riskitem R, Vehicleratinginfo I, Vehiclebaseinfo B, Policysummary Ps, Policydetail Pd WHERE R.Ratinginfo_Id = I.Id AND B.Id = R.Baseinfo_Id AND ps.policydetail_id = pd.id AND pd.id = r.policydetail_id AND policynumber LIKE '%1$s' ORDER BY r.ID DESC";

	public static final String COPY_EXISTING_ROW_BY_VIN = "INSERT INTO VEHICLEREFDATAVIN (SELECT * FROM VEHICLEREFDATAVIN WHERE VIN LIKE '%s' and VERSION = '%s')";
	public static final String COPY_EXISTING_ROW_BY_ID = "INSERT INTO VEHICLEREFDATAVIN (SELECT * FROM VEHICLEREFDATAVIN WHERE id = '%s' and VERSION = '%s')";

	public static final String UPDATE_ID_FOR_COPIED_ROW = "UPDATE VEHICLEREFDATAVIN SET id = '%s' WHERE VIN LIKE '%s' and VERSION='%s' AND ROWNUM = 1";
	public static final String UPDATE_COMP_COLL_SYMBOL = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOLLISION ='%d' , PHYSICALDAMAGECOMPREHENSIVE = '%d' where id = '%s' and vin like '%s' and VERSION = '%s'";

	public static final String UPDATE_COMP_VIN = "UPDATE Vehiclerefdatavin SET VIN ='%s' where vin like '%s' and  PHYSICALDAMAGECOMPREHENSIVE = '%d'";
	public static final String UPDATE_NO_COMP_VIN = "UPDATE Vehiclerefdatavin SET VIN ='%s' where vin like '%s' AND ID ='%s'";

	public static final String NULL_POLICY_STUB = "UPDATE Riskitem SET CURRENTVIN = NULL WHERE CURRENTVIN like 'JH4CU2F4%C'";
	public static final String EDIT_COMP_VALUE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE = PHYSICALDAMAGECOMPREHENSIVE + 50 where vin like '5TFEZ5CN%H' and VERSION like 'SYMBOL_2000'";
	public static final String REPAIR_COMP_VALUE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE = PHYSICALDAMAGECOMPREHENSIVE - 50 where vin like '5TFEZ5CN%H' and VERSION like 'SYMBOL_2000'";
	public static final String MINUS_50_FROM_COMP_VALUE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE = PHYSICALDAMAGECOMPREHENSIVE - 50 where vin like '%s' and VERSION like 'SYMBOL_2000'";

	public static final String NULL_SPECIFIC_POLICY_STUB = "UPDATE Riskitem SET CURRENTVIN = NULL WHERE CURRENTVIN like '%s'";
	public static final String EDIT_SPECIFIC_COMP_VALUE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE = PHYSICALDAMAGECOMPREHENSIVE + 50 where vin like '%s' and VERSION like '%s'";
	public static final String REPAIR_COLLCOMP_BY_ID = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOLLISION ='%s', PHYSICALDAMAGECOMPREHENSIVE ='%s' where id = '%s' and VERSION like '%s'";

	public static final String REPAIR_7MSRP15H_COMP = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE ='44' where vin like '7MSRP15H%V' and VERSION like 'SYMBOL_2000'";
	public static final String REPAIR_7MSRP15H_COLL = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOLLISION ='35' where vin like '7MSRP15H%V' and VERSION like 'SYMBOL_2000'";
	public static final String REPAIR_COLLCOMP = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOLLISION ='35', PHYSICALDAMAGECOMPREHENSIVE ='44' where vin like '%s' and VERSION like 'SYMBOL_2000'";
	public static final String REPAIR_7MSRP15H_COMP_CHOICE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOMPREHENSIVE ='43' where vin like '7MSRP15H%V' and VERSION like 'SYMBOL_2000_CHOICE'";
	public static final String REPAIR_7MSRP15H_COLL_CHOICE = "UPDATE Vehiclerefdatavin SET PHYSICALDAMAGECOLLISION ='33' where vin like '7MSRP15H%V' and VERSION like 'SYMBOL_2000_CHOICE'";

	public static final String DELETE_VEHICLEREFDATAVIN_BY_ID = "DELETE FROM VEHICLEREFDATAVIN WHERE ID = '%s'";
	public static final String DELETE_FROM_VEHICLEREFDATAVIN_BY_VERSION = "DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN %1$s";
	public static final String DELETE_FROM_VEHICLEREFDATAVIN_BY_VIN_AND_VERSION = "DELETE FROM VEHICLEREFDATAVIN v WHERE VIN like '%1$s' AND VERSION = '%2$s'";
	public static final String DELETE_FROM_VEHICLEREFDATAMODEL_VIN_AND_MAKE = "DELETE FROM VEHICLEREFDATAVIN v WHERE VIN like 'JA3AY26A%X' AND MAKE = 'TEST'";
	public static final String DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION = "DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN %1$s";
	public static final String DELETE_LIABILITY_SYMBOLS_FOR_VIN =
			"UPDATE Vehiclerefdatavin V SET V.bi_symbol= null, V.mp_symbol = null, V.pd_symbol = null, V.um_symbol = null, V.PHYSICALDAMAGECOLLISION = null, V.PHYSICALDAMAGECOMPREHENSIVE = null WHERE V.vin LIKE '%s'";

	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE = "UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'SYMBOL_2000'";
	public static final String UPDATE_CHOICE_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION =
			"UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'MSRP_2000_CHOICE' AND FORMTYPE='CHOICE'";
	public static final String UPDATE_SELECT_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION =
			"UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'MSRP_2000_SELECT' AND FORMTYPE='SELECT'";
	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE_FORMTYPE =
			"UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE VC.STATECD='%1$s' AND FORMTYPE = '%2$s'";

	public static final String RESTORE_LIABILITY_SYMBOLS_FOR_VIN =
			"UPDATE Vehiclerefdatavin V SET V.bi_symbol= 'K', V.mp_symbol = 'K', V.pd_symbol = 'K', V.um_symbol = 'K', V.PHYSICALDAMAGECOLLISION = '15', V.PHYSICALDAMAGECOMPREHENSIVE = '15' WHERE V.vin LIKE '%s'";

	/* Vin refresh enable/disable queries */
	public static final String SELECT_VALUE_VIN_REFRESH = "select DISPLAYVALUE from LOOKUPVALUE WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";
	public static final String UPDATE_DISPLAYVALUE_BY_CODE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST "
			+ "WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";
	public static final String PAYMENT_CENTRAL_CONFIG_CHECK = "select value from PROPERTYCONFIGURERENTITY where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')";

	public static final String INSERT_EFF_DATE_INTO_CONTROL_TABLE = "update VEHICLEREFDATAVINCONTROL set EFFECTIVEDATE = '%s' where EFFECTIVEDATE = '0'";
	public static final String INSERT_EXP_DATE_INTO_CONTROL_TABLE = "update VEHICLEREFDATAVINCONTROL set EXPIRATIONDATE = '%s' where EXPIRATIONDATE = '0'";

	public static final String INSERT_VEHICLEREFDATAVINCONTROL_VERSION =
			"Insert into VEHICLEREFDATAVINCONTROL (ID,PRODUCTCD,FORMTYPE,STATECD,VERSION,EFFECTIVEDATE,EXPIRATIONDATE,MSRP_VERSION) values"
					+ "(%1$d,'%2$s',%3$s,'%4$s','%5$s','%6$d','%7$d','%8$s')";

	//Cleanup queries for file upload PAS-12872
	public static final String UPDATE_VEHICLEREFDATAVIN_VALID_BY_VIN_VERSION_MAKETEXT = "UPDATE VEHICLEREFDATAVIN SET VALID = '%1$s' WHERE VIN LIKE '%2$s' AND VERSION ='%3$s' AND MAKE_TEXT ='%4$s'";

	public static final String DELETE_VEHICLEREFDATAVIN_BY_VIN_MAKETEXT = "DELETE FROM VEHICLEREFDATAVIN WHERE VIN LIKE '%1$s' AND MAKE_TEXT = '%2$s'";


	//Update queries for PAS-14532/PAS-16150
	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION = "update VEHICLEREFDATAVINCONTROL set EXPIRATIONDATE = '%1$s' where STATECD = '%2$S' and VERSION = '%3$S'";
	public static final String UPDATE_VEHICLEREFDATAVINCONTROL_EFFECTIVEDATE_BY_STATECD_VERSION = "update VEHICLEREFDATAVINCONTROL set EFFECTIVEDATE = '%1$s' where STATECD = '%2$S' and VERSION = '%3$S'";
	public static final String DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION = "delete from VEHICLEREFDATAVINCONTROL where STATECD = '%1$S' and VERSION = '%2$S'";
}
