package aaa.helpers.openl.model.home_ca.ho3;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class HomeCaHO3OpenLDwelling extends HomeCaOpenLDwelling {
	private Integer ageOfHome;
	private String burglarAlarmType;
	private String constructionType;
	private Integer firelineScore;
	private Integer numOfFamilies;

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

	public Integer getFirelineScore() {
		return firelineScore;
	}

	public void setFirelineScore(Integer firelineScore) {
		this.firelineScore = firelineScore;
	}

	public Integer getNumOfFamilies() {
		return numOfFamilies;
	}

	public void setNumOfFamilies(Integer numOfFamilies) {
		this.numOfFamilies = numOfFamilies;
	}
}
