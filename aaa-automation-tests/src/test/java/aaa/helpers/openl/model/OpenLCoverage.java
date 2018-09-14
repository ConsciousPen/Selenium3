package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public class OpenLCoverage {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	@ExcelColumnElement(ignoreCase = true)
	protected String coverageCd;

	protected String limit;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getCoverageCd() {
		return coverageCd;
	}

	public void setCoverageCd(String coverageCd) {
		this.coverageCd = coverageCd;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
}
