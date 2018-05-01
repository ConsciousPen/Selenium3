package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Comparator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ComparisonChain;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "Vehicle Information")
public class Vehicle implements RestBodyRequest {

	public String modelYear;

	public String manufacturer;

	public String series;

	public String model;

	public String bodyStyle;

	public String oid;

	public String purchaseDate;

	public String vehIdentificationNo;

	public String vehicleStatus;

	public String ownership;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String usage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean salvaged;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean garagingDifferent;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String antiTheft;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean registeredOwner;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String vehTypeCd;

	public Address garagingAddress;

	public Address ownerAddress;

	public static class VehicleComparator implements Comparator<Vehicle> {
		private static final String VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO = "PPA";
		private static final String VEHICLE_STATUS_PENDING = "pending";
		private static final String VEHICLE_STATUS_ACTIVE = "active";

		@Override
		public int compare(Vehicle v1, Vehicle v2) {
			return ComparisonChain.start()
					.compareTrueFirst(VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(v1.vehTypeCd),
							VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(v2.vehTypeCd))
					.compareTrueFirst(VEHICLE_STATUS_PENDING.equals(v1.vehicleStatus),
							VEHICLE_STATUS_PENDING.equals(v2.vehicleStatus))
					.compareTrueFirst(VEHICLE_STATUS_ACTIVE.equals(v1.vehicleStatus),
							VEHICLE_STATUS_ACTIVE.equals(v2.vehicleStatus))
					.compare(v1.oid, v2.oid)
					.result();
		}
	}
}