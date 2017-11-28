package aaa.utils.openl.model;

import java.time.LocalDateTime;

public class OpenLPolicy {
	@ExcelTableColumnElement(name = "_PK_")
	protected int number;
	protected String policyNumber;
	protected LocalDateTime effectiveDate;
	//private Dollar expectedPremium;

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

	/*public Dollar getExpectedPremium() {
		return expectedPremium;
	}

	public void setExpectedPremium(Dollar expectedPremium) {
		this.expectedPremium = expectedPremium;
	}*/
}
