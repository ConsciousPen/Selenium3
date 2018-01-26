package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLDriver {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;
	private String id;
	private String name;
	private String gender;
	private String maritalStatus;
	private Integer tyde;
	private Integer dsr;
	private Boolean hasSR22;
	private Boolean hasFR44; // VA specific
	private Integer driverAge;
	private Integer ageBeforeEndorsement;
	private String defensiveDrivingCourse;
	private Boolean cleanDriver; // MD specific
	private Boolean distantStudent;
	private Boolean exposure;
	private Boolean foreignLicense;
	private Boolean goodStudent;
	private Boolean outOfStateLicenseSurcharge;
	private Boolean smartDriver;
	private Boolean unverifiableDrivingRecord;
	private Boolean occasionalOperator; // DC specific ?
	private String vehicleAssignedId; // DC specific ?
	private Boolean hasTravelink; // DE specific ?
	private Boolean isExcludedDriver; // NJ specific ?

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Integer getTyde() {
		return tyde;
	}

	public void setTyde(Integer tyde) {
		this.tyde = tyde;
	}

	public Integer getDsr() {
		return dsr;
	}

	public void setDsr(Integer dsr) {
		this.dsr = dsr;
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

	public Boolean isDistantStudent() {
		return distantStudent;
	}

	public void setDistantStudent(Boolean distantStudent) {
		this.distantStudent = distantStudent;
	}

	public String getDefensiveDrivingCourse() {
		return defensiveDrivingCourse;
	}

	public void setDefensiveDrivingCourse(String defensiveDrivingCourse) {
		this.defensiveDrivingCourse = defensiveDrivingCourse;
	}

	public Boolean isForeignLicense() {
		return foreignLicense;
	}

	public void setForeignLicense(Boolean foreignLicense) {
		this.foreignLicense = foreignLicense;
	}

	public Boolean isUnverifiableDrivingRecord() {
		return unverifiableDrivingRecord;
	}

	public void setUnverifiableDrivingRecord(Boolean unverifiableDrivingRecord) {
		this.unverifiableDrivingRecord = unverifiableDrivingRecord;
	}

	public Boolean isOutOfStateLicenseSurcharge() {
		return outOfStateLicenseSurcharge;
	}

	public void setOutOfStateLicenseSurcharge(Boolean outOfStateLicenseSurcharge) {
		this.outOfStateLicenseSurcharge = outOfStateLicenseSurcharge;
	}

	public Boolean isExposure() {
		return exposure;
	}

	public void setExposure(Boolean exposure) {
		this.exposure = exposure;
	}

	public Boolean isGoodStudent() {
		return goodStudent;
	}

	public void setGoodStudent(Boolean goodStudent) {
		this.goodStudent = goodStudent;
	}

	public Boolean isSmartDriver() {
		return smartDriver;
	}

	public void setSmartDriver(Boolean smartDriver) {
		this.smartDriver = smartDriver;
	}

	public Boolean isCleanDriver() {
		return cleanDriver;
	}

	public void setCleanDriver(Boolean cleanDriver) {
		this.cleanDriver = cleanDriver;
	}

	public Boolean hasSR22() {
		return hasSR22;
	}

	public void setHasSR22(Boolean hasSR22) {
		this.hasSR22 = hasSR22;
	}

	public Boolean hasFR44() {
		return hasFR44;
	}

	public void setHasFR44(Boolean hasFR44) {
		this.hasFR44 = hasFR44;
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

	public Boolean hasTravelink() {
		return hasTravelink;
	}

	public void setHasTravelink(Boolean hasTravelink) {
		this.hasTravelink = hasTravelink;
	}

	public Boolean isExcludedDriver() {
		return isExcludedDriver;
	}

	public void setExcludedDriver(Boolean isExcludedDriver) {
		this.isExcludedDriver = isExcludedDriver;
	}

	@Override
	public String toString() {
		return "OpenLDriver{" +
				"number=" + number +
				", id='" + id + '\'' +
				", name='" + name + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatus='" + maritalStatus + '\'' +
				", tyde=" + tyde +
				", dsr=" + dsr +
				", hasSR22=" + hasSR22 +
				", driverAge=" + driverAge +
				", ageBeforeEndorsement=" + ageBeforeEndorsement +
				", defensiveDrivingCourse='" + defensiveDrivingCourse + '\'' +
				", cleanDriver=" + cleanDriver +
				", distantStudent=" + distantStudent +
				", exposure=" + exposure +
				", foreignLicense=" + foreignLicense +
				", goodStudent=" + goodStudent +
				", outOfStateLicenseSurcharge=" + outOfStateLicenseSurcharge +
				", smartDriver=" + smartDriver +
				", unverifiableDrivingRecord=" + unverifiableDrivingRecord +
				", occasionalOperator=" + occasionalOperator +
				", vehicleAssignedId=" + vehicleAssignedId +
				", hasTravelink=" + hasTravelink +
				", isExcludedDriver=" + isExcludedDriver +
				'}';
	}
}
