package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import com.google.common.collect.ComparisonChain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Comparator;
import java.util.List;

@ApiModel(description = "Vehicle Information")
public class Vehicle implements RestBodyRequest {

	@ApiModelProperty(value = "Model year", example = "2003")
	private static final String VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO = "PPA";
	private static final String VEHICLE_STATUS_PENDING = "pending";
	private static final String VEHICLE_STATUS_ACTIVE = "active";
	private static final String VEHICLE_STATUS_PENDING_REMOVAL = "pendingRemoval";
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

	@ApiModelProperty(value = "OID of vehicle replaced by this vehicle", example = "vKceby6oeNj4Hcu8rUJB7Q")
	public String vehicleReplacedBy;

	@ApiModelProperty(value = "Available Actions for the vehicle")
	public List<String> availableActions;

	@ApiModelProperty(value = "List of vehicle related validation errors")
	public List<ValidationError> validations;



	public static final Comparator<Vehicle> ACTIVE_POLICY_COMPARATOR = (vehicle1, vehicle2) -> ComparisonChain.start()
			.compareTrueFirst(VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(vehicle1.vehTypeCd),
					VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(vehicle2.vehTypeCd))
			.compareTrueFirst(VEHICLE_STATUS_PENDING.equals(vehicle1.vehicleStatus),
					VEHICLE_STATUS_PENDING.equals(vehicle2.vehicleStatus))
			.compareTrueFirst(VEHICLE_STATUS_ACTIVE.equals(vehicle1.vehicleStatus),
					VEHICLE_STATUS_ACTIVE.equals(vehicle2.vehicleStatus))
			.compare(vehicle1.oid, vehicle2.oid)
			.result();

	public static final Comparator<Vehicle> PENDING_ENDORSEMENT_COMPARATOR = (vehicle1, vehicle2) -> ComparisonChain.start()
			.compareTrueFirst(VEHICLE_STATUS_PENDING_REMOVAL.equals(vehicle1.vehicleStatus),
					VEHICLE_STATUS_PENDING_REMOVAL.equals(vehicle2.vehicleStatus))
			.compareTrueFirst(VEHICLE_STATUS_PENDING.equals(vehicle1.vehicleStatus),
					VEHICLE_STATUS_PENDING.equals(vehicle2.vehicleStatus))
			.compareTrueFirst(VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(vehicle1.vehTypeCd),
					VEHICLE_TYPE_PRIVATE_PASSENGER_AUTO.equals(vehicle2.vehTypeCd))
			.compare(vehicle1.oid, vehicle2.oid)
			.result();

}