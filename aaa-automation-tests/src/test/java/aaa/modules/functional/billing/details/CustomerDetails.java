package aaa.modules.functional.billing.details;

import java.time.LocalDateTime;

public class CustomerDetails {
	private String customerFirstName;
	private String customerLastName;
	private String customerStreet;
	private String customerCity;
	private String customerState;
	private String customerZip;
	private LocalDateTime customerDOB;

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public String getcustomerStreet() {
		return customerStreet;
	}

	public void setCustomerStreet(String customerStreet) {
		this.customerStreet = customerStreet;
	}

	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	public String getCustomerZip() {
		return customerZip;
	}

	public void setCustomerZip(String customerZip) {
		this.customerZip = customerZip;
	}

	public LocalDateTime getCustomerDOB() {
		return customerDOB;
	}

	public void setCustomerDOB(LocalDateTime customerDOB) {
		this.customerDOB = customerDOB;
	}

	public static class Builder {
		private CustomerDetails customerDetails = new CustomerDetails();

		public Builder setCustomerFirstName(String customerFirstName) {
			this.customerDetails.setCustomerFirstName(customerFirstName);
			return this;
		}

		public Builder setCustomerLastName(String customerLastName) {
			this.customerDetails.setCustomerLastName(customerLastName);
			return this;
		}

		public Builder setCustomerDOB(LocalDateTime customerDOB) {
			this.customerDetails.setCustomerDOB(customerDOB);
			return this;
		}

		public CustomerDetails build() {
			return this.customerDetails;
		}
	}
}
