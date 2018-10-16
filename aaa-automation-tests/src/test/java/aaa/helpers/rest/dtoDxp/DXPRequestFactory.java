package aaa.helpers.rest.dtoDxp;

import java.util.Collections;
import java.util.List;

public class DXPRequestFactory {

	public static AddDriverRequest createAddDriverRequest(String firstName, String middleName, String lastName, String birthDate, String suffix) {
		AddDriverRequest addDriverRequest = new AddDriverRequest();
		addDriverRequest.firstName = firstName;
		addDriverRequest.middleName = middleName;
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

	public static UpdateDriverRequest createUpdateDriverRequest(Boolean specificDisabilityInd, Boolean totalDisabilityInd) {
		UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
		updateDriverRequest.specificDisabilityInd = specificDisabilityInd;
		updateDriverRequest.totalDisabilityInd = totalDisabilityInd;
		return updateDriverRequest;
	}

	public static RemoveDriverRequest createRemoveDriverRequest(String removalReasonCode) {
		RemoveDriverRequest removeDriverRequest = new RemoveDriverRequest(); //"RD1001" and "RD1002" for happy path
		removeDriverRequest.removalReasonCode = removalReasonCode;
		return removeDriverRequest;
	}

	public static ReplaceVehicleRequest createReplaceVehicleRequest(String replacedVehicleVin, String purchaseDate, boolean keepAssignments, boolean keepCoverages) {
		ReplaceVehicleRequest replaceVehicleRequest = new ReplaceVehicleRequest();
		replaceVehicleRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleRequest.vehicleToBeAdded.purchaseDate = purchaseDate;
		replaceVehicleRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleVin;
		replaceVehicleRequest.keepAssignments = keepAssignments;
		replaceVehicleRequest.keepCoverages = keepCoverages;
		return replaceVehicleRequest;
	}

	public static Vehicle createAddVehicleRequest(String vin, String purchaseDate) {
		Vehicle addVehicleRequest= new Vehicle();
		addVehicleRequest.purchaseDate = purchaseDate;
		addVehicleRequest.vehIdentificationNo = vin;
		return addVehicleRequest;
	}

	public static UpdateCoverageRequest createUpdateCoverageRequest(String coverageCd, String limit) {
		UpdateCoverageRequest updateCoverageRequest = new UpdateCoverageRequest();
		updateCoverageRequest.coverageCd = coverageCd;
		updateCoverageRequest.limit = limit;
		return updateCoverageRequest;
	}

	public static UpdateCoverageRequest createUpdateCoverageRequest(String coverageCd, String limit, String driverOid) {
		UpdateCoverageRequest updateCoverageRequest = new UpdateCoverageRequest();
		updateCoverageRequest.coverageCd = coverageCd;
		updateCoverageRequest.limit = limit;
		updateCoverageRequest.driverOids = Collections.singletonList(driverOid);
		return updateCoverageRequest;
	}


	public static UpdateCoverageRequest createUpdateCoverageRequest(String coverageCd, String limit, List<String> driverOids) {
		UpdateCoverageRequest updateCoverageRequest = new UpdateCoverageRequest();
		updateCoverageRequest.coverageCd = coverageCd;
		updateCoverageRequest.limit = limit;
		updateCoverageRequest.driverOids = driverOids;
		return updateCoverageRequest;
	}
}
