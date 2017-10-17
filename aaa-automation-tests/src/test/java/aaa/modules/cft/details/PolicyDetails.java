package aaa.modules.cft.details;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PolicyDetails {

	private String policyNumber;
	private LocalDateTime policyEffDate;
	private LocalDateTime policyExpDate;
	private List<LocalDateTime> installments = new ArrayList<>();

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public LocalDateTime getPolicyEffDate() {
		return policyEffDate;
	}

	public void setPolicyEffDate(LocalDateTime policyEffDate) {
		this.policyEffDate = policyEffDate;
	}

	public LocalDateTime getPolicyExpDate() {
		return policyExpDate;
	}

	public void setPolicyExpDate(LocalDateTime policyExpDate) {
		this.policyExpDate = policyExpDate;
	}

	public List<LocalDateTime> getInstallments() {
		return installments;
	}

	public void setInstallments(List<LocalDateTime> installments) {
		this.installments = installments;
	}

	public static class Builder {
		private PolicyDetails policyDetails = new PolicyDetails();

		public Builder setPolicyNumber(String policyNumber) {
			this.policyDetails.setPolicyNumber(policyNumber);
			return this;
		}

		public Builder setPolicyEffectiveDate(LocalDateTime policyEffectiveDate) {
			this.policyDetails.setPolicyEffDate(policyEffectiveDate);
			return this;
		}

		public Builder setPolicyExpirationDate(LocalDateTime policyExpirationDate) {
			this.policyDetails.setPolicyExpDate(policyExpirationDate);
			return this;
		}

		public Builder setPolicyInstallmentsSchedule(List<LocalDateTime> installmentsSchedule) {
			this.policyDetails.setInstallments(installmentsSchedule);
			return this;
		}

		public PolicyDetails build() {
			return this.policyDetails;
		}

	}
}
