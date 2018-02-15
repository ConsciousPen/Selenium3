package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLAddress {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String state;
	protected String zip;

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

	@Override
	public String toString() {
		return "OpenLAddress{" +
				"number=" + number +
				", state='" + state + '\'' +
				", zip='" + zip + '\'' +
				'}';
	}
}
