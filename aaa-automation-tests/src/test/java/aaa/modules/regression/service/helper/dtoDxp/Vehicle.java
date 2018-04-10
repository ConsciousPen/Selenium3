package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import aaa.modules.regression.service.helper.RestBodyRequest;
import com.google.common.collect.ComparisonChain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.Assert;

import java.util.Comparator;

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

	public String getModelYear() {
		return modelYear;
	}

	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBodyStyle() {
		return bodyStyle;
	}

	public void setBodyStyle(String bodyStyle) {
		this.bodyStyle = bodyStyle;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public String getVehTypeCd() { return vehTypeCd; }

	public void setVehTypeCd(String vehTypeCd) {this.vehTypeCd = vehTypeCd; }

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