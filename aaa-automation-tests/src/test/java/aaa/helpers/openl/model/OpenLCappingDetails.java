package aaa.helpers.openl.model;

import java.time.LocalDate;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public class OpenLCappingDetails {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected LocalDate plcyInceptionDate;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public LocalDate getPlcyInceptionDate() {
		return plcyInceptionDate;
	}

	public void setPlcyInceptionDate(LocalDate plcyInceptionDate) {
		this.plcyInceptionDate = plcyInceptionDate;
	}
}
