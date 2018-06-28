package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Comparator;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import aaa.modules.regression.service.helper.DrivingLicense;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Driver Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DriversDto implements RestBodyRequest {

	private static final String DRIVER_TYPE_AVAILABLE_FOR_RATING = "afr";
	private static final String DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING = "nafr";
	private static final String DRIVER_TYPE_EXCLUDED = "excl";
	private static final String DRIVER_FIRST_NAME_INSURED = "FNI";
	private static final String DRIVER_NAME_INSURED = "NI";


	@JsonProperty("firstName")
	public String firstName;

	@JsonProperty("lastName")
	public String lastName;

	@JsonProperty("middleName")
	public String middleName;

	@JsonProperty("suffix")
	public String suffix;

	@JsonProperty("driverTypeCd")
	public String driverTypeCd;

	@JsonProperty("insuredOid")
	public String insuredOid;

	@JsonProperty("driverRelToApplicantCd")
	public String driverRelToApplicantCd;

	@JsonProperty("aaaMaritalStatusCd")
	public String aaaMaritalStatusCd;

	@JsonProperty("driverBaseDt")
	public Date driverBaseDt;

	@JsonProperty("driverStatus")
	public String driverStatus;

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "Driver Type", example = "afr")
	public String driverType;

	@ApiModelProperty(value = "Type of relations to the first name insured", example = "IN")
	public String namedInsuredType;

	@ApiModelProperty(value = "Relation to the first name insured type", example = "SP")
	public String relationToApplicantCd;

	@ApiModelProperty(value = "Marital status code", example = "MSS")
	public String maritalStatusCd;

	@JsonProperty("financialResponsibilityInd")
	public Boolean financialResponsibilityInd;

	@JsonProperty("birthDate")
	public Date birthDate;

	@ApiModelProperty(value = "Gender", example = "male")
	public String gender;

	@ApiModelProperty(value = "Age First Licensed", example = "18")
	public Integer ageFirstLicensed;

	@ApiModelProperty(value = "Driving license info", dataType = "com.eisgroup.aaa.policy.services.dto.DrivingLicense")
	public DrivingLicense drivingLicense;

	@JsonProperty("occupationTypeCd")
	public String occupationTypeCd;

	@JsonProperty("genderCd")
	public String genderCd;

	@JsonProperty("firstLicenseAge")
	public Integer firstLicenseAge;

	public static final Comparator<DriversDto> DRIVERS_COMPARATOR = (driver1, driver2) -> ComparisonChain.start()
			.compareTrueFirst(DRIVER_FIRST_NAME_INSURED.equals(driver1.namedInsuredType), DRIVER_FIRST_NAME_INSURED.equals(driver2.namedInsuredType))
			.compareTrueFirst(DRIVER_NAME_INSURED.equals(driver1.namedInsuredType), DRIVER_NAME_INSURED.equals(driver2.namedInsuredType))
			.compareTrueFirst(DRIVER_TYPE_AVAILABLE_FOR_RATING.equals(driver1.driverType), DRIVER_TYPE_AVAILABLE_FOR_RATING.equals(driver2.driverType))
			.compareTrueFirst(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING.equals(driver1.driverType), DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING.equals(driver2.driverType))
			.compareTrueFirst(DRIVER_TYPE_EXCLUDED.equals(driver1.driverType), DRIVER_TYPE_EXCLUDED.equals(driver2.driverType))
			.compare(driver1.oid, driver2.oid)
			.result();

}


