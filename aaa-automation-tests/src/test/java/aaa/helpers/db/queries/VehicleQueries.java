package aaa.helpers.db.queries;

public class VehicleQueries {

    public static final String SELECT_ID_FROM_VEHICLEREFDATAMODEL = "SELECT DM.id FROM vehiclerefdatamodel DM " +
            "JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
            "WHERE DV.version IN %1$s";
    public static final  String DELETE_FROM_VEHICLEREFDATAVIN_BY_VERSION = "DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN %1$s";
    public static final  String DELETE_FROM_VEHICLEREFDATAMODEL_BY_ID = "DELETE FROM vehiclerefdatamodel WHERE id='%s'";
    public static final  String DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION = "DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN %1$s";
    public static final  String UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE = "UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'MSRP_2000'";
    public static final  String UPDATE_CHOICE_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION = "UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'MSRP_2000_CHOICE' AND FORMTYPE='CHOICE'";
    public static final  String UPDATE_SELECT_VEHICLEREFDATAVINCONTROL_BY_MSRP_VERSION = "UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE MSRP_VERSION = 'MSRP_2000' AND FORMTYPE='SELECT'";
    public static final  String UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE_FORMTYPE = "UPDATE vehiclerefdatavincontrol VC SET expirationdate='99999999' WHERE VC.STATECD='%1$s' AND FORMTYPE = '%2$s'";
    public static final  String SELECT_VIN_NUMBER_BY_DATA = "select v.vin, v.version, v.valid, m.year, v.make_text make, v.model_text model, v.series_text series, v.bodytype_text bodystyle,"
            + "v.physicaldamagecollision, v.physicaldamagecomprehensive, v.stat, v.choice_symbol, v.choice_tier, v.bi_symbol, v.pd_symbol, v.um_symbol,"
            + " v.mp_symbol from vehiclerefdatavin v, vehiclerefdatamodel m where m.id = vehiclerefdatamodelid "
            + "and year = '%1$s' and v.make_text = '%2$s' and v.model_text = '%3$s' and v.series_text = '%4$s' and v.bodytype_text = '%5$s'";
}
