package aaa.modules.regression.service.helper.dtoDxp;

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
	public String vin;
	
	@ApiModelProperty
	public Integer year;
	
	@ApiModelProperty
	public String make;
	
	@ApiModelProperty
	public String modelText;
	
	@ApiModelProperty
	public String seriesText;
	
	@ApiModelProperty
	public String bodyStyleCd;
	
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



}
