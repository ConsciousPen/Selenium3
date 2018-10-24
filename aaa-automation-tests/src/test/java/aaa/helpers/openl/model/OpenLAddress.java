package aaa.helpers.openl.model;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.ADDRESS_SHEET_NAME, headerRowIndex = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
public class OpenLAddress {
	@RequiredField
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String state;

	@RequiredField
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
}
