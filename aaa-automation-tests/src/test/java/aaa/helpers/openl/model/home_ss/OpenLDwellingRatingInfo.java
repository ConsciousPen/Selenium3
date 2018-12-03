package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_RATING_INFO_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_RATING_INFO_HEADER_ROW_NUMBER)
public class OpenLDwellingRatingInfo {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	@ExcelColumnElement(ignoreCase = true)
	@RequiredField
	private Boolean isSecondaryHome;

	private Double dwellingLossFreq;
	private Integer familyUnits;

	@RequiredField
	private Integer homeAge;
	private Double noOfFloors;
	private String protectionClass;
	private Integer roofAge;
	private String roofType;

	@RequiredField
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

	public Double getDwellingLossFreq() {
		return dwellingLossFreq;
	}

	public void setDwellingLossFreq(Double dwellingLossFreq) {
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

	public Double getNoOfFloors() {
		return noOfFloors;
	}

	public void setNoOfFloors(Double noOfFloors) {
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
}
