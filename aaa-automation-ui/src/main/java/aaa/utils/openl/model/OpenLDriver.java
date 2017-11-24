package aaa.utils.openl.model;

public class OpenLDriver {
	private int number;
	private String id;
	private String name;
	private String gender;
	private String maritalStatus;
	private int tyde;
	private int dsr;
	private boolean isGoodStudent;
	private boolean hasSR22;
	private int driverAge;
	private int ageBeforeEndorsement;
	private boolean isSmartDriver;
	private boolean isDistantStudent;
	private String defensiveDrivingCourse;
	private boolean isForeignLicense;
	private boolean isUnverifiableDrivingRecord;
	private boolean isOutOfStateLicenseSurcharge;
	private boolean isExposure;
	private boolean isCleanDriver;

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

	public boolean isGoodStudent() {
		return isGoodStudent;
	}

	public void setGoodStudent(boolean isGoodStudent) {
		this.isGoodStudent = isGoodStudent;
	}

	public boolean hasSR22() {
		return hasSR22;
	}

	public void setHasSR22(boolean hasSR22) {
		this.hasSR22 = hasSR22;
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

	public boolean isSmartDriver() {
		return isSmartDriver;
	}

	public void setSmartDriver(boolean isSmartDriver) {
		this.isSmartDriver = isSmartDriver;
	}

	public boolean isDistantStudent() {
		return isDistantStudent;
	}

	public void setDistantStudent(boolean isDistantStudent) {
		this.isDistantStudent = isDistantStudent;
	}

	public String getDefensiveDrivingCourse() {
		return defensiveDrivingCourse;
	}

	public void setDefensiveDrivingCourse(String defensiveDrivingCourse) {
		this.defensiveDrivingCourse = defensiveDrivingCourse;
	}

	public boolean isForeignLicense() {
		return isForeignLicense;
	}

	public void setForeignLicense(boolean isForeignLicense) {
		this.isForeignLicense = isForeignLicense;
	}

	public boolean isUnverifiableDrivingRecord() {
		return isUnverifiableDrivingRecord;
	}

	public void setUnverifiableDrivingRecord(boolean isUnverifiableDrivingRecord) {
		this.isUnverifiableDrivingRecord = isUnverifiableDrivingRecord;
	}

	public boolean isOutOfStateLicenseSurcharge() {
		return isOutOfStateLicenseSurcharge;
	}

	public void setOutOfStateLicenseSurcharge(boolean isOutOfStateLicenseSurcharge) {
		this.isOutOfStateLicenseSurcharge = isOutOfStateLicenseSurcharge;
	}

	public boolean isExposure() {
		return isExposure;
	}

	public void setExposure(boolean isExposure) {
		this.isExposure = isExposure;
	}

	public boolean isCleanDriver() {
		return isCleanDriver;
	}

	public void setCleanDriver(boolean isCleanDriver) {
		this.isCleanDriver = isCleanDriver;
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
				", isGoodStudent=" + isGoodStudent +
				", hasSR22=" + hasSR22 +
				", driverAge=" + driverAge +
				", ageBeforeEndorsement=" + ageBeforeEndorsement +
				", isSmartDriver=" + isSmartDriver +
				", isDistantStudent=" + isDistantStudent +
				", defensiveDrivingCourse='" + defensiveDrivingCourse + '\'' +
				", isForeignLicense=" + isForeignLicense +
				", isUnverifiableDrivingRecord=" + isUnverifiableDrivingRecord +
				", isOutOfStateLicenseSurcharge=" + isOutOfStateLicenseSurcharge +
				", isExposure=" + isExposure +
				", isCleanDriver=" + isCleanDriver +
				'}';
	}
}
