package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = HomeCaOpenLFile.SCHEDULED_PROPERTY_ITEM_SHEET_NAME, headerRowIndex = HomeCaOpenLFile.SCHEDULED_PROPERTY_ITEM_HEADER_ROW_NUMBER)
public class HomeCaOpenLScheduledPropertyItem {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	@RequiredField
	private Double limit;

	@RequiredField
	private String propertyType;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
}
