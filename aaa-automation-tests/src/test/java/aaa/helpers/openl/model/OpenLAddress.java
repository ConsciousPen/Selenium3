package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLAddress {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private Integer number;

	private String state;
	private String zip;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
