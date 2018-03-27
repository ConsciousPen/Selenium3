package aaa.helpers.openl.model;

import java.time.LocalDateTime;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public abstract class OpenLPolicy {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String policyNumber;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public abstract Integer getTerm();

	public abstract LocalDateTime getEffectiveDate();

	@Override
	public String toString() {
		return "OpenLPolicy{" +
				"number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
