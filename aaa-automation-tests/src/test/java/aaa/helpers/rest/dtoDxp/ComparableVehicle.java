package aaa.helpers.rest.dtoDxp;

import java.util.Map;

public class ComparableVehicle extends ComparableObject<Vehicle> {
	public ComparableObject<Address> garagingAddress;
	public ComparableObject<VehicleOwnership> vehicleOwnership;
	public Map<String, ComparableObject<DriverAssignment>> driverAssignments;
	public Map<String, ComparableObject<Coverage>> coverages;
}
