package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLPolicy {
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

	@Override
	public String toString() {
		return "OpenLPolicy{" +
				"number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
