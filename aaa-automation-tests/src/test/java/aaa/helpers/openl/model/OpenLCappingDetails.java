package aaa.helpers.openl.model;

import java.time.LocalDateTime;
import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLCappingDetails {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected LocalDateTime plcyInceptionDate;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public LocalDateTime getPlcyInceptionDate() {
		return plcyInceptionDate;
	}

	public void setPlcyInceptionDate(LocalDateTime plcyInceptionDate) {
		this.plcyInceptionDate = plcyInceptionDate;
	}

	@Override
	public String toString() {
		return "OpenLCappingDetails{" +
				"number=" + number +
				", plcyInceptionDate=" + plcyInceptionDate +
				'}';
	}
}
