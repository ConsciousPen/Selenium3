package aaa.helpers.openl.model;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public abstract class OpenLPolicy {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String policyNumber;

	@ExcelTransient
	private Dollar expectedPremium;

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

	public Dollar getExpectedPremium() {
		return expectedPremium;
	}

	public void setExpectedPremium(Dollar expectedPremium) {
		this.expectedPremium = expectedPremium;
	}

	public abstract Integer getTerm();

	public Double getPreviousPolicyPremium() {
		return null;
	}

	public abstract String getUnderwriterCode();

	public abstract LocalDateTime getEffectiveDate();

	@Override
	public String toString() {
		return "OpenLPolicy{" +
				"number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
