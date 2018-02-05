package aaa.helpers.mock.model.membership;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class MembershipRequest {
	@ExcelTableColumnElement(name = "ID")
	private String id;

	private String membershipNumber;
	private String userId;
	private String transactionType;
	private String application;
	private String subSystem;
	private String address;
	private String correlationId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	@Override
	public String toString() {
		return "MembershipRequest{" +
				"id='" + id + '\'' +
				", membershipNumber='" + membershipNumber + '\'' +
				", userId='" + userId + '\'' +
				", transactionType='" + transactionType + '\'' +
				", application='" + application + '\'' +
				", subSystem='" + subSystem + '\'' +
				", address='" + address + '\'' +
				", correlationId='" + correlationId + '\'' +
				'}';
	}
}
