package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DISCOUNT_INFORMATION_SHEET_NAME, headerRowIndex = OpenLFile.DISCOUNT_INFORMATION_HEADER_ROW_NUMBER)
public class OpenLDiscountInformation {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	@RequiredField
	private Integer autoInsPersistency;
	private String discountByAutoGroup;

	@RequiredField
	private String fireAlarmType;

	@RequiredField
	private Boolean greenHomeDiscApplicability;

	@RequiredField
	private Integer homeInsPersistency;
	private String installmentType;

	@RequiredField
	private Boolean isAAAEmployee;

	@RequiredField
	private Boolean isAMIGMotorPolicyInd;

	@RequiredField
	private Boolean isAMIGWatercraftPolicyInd;

	@RequiredField
	private Boolean isAutoPolicyInd;
	private Boolean isCEQPolicyInd;

	@RequiredField
	private Boolean isCurrAAAMember;

	@RequiredField
	private Boolean isDP3PolicyInd;

	@RequiredField
	private Boolean isLifePolicyInd;

	@RequiredField
	private Boolean isPUPPolicyInd;

	@RequiredField
	private Boolean isPrivateCommunity;

	@RequiredField
	private Boolean isUnderlyingRenterPolicy;
	private Boolean isLightningProtected;

	@RequiredField
	private Integer memberPersistency;
	private Integer noOfConsecutiveYrs;

	@RequiredField
	private Integer noOfYrsSinceLoanInception;

	@RequiredField
	private String paymentPlan;

	@RequiredField
	private Boolean proofCentralFireAlarm;

	@RequiredField
	private Boolean proofCentralTheftAlarm;
	private Boolean proofOfGreenHome;
	private Boolean proofOfPEHCR;

	@RequiredField
	private Boolean proofOfTenant;
	private String rentalPropertyMgr;
	private String sprinklerType;

	@RequiredField
	private String theftAlarmType;
	private Integer timeSinceRenovElectrical;
	private Integer timeSinceRenovHeatOrCooling;
	private Integer timeSinceRenovPlumbing;

	@RequiredField
	private Integer timeSinceRenovRoof;
	private String windStorm;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getAutoInsPersistency() {
		return autoInsPersistency;
	}

	public void setAutoInsPersistency(Integer autoInsPersistency) {
		this.autoInsPersistency = autoInsPersistency;
	}

	public String getDiscountByAutoGroup() {
		return discountByAutoGroup;
	}

	public void setDiscountByAutoGroup(String discountByAutoGroup) {
		this.discountByAutoGroup = discountByAutoGroup;
	}

	public String getFireAlarmType() {
		return fireAlarmType;
	}

	public void setFireAlarmType(String fireAlarmType) {
		this.fireAlarmType = fireAlarmType;
	}

	public Boolean getGreenHomeDiscApplicability() {
		return greenHomeDiscApplicability;
	}

	public void setGreenHomeDiscApplicability(Boolean greenHomeDiscApplicability) {
		this.greenHomeDiscApplicability = greenHomeDiscApplicability;
	}

	public Integer getHomeInsPersistency() {
		return homeInsPersistency;
	}

	public void setHomeInsPersistency(Integer homeInsPersistency) {
		this.homeInsPersistency = homeInsPersistency;
	}

	public String getInstallmentType() {
		return installmentType;
	}

	public void setInstallmentType(String installmentType) {
		this.installmentType = installmentType;
	}

	public Boolean isAAAEmployee() {
		return isAAAEmployee;
	}

	public void setAAAEmployee(Boolean isAAAEmployee) {
		this.isAAAEmployee = isAAAEmployee;
	}

	public Boolean isAMIGMotorPolicyInd() {
		return isAMIGMotorPolicyInd;
	}

	public void setAMIGMotorPolicyInd(Boolean isAMIGMotorPolicyInd) {
		this.isAMIGMotorPolicyInd = isAMIGMotorPolicyInd;
	}

	public Boolean isAMIGWatercraftPolicyInd() {
		return isAMIGWatercraftPolicyInd;
	}

	public void setAMIGWatercraftPolicyInd(Boolean isAMIGWatercraftPolicyInd) {
		this.isAMIGWatercraftPolicyInd = isAMIGWatercraftPolicyInd;
	}

	public Boolean isAutoPolicyInd() {
		return isAutoPolicyInd;
	}

	public void setAutoPolicyInd(Boolean isAutoPolicyInd) {
		this.isAutoPolicyInd = isAutoPolicyInd;
	}

	public Boolean isCEQPolicyInd() {
		return isCEQPolicyInd;
	}

	public void setCEQPolicyInd(Boolean isCEQPolicyInd) {
		this.isCEQPolicyInd = isCEQPolicyInd;
	}

	public Boolean isCurrAAAMember() {
		return isCurrAAAMember;
	}

	public void setCurrAAAMember(Boolean isCurrAAAMember) {
		this.isCurrAAAMember = isCurrAAAMember;
	}

	public Boolean isDP3PolicyInd() {
		return isDP3PolicyInd;
	}

	public void setDP3PolicyInd(Boolean isDP3PolicyInd) {
		this.isDP3PolicyInd = isDP3PolicyInd;
	}

	public Boolean isLifePolicyInd() {
		return isLifePolicyInd;
	}

	public void setLifePolicyInd(Boolean isLifePolicyInd) {
		this.isLifePolicyInd = isLifePolicyInd;
	}

	public Boolean isPUPPolicyInd() {
		return isPUPPolicyInd;
	}

	public void setPUPPolicyInd(Boolean isPUPPolicyInd) {
		this.isPUPPolicyInd = isPUPPolicyInd;
	}

	public Boolean isPrivateCommunity() {
		return isPrivateCommunity;
	}

	public void setPrivateCommunity(Boolean isPrivateCommunity) {
		this.isPrivateCommunity = isPrivateCommunity;
	}

	public Boolean isUnderlyingRenterPolicy() {
		return isUnderlyingRenterPolicy;
	}

	public void setUnderlyingRenterPolicy(Boolean isUnderlyingRenterPolicy) {
		this.isUnderlyingRenterPolicy = isUnderlyingRenterPolicy;
	}

	public Boolean isLightningProtected() {
		return isLightningProtected;
	}

	public void setLightningProtected(Boolean isLightningProtected) {
		this.isLightningProtected = isLightningProtected;
	}

	public Integer getMemberPersistency() {
		return memberPersistency;
	}

	public void setMemberPersistency(Integer memberPersistency) {
		this.memberPersistency = memberPersistency;
	}

	public Integer getNoOfConsecutiveYrs() {
		return noOfConsecutiveYrs == null ? 0 : noOfConsecutiveYrs;
	}

	public void setNoOfConsecutiveYrs(Integer noOfConsecutiveYrs) {
		this.noOfConsecutiveYrs = noOfConsecutiveYrs;
	}

	public Integer getNoOfYrsSinceLoanInception() {
		return noOfYrsSinceLoanInception;
	}

	public void setNoOfYrsSinceLoanInception(Integer noOfYrsSinceLoanInception) {
		this.noOfYrsSinceLoanInception = noOfYrsSinceLoanInception;
	}

	public String getPaymentPlan() {
		return paymentPlan;
	}

	public void setPaymentPlan(String paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	public Boolean getProofCentralFireAlarm() {
		return proofCentralFireAlarm;
	}

	public void setProofCentralFireAlarm(Boolean proofCentralFireAlarm) {
		this.proofCentralFireAlarm = proofCentralFireAlarm;
	}

	public Boolean getProofCentralTheftAlarm() {
		return proofCentralTheftAlarm;
	}

	public void setProofCentralTheftAlarm(Boolean proofCentralTheftAlarm) {
		this.proofCentralTheftAlarm = proofCentralTheftAlarm;
	}

	public Boolean getProofOfGreenHome() {
		return proofOfGreenHome;
	}

	public void setProofOfGreenHome(Boolean proofOfGreenHome) {
		this.proofOfGreenHome = proofOfGreenHome;
	}

	public Boolean getProofOfPEHCR() {
		return proofOfPEHCR;
	}

	public void setProofOfPEHCR(Boolean proofOfPEHCR) {
		this.proofOfPEHCR = proofOfPEHCR;
	}

	public Boolean getProofOfTenant() {
		return proofOfTenant;
	}

	public void setProofOfTenant(Boolean proofOfTenant) {
		this.proofOfTenant = proofOfTenant;
	}

	public String getRentalPropertyMgr() {
		return rentalPropertyMgr == null ? "None" : rentalPropertyMgr;
	}

	public void setRentalPropertyMgr(String rentalPropertyMgr) {
		this.rentalPropertyMgr = rentalPropertyMgr;
	}

	public String getSprinklerType() {
		return sprinklerType;
	}

	public void setSprinklerType(String sprinklerType) {
		this.sprinklerType = sprinklerType;
	}

	public String getTheftAlarmType() {
		return theftAlarmType;
	}

	public void setTheftAlarmType(String theftAlarmType) {
		this.theftAlarmType = theftAlarmType;
	}

	public Integer getTimeSinceRenovElectrical() {
		return timeSinceRenovElectrical;
	}

	public void setTimeSinceRenovElectrical(Integer timeSinceRenovElectrical) {
		this.timeSinceRenovElectrical = timeSinceRenovElectrical;
	}

	public Integer getTimeSinceRenovHeatOrCooling() {
		return timeSinceRenovHeatOrCooling;
	}

	public void setTimeSinceRenovHeatOrCooling(Integer timeSinceRenovHeatOrCooling) {
		this.timeSinceRenovHeatOrCooling = timeSinceRenovHeatOrCooling;
	}

	public Integer getTimeSinceRenovPlumbing() {
		return timeSinceRenovPlumbing;
	}

	public void setTimeSinceRenovPlumbing(Integer timeSinceRenovPlumbing) {
		this.timeSinceRenovPlumbing = timeSinceRenovPlumbing;
	}

	public Integer getTimeSinceRenovRoof() {
		return timeSinceRenovRoof;
	}

	public void setTimeSinceRenovRoof(Integer timeSinceRenovRoof) {
		this.timeSinceRenovRoof = timeSinceRenovRoof;
	}

	public String getWindStorm() {
		return windStorm;
	}

	public void setWindStorm(String windStorm) {
		this.windStorm = windStorm;
	}
}
