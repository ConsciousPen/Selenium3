package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DrivingLicenseDTO  {
	public String licensePermitNumber;
	public String stateProvCd;
}