package aaa.utils.openl.model;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;

public abstract class OpenLPolicy {
	private int number;
	private String policyNumber;
	private LocalDateTime effectiveDate;
	private Dollar expectedPremium;

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

	public Dollar getExpectedPremium() {
		return expectedPremium;
	}

	public void setExpectedPremium(Dollar expectedPremium) {
		this.expectedPremium = expectedPremium;
	}
}
