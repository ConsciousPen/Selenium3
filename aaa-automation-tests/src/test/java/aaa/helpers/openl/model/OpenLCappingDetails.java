package aaa.helpers.openl.model;

import java.time.LocalDateTime;
import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLCappingDetails {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private int number;
	private LocalDateTime plcyInceptionDate;
	private String state;
	private int term;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public LocalDateTime getPlcyInceptionDate() {
		return plcyInceptionDate;
	}

	public void setPlcyInceptionDate(LocalDateTime plcyInceptionDate) {
		this.plcyInceptionDate = plcyInceptionDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}
}
