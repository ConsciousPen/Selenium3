package aaa.helpers.rest.dtoDxp;

import java.util.Map;

public class ComparablePolicy {
	public String changeType;
	public Map<String, ComparableDriver> drivers;
	public Map<String, ComparableObject<DriverCoverage>> driverCoverages;
	public Map<String, ComparableObject<PolicyCoverage>> policyCoverages;
	public Map<String, ComparableVehicle> vehicles;
}
