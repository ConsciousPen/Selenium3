package aaa.helpers.openl.model.home_ca.ho6;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class HomeCaHO6OpenLDwelling extends HomeCaOpenLDwelling {
	private Integer ageOfHome;
	private String burglarAlarmType;

	@RequiredField
	private String constructionType;
	private String fireAlarmType;

	@RequiredField
	private Boolean hasSprinklers;

	@RequiredField
	private Boolean isGatedCommunity;

	@RequiredField
	private Boolean isSecondaryHome;

	public Integer getAgeOfHome() {
		return ageOfHome;
	}

	public void setAgeOfHome(Integer ageOfHome) {
		this.ageOfHome = ageOfHome;
	}

	public String getBurglarAlarmType() {
		return burglarAlarmType;
	}

	public void setBurglarAlarmType(String burglarAlarmType) {
		this.burglarAlarmType = burglarAlarmType;
	}

	public String getConstructionType() {
		return constructionType;
	}

	public void setConstructionType(String constructionType) {
		this.constructionType = constructionType;
	}

	public String getFireAlarmType() {
		return fireAlarmType;
	}

	public void setFireAlarmType(String fireAlarmType) {
		this.fireAlarmType = fireAlarmType;
	}

	public Boolean getHasSprinklers() {
		return hasSprinklers;
	}

	public void setHasSprinklers(Boolean hasSprinklers) {
		this.hasSprinklers = hasSprinklers;
	}

	public Boolean getGatedCommunity() {
		return isGatedCommunity;
	}

	public void setGatedCommunity(Boolean gatedCommunity) {
		isGatedCommunity = gatedCommunity;
	}

	public Boolean getSecondaryHome() {
		return isSecondaryHome;
	}

	public void setSecondaryHome(Boolean secondaryHome) {
		isSecondaryHome = secondaryHome;
	}
}
