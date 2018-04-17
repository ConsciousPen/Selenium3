package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Comparator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ComparisonChain;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Vehicle Information")
public class Vehicle implements RestBodyRequest {

	@ApiModelProperty(value = "Model year", example = "2002")
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

	@ApiModelProperty(value = "Vehicle status", example = "Active")
	public String vehicleStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
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

	public String garagingAddressPostalCode;

	public String addressLine1;

	public String addressLine2;

	public String city;

	public String stateProvCd;

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