package aaa.helpers.openl.model.home_ca.dp3;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class HomeCaDP3OpenLDwelling extends HomeCaOpenLDwelling {
	private Integer ageOfHome;
	private Boolean brushFireZone;
	private String burglarAlarmType;
	private String constructionType;
	private String fireAlarmType;
	private Integer firelineScore;
	private Boolean hasSprinklers;
	private Boolean hasWoodStove;
	private Boolean isGatedCommunity;
	private Integer numOfFamilies;
	private Integer numOfLivestock;
	private String roofType;
	private String swimmingPoolType;
	private Integer yearsSinceRenovation;

	public Integer getAgeOfHome() {
		return ageOfHome;
	}

	public void setAgeOfHome(Integer ageOfHome) {
		this.ageOfHome = ageOfHome;
	}

	public Boolean getBrushFireZone() {
		return brushFireZone;
	}

	public void setBrushFireZone(Boolean brushFireZone) {
		this.brushFireZone = brushFireZone;
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

	public Integer getFirelineScore() {
		return firelineScore;
	}

	public void setFirelineScore(Integer firelineScore) {
		this.firelineScore = firelineScore;
	}

	public Boolean getHasSprinklers() {
		return hasSprinklers;
	}

	public void setHasSprinklers(Boolean hasSprinklers) {
		this.hasSprinklers = hasSprinklers;
	}

	public Boolean getHasWoodStove() {
		return hasWoodStove;
	}

	public void setHasWoodStove(Boolean hasWoodStove) {
		this.hasWoodStove = hasWoodStove;
	}

	public Boolean getGatedCommunity() {
		return isGatedCommunity;
	}

	public void setGatedCommunity(Boolean gatedCommunity) {
		isGatedCommunity = gatedCommunity;
	}

	public Integer getNumOfFamilies() {
		return numOfFamilies;
	}

	public void setNumOfFamilies(Integer numOfFamilies) {
		this.numOfFamilies = numOfFamilies;
	}

	public Integer getNumOfLivestock() {
		return numOfLivestock;
	}

	public void setNumOfLivestock(Integer numOfLivestock) {
		this.numOfLivestock = numOfLivestock;
	}

	public String getRoofType() {
		return roofType;
	}

	public void setRoofType(String roofType) {
		this.roofType = roofType;
	}

	public String getSwimmingPoolType() {
		return swimmingPoolType;
	}

	public void setSwimmingPoolType(String swimmingPoolType) {
		this.swimmingPoolType = swimmingPoolType;
	}

	public Integer getYearsSinceRenovation() {
		return yearsSinceRenovation;
	}

	public void setYearsSinceRenovation(Integer yearsSinceRenovation) {
		this.yearsSinceRenovation = yearsSinceRenovation;
	}
}
