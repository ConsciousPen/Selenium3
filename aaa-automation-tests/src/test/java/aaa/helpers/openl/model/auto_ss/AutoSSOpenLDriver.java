package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.annotation.MatchingField;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@ExcelTableElement(containsSheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
public class AutoSSOpenLDriver extends OpenLDriver implements Comparable<AutoSSOpenLDriver> {
	@MatchingField
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
	private Boolean occasionalOperator; // NY specific
	private String vehicleAssignedId; // DC specific ?
	private Boolean hasTravelink; // DE specific
	private Boolean isExcludedDriver; // NJ specific

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

	@Override
	public int compareTo(AutoSSOpenLDriver otherDriver) {
		return this.getName().compareTo(otherDriver.getName());
	}
}
