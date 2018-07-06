package aaa.helpers.mock.model.membership;

import java.util.Objects;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "MEMBERSHIP_REQUEST", hasEmptyRows = true)
public class MembershipRequest {
	@ExcelColumnElement(name = "ID")
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MembershipRequest otherMembershipRequest = (MembershipRequest) o;
		return Objects.equals(getId(), otherMembershipRequest.getId()) &&
				Objects.equals(getMembershipNumber(), otherMembershipRequest.getMembershipNumber()) &&
				Objects.equals(getUserId(), otherMembershipRequest.getUserId()) &&
				Objects.equals(getTransactionType(), otherMembershipRequest.getTransactionType()) &&
				Objects.equals(getApplication(), otherMembershipRequest.getApplication()) &&
				Objects.equals(getSubSystem(), otherMembershipRequest.getSubSystem()) &&
				Objects.equals(getAddress(), otherMembershipRequest.getAddress()) &&
				Objects.equals(getCorrelationId(), otherMembershipRequest.getCorrelationId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getId(), getMembershipNumber(), getUserId(), getTransactionType(), getApplication(), getSubSystem(), getAddress(), getCorrelationId());
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
