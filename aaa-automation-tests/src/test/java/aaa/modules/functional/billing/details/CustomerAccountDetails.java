package aaa.modules.functional.billing.details;

import toolkit.exceptions.IstfException;

import java.util.ArrayList;
import java.util.List;

public class CustomerAccountDetails {
	private String customerNumber;
	private List<CustomerDetails> customerDetails = new ArrayList<>();

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public List<CustomerDetails> getCustomerDetails() {
		return customerDetails;
	}

	public CustomerDetails getCurrentCustomerDetails() {
		if (getCustomerDetails().isEmpty()) {
			throw new IstfException("There are no registered customers for current thread/test");
		}
		return getCustomerDetails().get(getCustomerDetails().size() - 1);
	}

	public void setCustomerDetails(List<CustomerDetails> customerDetails) {
		this.customerDetails = customerDetails;
	}

	public static class Builder {
		private CustomerAccountDetails customerAccountDetails = new CustomerAccountDetails();

		public Builder setCustomerNumber(String customerNumber) {
			this.customerAccountDetails.setCustomerNumber(customerNumber);
			return this;
		}

		public Builder setCustomerDetails(List<CustomerDetails> customerDetails) {
			this.customerAccountDetails.setCustomerDetails(customerDetails);
			return this;
		}

		public Builder addCustomerDetails(CustomerDetails customerDetails) {
			this.customerAccountDetails.getCustomerDetails().add(customerDetails);
			return this;
		}

		public CustomerAccountDetails build() {
			return this.customerAccountDetails;
		}
	}

}
