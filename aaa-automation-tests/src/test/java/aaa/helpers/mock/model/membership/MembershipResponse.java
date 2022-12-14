package aaa.helpers.mock.model.membership;

import java.time.LocalDate;
import java.time.LocalDateTime;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

@ExcelTableElement(sheetName = "MEMBERSHIP_RESPONSE", hasEmptyRows = true)
public class MembershipResponse {
	@ExcelTransient
	private static final String DATE_PATTERN_1 = "M/d/yyyy";

	@ExcelTransient
	private static final String DATE_PATTERN_2 = "MM-dd-yy";

	@ExcelTransient
	private static final String DATE_PATTERN_3 = "M.d.yyyy";

	@ExcelColumnElement(name = "ID")
	private String id;

	@ExcelColumnElement(name = "membershipStatus_A_C_L")
	private String membershipStatusAcl;

	@ExcelColumnElement(dateFormatPatterns = {DATE_PATTERN_1, DATE_PATTERN_2})
	private LocalDate membershipEndDate;

	@ExcelColumnElement(dateFormatPatterns = {DATE_PATTERN_1, DATE_PATTERN_2})
	private LocalDate membershipEffectiveDate;

	private Integer membershipEffectiveDateOffset;
	private Integer membershipEndDateOffset;
	private String membershipNumber;

	@ExcelColumnElement(name = "ersUsageCountPerActive_Member")
	private Double ersUsageCountPerActiveMember;

	@ExcelColumnElement(name = "ersUsage_Abuse")
	private Boolean ersUsageAbuse;

	private String responseMessageRuleDecision;

	@ExcelColumnElement(name = "Service")
	private String service;

	@ExcelColumnElement(dateFormatPatterns = {DATE_PATTERN_1, DATE_PATTERN_2, DATE_PATTERN_3})
	private LocalDate memberStartDate;

	private Integer memberStartDateMonthsOffset;
	private String status;
	private String memberType;

	@ExcelColumnElement(dateFormatPatterns = {DATE_PATTERN_1, DATE_PATTERN_2})
	private LocalDate serviceDate;

	private String type;
	private Boolean chargeble;
	private String memberCoverageType;
	private String firstName;
	private String lastName;
	private String middleName;
	private String suffixTitle;
	private String prefixTitle;

	@ExcelColumnElement(dateFormatPatterns = {DATE_PATTERN_1, DATE_PATTERN_2})
	private LocalDate birthDate;

	private String city;
	private String region;
	private String postalCode;
	private String addressLine1;
	private String addressLine2;
	private String comment;

	@ExcelColumnElement(name = "faultcode")
	private String faultCode;

	@ExcelColumnElement(name = "faultstring")
	private String faultString;

	@ExcelColumnElement(dateFormatPatterns = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
	private LocalDateTime errorTimeStamp;

	private String errorCode;
	private String errorMessageText;
	private String friendlyErrorMessage;
	private String serviceName;
	private String sourceSystem;
	private String ruleDecision;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMembershipStatusAcl() {
		return membershipStatusAcl;
	}

	public void setMembershipStatusAcl(String membershipStatusAcl) {
		this.membershipStatusAcl = membershipStatusAcl;
	}

	public Double getErsUsageCountPerActiveMember() {
		return ersUsageCountPerActiveMember;
	}

	public void setErsUsageCountPerActiveMember(Double ersUsageCountPerActiveMember) {
		this.ersUsageCountPerActiveMember = ersUsageCountPerActiveMember;
	}

	public Boolean getErsUsageAbuse() {
		return ersUsageAbuse;
	}

	public void setErsUsageAbuse(Boolean ersUsageAbuse) {
		this.ersUsageAbuse = ersUsageAbuse;
	}

	public String getResponseMessageRuleDecision() {
		return responseMessageRuleDecision;
	}

	public void setResponseMessageRuleDecision(String responseMessageRuleDecision) {
		this.responseMessageRuleDecision = responseMessageRuleDecision;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public String getFaultString() {
		return faultString;
	}

	public void setFaultString(String faultString) {
		this.faultString = faultString;
	}

	public LocalDate getMembershipEndDate() {
		return membershipEndDate;
	}

	public void setMembershipEndDate(LocalDate membershipEndDate) {
		this.membershipEndDate = membershipEndDate;
	}

	public LocalDate getMembershipEffectiveDate() {
		return membershipEffectiveDate;
	}

	public void setMembershipEffectiveDate(LocalDate membershipEffectiveDate) {
		this.membershipEffectiveDate = membershipEffectiveDate;
	}

	public Integer getMembershipEffectiveDateOffset() {
		return membershipEffectiveDateOffset;
	}

	public void setMembershipEffectiveDateOffset(Integer membershipEffectiveDateOffset) {
		this.membershipEffectiveDateOffset = membershipEffectiveDateOffset;
	}

	public Integer getMembershipEndDateOffset() {
		return membershipEndDateOffset;
	}

	public void setMembershipEndDateOffset(Integer membershipEndDateOffset) {
		this.membershipEndDateOffset = membershipEndDateOffset;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public LocalDate getMemberStartDate() {
		return memberStartDate;
	}

	public void setMemberStartDate(LocalDate memberStartDate) {
		this.memberStartDate = memberStartDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public LocalDate getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(LocalDate serviceDate) {
		this.serviceDate = serviceDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getChargeble() {
		return chargeble;
	}

	public void setChargeble(Boolean chargeble) {
		this.chargeble = chargeble;
	}

	public String getMemberCoverageType() {
		return memberCoverageType;
	}

	public void setMemberCoverageType(String memberCoverageType) {
		this.memberCoverageType = memberCoverageType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSuffixTitle() {
		return suffixTitle;
	}

	public void setSuffixTitle(String suffixTitle) {
		this.suffixTitle = suffixTitle;
	}

	public String getPrefixTitle() {
		return prefixTitle;
	}

	public void setPrefixTitle(String prefixTitle) {
		this.prefixTitle = prefixTitle;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getErrorTimeStamp() {
		return errorTimeStamp;
	}

	public void setErrorTimeStamp(LocalDateTime errorTimeStamp) {
		this.errorTimeStamp = errorTimeStamp;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessageText() {
		return errorMessageText;
	}

	public void setErrorMessageText(String errorMessageText) {
		this.errorMessageText = errorMessageText;
	}

	public String getFriendlyErrorMessage() {
		return friendlyErrorMessage;
	}

	public void setFriendlyErrorMessage(String friendlyErrorMessage) {
		this.friendlyErrorMessage = friendlyErrorMessage;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getRuleDecision() {
		return ruleDecision;
	}

	public void setRuleDecision(String ruleDecision) {
		this.ruleDecision = ruleDecision;
	}

	public Integer getMemberStartDateMonthsOffset() {
		return memberStartDateMonthsOffset;
	}

	public void setMemberStartDateMonthsOffset(Integer memberStartDateMonthsOffset) {
		this.memberStartDateMonthsOffset = memberStartDateMonthsOffset;
	}

	@Override
	public String toString() {
		return "MembershipResponse{" +
				"id='" + id + '\'' +
				", membershipStatusAcl='" + membershipStatusAcl + '\'' +
				", ersUsageCountPerActiveMember=" + ersUsageCountPerActiveMember +
				", ersUsageAbuse=" + ersUsageAbuse +
				", responseMessageRuleDecision='" + responseMessageRuleDecision + '\'' +
				", service='" + service + '\'' +
				", faultCode='" + faultCode + '\'' +
				", faultString='" + faultString + '\'' +
				", membershipEndDate=" + membershipEndDate +
				", membershipEffectiveDate=" + membershipEffectiveDate +
				", memberStartDate=" + memberStartDate +
				", serviceDate=" + serviceDate +
				", birthDate=" + birthDate +
				", errorTimeStamp=" + errorTimeStamp +
				", membershipEffectiveDateOffset=" + membershipEffectiveDateOffset +
				", membershipEndDateOffset=" + membershipEndDateOffset +
				", membershipNumber='" + membershipNumber + '\'' +
				", status='" + status + '\'' +
				", memberType='" + memberType + '\'' +
				", type='" + type + '\'' +
				", chargeble=" + chargeble +
				", memberCoverageType='" + memberCoverageType + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", middleName='" + middleName + '\'' +
				", suffixTitle='" + suffixTitle + '\'' +
				", prefixTitle='" + prefixTitle + '\'' +
				", city='" + city + '\'' +
				", region='" + region + '\'' +
				", postalCode='" + postalCode + '\'' +
				", addressLine1='" + addressLine1 + '\'' +
				", addressLine2='" + addressLine2 + '\'' +
				", comment='" + comment + '\'' +
				", errorCode='" + errorCode + '\'' +
				", errorMessageText='" + errorMessageText + '\'' +
				", friendlyErrorMessage='" + friendlyErrorMessage + '\'' +
				", serviceName='" + serviceName + '\'' +
				", sourceSystem='" + sourceSystem + '\'' +
				", ruleDecision='" + ruleDecision + '\'' +
				", memberStartDateMonthsOffset=" + memberStartDateMonthsOffset +
				'}';
	}
}
