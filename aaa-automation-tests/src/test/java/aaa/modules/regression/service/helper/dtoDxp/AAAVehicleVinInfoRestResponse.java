package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Retrieve vehicle VIN information from PAS VIN tables.
 * To be used by any consumers e.g. self-service portal.
 * 
 * @author Arthur V.
 * @since Sprint 11 - Scorpions
 */
@ApiModel(description = "Vehicle VIN Information")
public class AAAVehicleVinInfoRestResponse {
	
	@ApiModelProperty(value = "Vehicle VIN Number", required = true)
	private String vin;
	
	@ApiModelProperty
	private Integer year;
	
	@ApiModelProperty
	private String make;
	
	@ApiModelProperty
	private String modelText;
	
	@ApiModelProperty
	private String seriesText;
	
	@ApiModelProperty
	private String bodyStyleCd;
	
	@ApiModelProperty
	private String restraintsCode;
	
	@ApiModelProperty
	private String restraintsCodeText;
	
	@ApiModelProperty
	private String antiLockCodeText;
	
	@ApiModelProperty
	private String antiTheftCode;
	
	@ApiModelProperty
	private String antiTheftCodeText;
	
	@ApiModelProperty
	private Boolean altFuel;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String entryDate;

	public AAAVehicleVinInfoRestResponse() {
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModelText() {
		return modelText;
	}

	public void setModelText(String modelText) {
		this.modelText = modelText;
	}

	public String getSeriesText() {
		return seriesText;
	}

	public void setSeriesText(String seriesText) {
		this.seriesText = seriesText;
	}

	public String getBodyStyleCd() {
		return bodyStyleCd;
	}

	public void setBodyStyleCd(String bodyStyleCd) {
		this.bodyStyleCd = bodyStyleCd;
	}

	public String getRestraintsCode() {
		return restraintsCode;
	}

	public void setRestraintsCode(String restraintsCode) {
		this.restraintsCode = restraintsCode;
	}

	public String getRestraintsCodeText() {
		return restraintsCodeText;
	}

	public void setRestraintsCodeText(String restraintsCodeText) {
		this.restraintsCodeText = restraintsCodeText;
	}

	public String getAntiLockCodeText() {
		return antiLockCodeText;
	}

	public void setAntiLockCodeText(String antiLockCodeText) {
		this.antiLockCodeText = antiLockCodeText;
	}

	public String getAntiTheftCode() {
		return antiTheftCode;
	}

	public void setAntiTheftCode(String antiTheftCode) {
		this.antiTheftCode = antiTheftCode;
	}

	public String getAntiTheftCodeText() {
		return antiTheftCodeText;
	}

	public void setAntiTheftCodeText(String antiTheftCodeText) {
		this.antiTheftCodeText = antiTheftCodeText;
	}

	public Boolean getAltFuel() {
		return altFuel;
	}

	public void setAltFuel(Boolean altFuel) {
		this.altFuel = altFuel;
	}

	@JsonIgnore
	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

}
