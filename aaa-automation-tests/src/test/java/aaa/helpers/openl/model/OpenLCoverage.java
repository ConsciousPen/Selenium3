package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLCoverage {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected Integer additionalLimitAmount;
	protected String coverageCD;
	protected String deductible;
	protected String glassDeductible;
	protected String limit;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Integer getAdditionalLimitAmount() {
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

	@Override
	public String toString() {
		return "OpenLCoverage{" +
				"number=" + number +
				", additionalLimitAmount=" + additionalLimitAmount +
				", coverageCD='" + coverageCD + '\'' +
				", deductible='" + deductible + '\'' +
				", glassDeductible='" + glassDeductible + '\'' +
				", limit='" + limit + '\'' +
				'}';
	}
}
