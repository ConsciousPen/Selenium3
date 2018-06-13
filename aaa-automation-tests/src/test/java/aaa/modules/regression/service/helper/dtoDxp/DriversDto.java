package aaa.modules.regression.service.helper.dtoDxp;

import java.util.Comparator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ComparisonChain;
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

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "First Name", example = "John")
	public String firstName;

	@ApiModelProperty(value = "Last Name", example = "Smith")
	public String lastName;

	@ApiModelProperty(value = "Middle Name", example = "Jacob")
	public String middleName;

	@ApiModelProperty(value = "Driver Type", example = "afr")
	public String driverType;

	@ApiModelProperty(value = "Suffix", example = "III")
	public String suffix;

	@ApiModelProperty(value = "Type of relations to the first name insured", example = "IN")
	public String namedInsuredType;

	@ApiModelProperty(value = "Relation to the first name insured type", example = "SP")
	public String relationToApplicantCd;

	@ApiModelProperty(value = "Marital status code", example = "MSS")
	public String maritalStatusCd;

	public String driverStatus;

	@ApiModelProperty(value = "Date of birth", example = "2000-01-31")
	public String birthDate;

	//public String driverStatus;

	public static final Comparator<DriversDto> DRIVERS_COMPARATOR = (driver1, driver2) -> ComparisonChain.start()
			.compareTrueFirst(DRIVER_FIRST_NAME_INSURED.equals(driver1.namedInsuredType), DRIVER_FIRST_NAME_INSURED.equals(driver2.namedInsuredType))
			.compareTrueFirst(DRIVER_NAME_INSURED.equals(driver1.namedInsuredType), DRIVER_NAME_INSURED.equals(driver2.namedInsuredType))
			.compareTrueFirst(DRIVER_TYPE_AVAILABLE_FOR_RATING.equals(driver1.driverType), DRIVER_TYPE_AVAILABLE_FOR_RATING.equals(driver2.driverType))
			.compareTrueFirst(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING.equals(driver1.driverType), DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING.equals(driver2.driverType))
			.compareTrueFirst(DRIVER_TYPE_EXCLUDED.equals(driver1.driverType), DRIVER_TYPE_EXCLUDED.equals(driver2.driverType))
			.compare(driver1.oid, driver2.oid)
			.result();

}


