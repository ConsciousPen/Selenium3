package aaa.modules.regression.sales.auto_ss.functional.postconditions;

public interface TestVinUploadPostConditions {
	String SELECT_ID_FROM_VEHICLEREFDATAMODEL = "SELECT DM.id FROM vehiclerefdatamodel DM " +
			"JOIN vehiclerefdatavin DV ON DV.vehiclerefdatamodelid=DM.id " +
			"WHERE DV.version IN %1$s";

	String DELETE_FROM_VEHICLEREFDATAVIN_BY_VERSION = "DELETE FROM vehiclerefdatavin V WHERE V.VERSION IN %1$s";

	String DELETE_FROM_VEHICLEREFDATAMODEL_BY_ID = "DELETE FROM vehiclerefdatamodel WHERE id='%s'";
	String DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION = "DELETE FROM vehiclerefdatavincontrol VC WHERE VC.version IN %1$s";
	//todo research what is going on below.
	String UPDATE = "UPDATE vehiclerefdatavincontrol SET expirationdate='99999999'";
}
