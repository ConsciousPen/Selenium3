package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLConstructionInfo {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String constructionType;
	private Integer dogType;
	private Integer liveStkNo;
	private Integer numberOfTub;
	private String swimmingPoolType;
	private String trampoline;
	private Boolean woodStove;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getConstructionType() {
		return constructionType;
	}

	public void setConstructionType(String constructionType) {
		this.constructionType = constructionType;
	}

	public Integer getDogType() {
		return dogType;
	}

	public void setDogType(Integer dogType) {
		this.dogType = dogType;
	}

	public Integer getLiveStkNo() {
		return liveStkNo;
	}

	public void setLiveStkNo(Integer liveStkNo) {
		this.liveStkNo = liveStkNo;
	}

	public Integer getNumberOfTub() {
		return numberOfTub;
	}

	public void setNumberOfTub(Integer numberOfTub) {
		this.numberOfTub = numberOfTub;
	}

	public String getSwimmingPoolType() {
		return swimmingPoolType;
	}

	public void setSwimmingPoolType(String swimmingPoolType) {
		this.swimmingPoolType = swimmingPoolType;
	}

	public String getTrampoline() {
		return trampoline;
	}

	public void setTrampoline(String trampoline) {
		this.trampoline = trampoline;
	}

	public Boolean getWoodStove() {
		return woodStove;
	}

	public void setWoodStove(Boolean woodStove) {
		this.woodStove = woodStove;
	}

	@Override
	public String toString() {
		return "OpenLConstructionInfo{" +
				"number=" + number +
				", constructionType='" + constructionType + '\'' +
				", dogType=" + dogType +
				", liveStkNo=" + liveStkNo +
				", numberOfTub=" + numberOfTub +
				", swimmingPoolType='" + swimmingPoolType + '\'' +
				", trampoline='" + trampoline + '\'' +
				", woodStove=" + woodStove +
				'}';
	}
}
