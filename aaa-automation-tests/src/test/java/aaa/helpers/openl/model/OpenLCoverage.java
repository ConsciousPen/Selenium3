package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLCoverage {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String limit;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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
				", limit='" + limit + '\'' +
				'}';
	}
}
