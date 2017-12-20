package aaa.helpers.openl.model;

import java.time.LocalDateTime;
import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLPolicy {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	protected int number;
	protected String policyNumber;
	protected LocalDateTime effectiveDate;

	public int getNumber() {
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

	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
