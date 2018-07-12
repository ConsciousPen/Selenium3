package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Map;

public class ComparableVehicle extends ComparableObject<Vehicle> {
	public ComparableGaragingAddress garagingAddress;
	public ComparableOwnership vehicleOwnership;
	public Map<String, ComparableDriverAssignment> driverAssignments;
	public Map<String, ComparableCoverage> coverages;
}
