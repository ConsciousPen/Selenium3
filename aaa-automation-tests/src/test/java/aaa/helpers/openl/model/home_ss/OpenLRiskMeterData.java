package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLRiskMeterData {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
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

	@Override
	public String toString() {
		return "OpenLRiskMeterData{" +
				"number=" + number +
				", distanceToCoast=" + distanceToCoast +
				", elevation=" + elevation +
				'}';
	}
}
