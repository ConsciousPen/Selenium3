package aaa.modules.cft.details;


import toolkit.exceptions.IstfException;

import java.util.ArrayList;
import java.util.List;

public class BillingAccountDetails {

	private String billingAccountNumber;
	private List<PolicyDetails> policyDetails = new ArrayList<>();

	public String getBillingAccountNumber() {
		return billingAccountNumber;
	}

	public void setBillingAccountNumber(String billingAccountNumber) {
		this.billingAccountNumber = billingAccountNumber;
	}

	public List<PolicyDetails> getPolicyDetails() {
		return policyDetails;
	}

	public PolicyDetails getCurrentPolicyDetails() {
		if (getPolicyDetails().isEmpty()) {
			throw new IstfException("There are no registered policies for current thread/test");
		}
		return getPolicyDetails().get(getPolicyDetails().size() - 1);
	}

	public void setPolicyDetails(List<PolicyDetails> policyDetails) {
		this.policyDetails = policyDetails;
	}

	public static class Builder {
		private BillingAccountDetails billingAccountDetails = new BillingAccountDetails();

		public Builder setBillingAccountNumber(String billingAccountNumber) {
			this.billingAccountDetails.setBillingAccountNumber(billingAccountNumber);
			return this;
		}

		public Builder setPolicyDetails(List<PolicyDetails> policyDetails) {
			this.billingAccountDetails.setPolicyDetails(policyDetails);
			return this;
		}

		public Builder addPolicyDetails(PolicyDetails policyDetails) {
			this.billingAccountDetails.getPolicyDetails().add(policyDetails);
			return this;
		}

		public BillingAccountDetails build() {
			return this.billingAccountDetails;
		}
	}
}
