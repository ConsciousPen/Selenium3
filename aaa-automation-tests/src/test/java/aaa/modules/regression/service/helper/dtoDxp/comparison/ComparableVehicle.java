package aaa.modules.regression.service.helper.dtoDxp.comparison;

import java.util.Map;

public class ComparableVehicle {

	public String changeType;
	public ComparableVehicleData data;

	public ComparableAddress garagingAddress;
	public ComparableVehicleOwnership vehicleOwnership;
	public Map<String, ComparableDriverAssignment> driverAssignments;
	public Map<String, ComparableCoverage> coverages;
}
