package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLCoverage {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private int number;

	private int additionalLimitAmount;
	private String coverageCD;
	private String deductible;
	private String glassDeductible;
	private String limit;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getAdditionalLimitAmount() {
		return additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(int additionalLimitAmount) {
		this.additionalLimitAmount = additionalLimitAmount;
	}

	public String getCoverageCD() {
		return coverageCD;
	}

	public void setCoverageCD(String coverageCD) {
		this.coverageCD = coverageCD;
	}

	public String getDeductible() {
		return deductible;
	}

	public void setDeductible(String deductible) {
		this.deductible = deductible;
	}

	public String getGlassDeductible() {
		return glassDeductible;
	}

	public void setGlassDeductible(String glassDeductible) {
		this.glassDeductible = glassDeductible;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
}
