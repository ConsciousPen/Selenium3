package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_ADDRESS_SHEET_NAME, headerRowIndex = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
public class PUPOpenLAddress {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String county;
	private String zipCode;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
