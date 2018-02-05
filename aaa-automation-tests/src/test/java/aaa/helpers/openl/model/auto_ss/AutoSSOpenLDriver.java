package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.model.OpenLDriver;

public class AutoSSOpenLDriver extends OpenLDriver {
	private String name;
	private Boolean hasSR22;
	private Boolean hasFR44; // VA specific
	private Integer driverAge;
	private Integer ageBeforeEndorsement;
	private String defensiveDrivingCourse;
	private Boolean cleanDriver; // MD specific
	private Boolean distantStudent;
	private Boolean exposure;
	private Boolean foreignLicense;
	private Boolean outOfStateLicenseSurcharge;
	private Boolean smartDriver;
	private Boolean unverifiableDrivingRecord;
	private Boolean occasionalOperator; // DC specific ?
	private String vehicleAssignedId; // DC specific ?
	private Boolean hasTravelink; // DE specific ?
	private Boolean isExcludedDriver; // NJ specific ?

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDriverAge() {
		return driverAge;
	}

	public void setDriverAge(Integer driverAge) {
		this.driverAge = driverAge;
	}

	public Integer getAgeBeforeEndorsement() {
		return ageBeforeEndorsement;
	}

	public void setAgeBeforeEndorsement(Integer ageBeforeEndorsement) {
		this.ageBeforeEndorsement = ageBeforeEndorsement;
	}

	public String getDefensiveDrivingCourse() {
		return defensiveDrivingCourse;
	}

	public void setDefensiveDrivingCourse(String defensiveDrivingCourse) {
		this.defensiveDrivingCourse = defensiveDrivingCourse;
	}

	public Boolean getOccasionalOperator() {
		return occasionalOperator;
	}

	public void setOccasionalOperator(Boolean occasionalOperator) {
		this.occasionalOperator = occasionalOperator;
	}

	public String getVehicleAssignedId() {
		return vehicleAssignedId;
	}

	public void setVehicleAssignedId(String vehicleAssignedId) {
		this.vehicleAssignedId = vehicleAssignedId;
	}

	public void setDistantStudent(Boolean distantStudent) {
		this.distantStudent = distantStudent;
	}

	public void setForeignLicense(Boolean foreignLicense) {
		this.foreignLicense = foreignLicense;
	}

	public void setUnverifiableDrivingRecord(Boolean unverifiableDrivingRecord) {
		this.unverifiableDrivingRecord = unverifiableDrivingRecord;
	}

	public void setOutOfStateLicenseSurcharge(Boolean outOfStateLicenseSurcharge) {
		this.outOfStateLicenseSurcharge = outOfStateLicenseSurcharge;
	}

	public void setExposure(Boolean exposure) {
		this.exposure = exposure;
	}

	public void setSmartDriver(Boolean smartDriver) {
		this.smartDriver = smartDriver;
	}

	public void setCleanDriver(Boolean cleanDriver) {
		this.cleanDriver = cleanDriver;
	}

	public void setHasSR22(Boolean hasSR22) {
		this.hasSR22 = hasSR22;
	}

	public void setHasFR44(Boolean hasFR44) {
		this.hasFR44 = hasFR44;
	}

	public void setHasTravelink(Boolean hasTravelink) {
		this.hasTravelink = hasTravelink;
	}

	public void setExcludedDriver(Boolean isExcludedDriver) {
		this.isExcludedDriver = isExcludedDriver;
	}

	@Override
	public String toString() {
		return "AutoSSOpenLDriver{" +
				"name='" + name + '\'' +
				", hasSR22=" + hasSR22 +
				", hasFR44=" + hasFR44 +
				", driverAge=" + driverAge +
				", ageBeforeEndorsement=" + ageBeforeEndorsement +
				", defensiveDrivingCourse='" + defensiveDrivingCourse + '\'' +
				", cleanDriver=" + cleanDriver +
				", distantStudent=" + distantStudent +
				", exposure=" + exposure +
				", foreignLicense=" + foreignLicense +
				", outOfStateLicenseSurcharge=" + outOfStateLicenseSurcharge +
				", smartDriver=" + smartDriver +
				", unverifiableDrivingRecord=" + unverifiableDrivingRecord +
				", occasionalOperator=" + occasionalOperator +
				", vehicleAssignedId='" + vehicleAssignedId + '\'' +
				", hasTravelink=" + hasTravelink +
				", isExcludedDriver=" + isExcludedDriver +
				", number=" + number +
				", id='" + id + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatus='" + maritalStatus + '\'' +
				", tyde=" + tyde +
				", dsr=" + dsr +
				", goodStudent=" + goodStudent +
				'}';
	}

	public Boolean isDistantStudent() {
		return distantStudent;
	}

	public Boolean isForeignLicense() {
		return foreignLicense;
	}

	public Boolean isUnverifiableDrivingRecord() {
		return unverifiableDrivingRecord;
	}

	public Boolean isOutOfStateLicenseSurcharge() {
		return outOfStateLicenseSurcharge;
	}

	public Boolean isExposure() {
		return exposure;
	}

	public Boolean isSmartDriver() {
		return smartDriver;
	}

	public Boolean isCleanDriver() {
		return cleanDriver;
	}

	public Boolean hasSR22() {
		return hasSR22;
	}

	public Boolean hasFR44() {
		return hasFR44;
	}

	public Boolean hasTravelink() {
		return hasTravelink;
	}

	public Boolean isExcludedDriver() {
		return isExcludedDriver;
	}
}
