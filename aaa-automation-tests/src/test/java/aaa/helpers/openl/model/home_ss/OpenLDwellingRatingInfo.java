package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLDwellingRatingInfo {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	@ExcelTableColumnElement(name = "IsSecondaryHome")
	private Boolean isSecondaryHome;

	private Integer dwellingLossFreq;
	private Integer familyUnits;
	private Integer homeAge;
	private Integer noOfFloors;
	private String protectionClass;
	private Integer roofAge;
	private String roofType;
	private Integer yearBuilt;
	private String hailResistiveCode;
	private String laundryLocation;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Boolean getSecondaryHome() {
		return isSecondaryHome;
	}

	public void setSecondaryHome(Boolean secondaryHome) {
		isSecondaryHome = secondaryHome;
	}

	public Integer getDwellingLossFreq() {
		return dwellingLossFreq;
	}

	public void setDwellingLossFreq(Integer dwellingLossFreq) {
		this.dwellingLossFreq = dwellingLossFreq;
	}

	public Integer getFamilyUnits() {
		return familyUnits;
	}

	public void setFamilyUnits(Integer familyUnits) {
		this.familyUnits = familyUnits;
	}

	public Integer getHomeAge() {
		return homeAge;
	}

	public void setHomeAge(Integer homeAge) {
		this.homeAge = homeAge;
	}

	public Integer getNoOfFloors() {
		return noOfFloors;
	}

	public void setNoOfFloors(Integer noOfFloors) {
		this.noOfFloors = noOfFloors;
	}

	public String getProtectionClass() {
		return protectionClass;
	}

	public void setProtectionClass(String protectionClass) {
		this.protectionClass = protectionClass;
	}

	public Integer getRoofAge() {
		return roofAge;
	}

	public void setRoofAge(Integer roofAge) {
		this.roofAge = roofAge;
	}

	public String getRoofType() {
		return roofType;
	}

	public void setRoofType(String roofType) {
		this.roofType = roofType;
	}

	public Integer getYearBuilt() {
		return yearBuilt;
	}

	public void setYearBuilt(Integer yearBuilt) {
		this.yearBuilt = yearBuilt;
	}

	public String getHailResistiveCode() {
		return hailResistiveCode;
	}

	public void setHailResistiveCode(String hailResistiveCode) {
		this.hailResistiveCode = hailResistiveCode;
	}

	public String getLaundryLocation() {
		return laundryLocation;
	}

	public void setLaundryLocation(String laundryLocation) {
		this.laundryLocation = laundryLocation;
	}

	@Override
	public String toString() {
		return "OpenLDwellingRatingInfo{" +
				"number=" + number +
				", isSecondaryHome=" + isSecondaryHome +
				", dwellingLossFreq=" + dwellingLossFreq +
				", familyUnits=" + familyUnits +
				", homeAge=" + homeAge +
				", noOfFloors=" + noOfFloors +
				", protectionClass='" + protectionClass + '\'' +
				", roofAge=" + roofAge +
				", roofType='" + roofType + '\'' +
				", yearBuilt=" + yearBuilt +
				", hailResistiveCode='" + hailResistiveCode + '\'' +
				", laundryLocation='" + laundryLocation + '\'' +
				'}';
	}
}
