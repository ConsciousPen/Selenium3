package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.RISK_METER_DATA_SHEET_NAME, headerRowIndex = OpenLFile.RISK_METER_DATA_HEADER_ROW_NUMBER)
public class OpenLRiskMeterData {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	private Double distanceToCoast;
	private Double elevation;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Double getDistanceToCoast() {
		return distanceToCoast;
	}

	public void setDistanceToCoast(Double distanceToCoast) {
		this.distanceToCoast = distanceToCoast;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
}
