package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class HomeCaOpenLDwelling {
	protected HomeCaOpenLAddress address;

	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected Integer ppcValue;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public HomeCaOpenLAddress getAddress() {
		return address;
	}

	public void setAddress(HomeCaOpenLAddress address) {
		this.address = address;
	}

	public Integer getPpcValue() {
		return ppcValue;
	}

	public void setPpcValue(Integer ppcValue) {
		this.ppcValue = ppcValue;
	}
}
