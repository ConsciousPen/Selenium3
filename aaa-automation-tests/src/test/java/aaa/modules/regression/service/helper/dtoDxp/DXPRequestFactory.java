package aaa.modules.regression.service.helper.dtoDxp;

public class DXPRequestFactory {

	public static AddDriverRequest createAddDriverRequest(String firstName, String middleName, String lastName, String birthDate, String suffix) {
		AddDriverRequest addDriverRequest = new AddDriverRequest();
		addDriverRequest.firstName = firstName;
		addDriverRequest.lastName = middleName;
		addDriverRequest.lastName = lastName;
		addDriverRequest.birthDate = birthDate;
		addDriverRequest.suffix = suffix;
		return addDriverRequest;
	}

	public static UpdateDriverRequest createUpdateDriverRequest(String gender, String licenseNumber, Integer ageFirstLicensed, String stateLicensed, String relationToApplicantCd, String maritalStatusCd) {
		UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
		updateDriverRequest.gender = gender;
		updateDriverRequest.licenseNumber = licenseNumber;
		updateDriverRequest.ageFirstLicensed = ageFirstLicensed;
		updateDriverRequest.stateLicensed = stateLicensed;
		updateDriverRequest.relationToApplicantCd = relationToApplicantCd;
		updateDriverRequest.maritalStatusCd = maritalStatusCd;
		return updateDriverRequest;
	}
}
