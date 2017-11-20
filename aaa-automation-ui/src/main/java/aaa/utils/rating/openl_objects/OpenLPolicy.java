package aaa.utils.rating.openl_objects;

import java.time.LocalDateTime;

public abstract class OpenLPolicy {
	private int number;
	private String policyNumber;
	private LocalDateTime effectiveDate;

	public int getNumber() {
		return number;
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
