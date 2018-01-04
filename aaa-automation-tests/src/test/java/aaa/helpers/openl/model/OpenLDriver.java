package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLDriver {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private int number;
	private String id;
	private String name;
	private String gender;
	private String maritalStatus;
	private int tyde;
	private int dsr;
	private boolean hasSR22;
	private int driverAge;
	private int ageBeforeEndorsement;
	private String defensiveDrivingCourse;
	private boolean cleanDriver;
	private boolean distantStudent;
	private boolean exposure;
	private boolean foreignLicense;
	private boolean goodStudent;
	private boolean outOfStateLicenseSurcharge;
	private boolean smartDriver;
	private boolean unverifiableDrivingRecord;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
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

	public int getTyde() {
		return tyde;
	}

	public void setTyde(int tyde) {
		this.tyde = tyde;
	}

	public int getDsr() {
		return dsr;
	}

	public void setDsr(int dsr) {
		this.dsr = dsr;
	}

	public int getDriverAge() {
		return driverAge;
	}

	public void setDriverAge(int driverAge) {
		this.driverAge = driverAge;
	}

	public int getAgeBeforeEndorsement() {
		return ageBeforeEndorsement;
	}

	public void setAgeBeforeEndorsement(int ageBeforeEndorsement) {
		this.ageBeforeEndorsement = ageBeforeEndorsement;
	}

	public boolean isDistantStudent() {
		return distantStudent;
	}

	public void setDistantStudent(boolean distantStudent) {
		this.distantStudent = distantStudent;
	}

	public String getDefensiveDrivingCourse() {
		return defensiveDrivingCourse;
	}

	public void setDefensiveDrivingCourse(String defensiveDrivingCourse) {
		this.defensiveDrivingCourse = defensiveDrivingCourse;
	}

	public boolean isForeignLicense() {
		return foreignLicense;
	}

	public void setForeignLicense(boolean foreignLicense) {
		this.foreignLicense = foreignLicense;
	}

	public boolean isUnverifiableDrivingRecord() {
		return unverifiableDrivingRecord;
	}

	public void setUnverifiableDrivingRecord(boolean unverifiableDrivingRecord) {
		this.unverifiableDrivingRecord = unverifiableDrivingRecord;
	}

	public boolean isOutOfStateLicenseSurcharge() {
		return outOfStateLicenseSurcharge;
	}

	public void setOutOfStateLicenseSurcharge(boolean outOfStateLicenseSurcharge) {
		this.outOfStateLicenseSurcharge = outOfStateLicenseSurcharge;
	}

	public boolean isExposure() {
		return exposure;
	}

	public void setExposure(boolean exposure) {
		this.exposure = exposure;
	}

	public boolean isGoodStudent() {
		return goodStudent;
	}

	public void setGoodStudent(boolean goodStudent) {
		this.goodStudent = goodStudent;
	}

	public boolean isSmartDriver() {
		return smartDriver;
	}

	public void setSmartDriver(boolean smartDriver) {
		this.smartDriver = smartDriver;
	}

	public boolean isCleanDriver() {
		return cleanDriver;
	}

	public void setCleanDriver(boolean cleanDriver) {
		this.cleanDriver = cleanDriver;
	}

	public void setHasSR22(boolean hasSR22) {
		this.hasSR22 = hasSR22;
	}

	public boolean hasSR22() {
		return hasSR22;
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
				'}';
	}
}
