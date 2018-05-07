package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Comparator;
import com.google.common.collect.ComparisonChain;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Vehicle Information")
public class Vehicle implements RestBodyRequest {

	@ApiModelProperty(value = "Model year", example = "2003")
	public String modelYear;

	@ApiModelProperty(value = "Manufacturer", example = "Ferrari")
	public String manufacturer;

	@ApiModelProperty(value = "Series", example = "Enzo")
	public String series;

	@ApiModelProperty(value = "Model", example = "Enzo")
	public String model;


	@ApiModelProperty(value = "Body style", example = "Coupe")
	public String bodyStyle;

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "Purchase Date", example = "2012-02-21")
	public String purchaseDate;

	@ApiModelProperty(value = "VIN", example = "ZFFCW56A830133118")
	public String vehIdentificationNo;

	@ApiModelProperty(value = "vehicleStatus", example = "pending")
	public String vehicleStatus;

	@ApiModelProperty(value = "Usage", example = "Pleasure")
	public String usage;

	@ApiModelProperty(value = "Salvaged", example = "false")
	public Boolean salvaged;

	@ApiModelProperty(value = "Garaging different than the Residential?", example = "false")
	public Boolean garagingDifferent;


	@ApiModelProperty(value = "Anti-Theft", example = "NONE")
	public String antiTheft;

	@ApiModelProperty(value = "Registered Owner?", example = "false")
	public Boolean registeredOwner;

	@ApiModelProperty(value = "Vehicle code type", example = "PPA")
	public String vehTypeCd;

	@ApiModelProperty(value = "Garaging address", dataType = "com.eisgroup.aaa.policy.services.dto.Address")
	public Address garagingAddress;

	@ApiModelProperty(value = "Ownership info", dataType = "com.eisgroup.aaa.policy.services.dto.VehicleOwnership")
	public VehicleOwnership vehicleOwnership;

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