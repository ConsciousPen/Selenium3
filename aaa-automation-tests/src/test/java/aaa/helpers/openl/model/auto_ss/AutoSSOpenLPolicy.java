package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoSSOpenLPolicy extends OpenLPolicy {
	private Integer term;
	private Boolean isHomeOwner;
	private Integer creditScore;
	private Boolean isAAAMember;
	private String aaaHomePolicy;
	private String aaaRentersPolicy;
	private String aaaCondoPolicy;
	private Boolean aaaLifePolicy;
	private Boolean aaaMotorcyclePolicy;
	private Boolean isEMember;
	private Integer memberPersistency;
	private Integer autoInsurancePersistency;
	private Integer aaaInsurancePersistency;
	private Integer aaaAsdInsurancePersistency;
	private Boolean isAARP;
	private Boolean isEmployee;
	private Boolean isAdvanceShopping;
	private String paymentPlanType;
	private String distributionChannel;
	private Boolean unacceptableRisk;
	private String priorBILimit;
	private Integer reinstatements;
	private Integer yearsAtFaultAccidentFree;
	private Integer yearsIncidentFree;
	private Integer aggregateCompClaims;
	private Integer nafAccidents;
	private Integer avgAnnualERSperMember;
	private Integer insuredAge;
	private Integer noOfVehiclesExcludingTrailer;
	private Boolean multiCar;
	private Boolean supplementalSpousalLiability; // OR specific

	@ExcelTableElement(sheetName = "Batch- CappingDetails", headerRowNumber = OpenLFile.CAPPINGDETAILS_HEADER_ROW_NUMBER)
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTableElement(sheetName = "Batch- VehicleAZ", headerRowNumber = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
	private List<OpenLVehicle> vehicles;

	@ExcelTableElement(sheetName = "Batch- DriverAZ", headerRowNumber = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> drivers;

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Boolean isHomeOwner() {
		return isHomeOwner;
	}

	public void setHomeOwner(Boolean homeOwner) {
		isHomeOwner = homeOwner;
	}

	public Integer getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(Integer creditScore) {
		this.creditScore = creditScore;
	}

	public Boolean isAAAMember() {
		return isAAAMember;
	}

	public void setAAAMember(Boolean isAAAMember) {
		this.isAAAMember = isAAAMember;
	}

	public String getAaaHomePolicy() {
		return aaaHomePolicy;
	}

	public void setAaaHomePolicy(String aaaHomePolicy) {
		this.aaaHomePolicy = aaaHomePolicy;
	}

	public String getAaaRentersPolicy() {
		return aaaRentersPolicy;
	}

	public void setAaaRentersPolicy(String aaaRentersPolicy) {
		this.aaaRentersPolicy = aaaRentersPolicy;
	}

	public String getAaaCondoPolicy() {
		return aaaCondoPolicy;
	}

	public void setAaaCondoPolicy(String aaaCondoPolicy) {
		this.aaaCondoPolicy = aaaCondoPolicy;
	}

	public Boolean isAaaLifePolicy() {
		return aaaLifePolicy;
	}

	public void setAaaLifePolicy(Boolean aaaLifePolicy) {
		this.aaaLifePolicy = aaaLifePolicy;
	}

	public Boolean isAaaMotorcyclePolicy() {
		return aaaMotorcyclePolicy;
	}

	public void setAaaMotorcyclePolicy(Boolean aaaMotorcyclePolicy) {
		this.aaaMotorcyclePolicy = aaaMotorcyclePolicy;
	}

	public Boolean isEMember() {
		return isEMember;
	}

	public void setEMember(Boolean isEMember) {
		this.isEMember = isEMember;
	}

	public Integer getMemberPersistency() {
		return memberPersistency;
	}

	public void setMemberPersistency(Integer memberPersistency) {
		this.memberPersistency = memberPersistency;
	}

	public Integer getAutoInsurancePersistency() {
		return autoInsurancePersistency;
	}

	public void setAutoInsurancePersistency(Integer autoInsurancePersistency) {
		this.autoInsurancePersistency = autoInsurancePersistency;
	}

	public Integer getAaaInsurancePersistency() {
		return aaaInsurancePersistency;
	}

	public void setAaaInsurancePersistency(Integer aaaInsurancePersistency) {
		this.aaaInsurancePersistency = aaaInsurancePersistency;
	}

	public Integer getAaaAsdInsurancePersistency() {
		return aaaAsdInsurancePersistency;
	}

	public void setAaaAsdInsurancePersistency(Integer aaaAsdInsurancePersistency) {
		this.aaaAsdInsurancePersistency = aaaAsdInsurancePersistency;
	}

	public Boolean isAARP() {
		return isAARP;
	}

	public void setAARP(Boolean isAARP) {
		this.isAARP = isAARP;
	}

	public Boolean isEmployee() {
		return isEmployee;
	}

	public void setEmployee(Boolean employee) {
		isEmployee = employee;
	}

	public Boolean isAdvanceShopping() {
		return isAdvanceShopping;
	}

	public void setAdvanceShopping(Boolean advanceShopping) {
		isAdvanceShopping = advanceShopping;
	}

	public String getPaymentPlanType() {
		return paymentPlanType;
	}

	public void setPaymentPlanType(String paymentPlanType) {
		this.paymentPlanType = paymentPlanType;
	}

	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public Boolean isUnacceptableRisk() {
		return unacceptableRisk;
	}

	public void setUnacceptableRisk(Boolean unacceptableRisk) {
		this.unacceptableRisk = unacceptableRisk;
	}

	public String getPriorBILimit() {
		return priorBILimit;
	}

	public void setPriorBILimit(String priorBILimit) {
		this.priorBILimit = priorBILimit;
	}

	public Integer getReinstatements() {
		return reinstatements;
	}

	public void setReinstatements(Integer reinstatements) {
		this.reinstatements = reinstatements;
	}

	public Integer getYearsAtFaultAccidentFree() {
		return yearsAtFaultAccidentFree;
	}

	public void setYearsAtFaultAccidentFree(Integer yearsAtFaultAccidentFree) {
		this.yearsAtFaultAccidentFree = yearsAtFaultAccidentFree;
	}

	public Integer getYearsIncidentFree() {
		return yearsIncidentFree;
	}

	public void setYearsIncidentFree(Integer yearsIncidentFree) {
		this.yearsIncidentFree = yearsIncidentFree;
	}

	public Integer getAggregateCompClaims() {
		return aggregateCompClaims;
	}

	public void setAggregateCompClaims(Integer aggregateCompClaims) {
		this.aggregateCompClaims = aggregateCompClaims;
	}

	public Integer getNafAccidents() {
		return nafAccidents;
	}

	public void setNafAccidents(Integer nafAccidents) {
		this.nafAccidents = nafAccidents;
	}

	public Integer getAvgAnnualERSperMember() {
		return avgAnnualERSperMember;
	}

	public void setAvgAnnualERSperMember(Integer avgAnnualERSperMember) {
		this.avgAnnualERSperMember = avgAnnualERSperMember;
	}

	public Integer getInsuredAge() {
		return insuredAge;
	}

	public void setInsuredAge(Integer insuredAge) {
		this.insuredAge = insuredAge;
	}

	public List<OpenLCappingDetails> getCappingDetails() {
		return cappingDetails;
	}

	public void setCappingDetails(List<OpenLCappingDetails> cappingDetails) {
		this.cappingDetails = new ArrayList<>(cappingDetails);
	}

	public List<OpenLVehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<OpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public Integer getNoOfVehiclesExcludingTrailer() {
		return noOfVehiclesExcludingTrailer;
	}

	public void setNoOfVehiclesExcludingTrailer(int noOfVehiclesExcludingTrailer) {
		this.noOfVehiclesExcludingTrailer = noOfVehiclesExcludingTrailer;
	}

	public Boolean isMultiCar() {
		return multiCar;
	}

	public void setMultiCar(Boolean multiCar) {
		this.multiCar = multiCar;
	}

	public List<OpenLDriver> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<OpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public Boolean isSupplementalSpousalLiability() {
		return supplementalSpousalLiability;
	}

	public void setSupplementalSpousalLiability(Boolean supplementalSpousalLiability) {
		this.supplementalSpousalLiability = supplementalSpousalLiability;
	}

	@Override
	public String toString() {
		return "AutoSSOpenLPolicy{" +
				"term=" + term +
				", isHomeOwner=" + isHomeOwner +
				", creditScore=" + creditScore +
				", isAAAMember=" + isAAAMember +
				", aaaHomePolicy='" + aaaHomePolicy + '\'' +
				", aaaRentersPolicy='" + aaaRentersPolicy + '\'' +
				", aaaCondoPolicy='" + aaaCondoPolicy + '\'' +
				", aaaLifePolicy=" + aaaLifePolicy +
				", aaaMotorcyclePolicy=" + aaaMotorcyclePolicy +
				", isEMember=" + isEMember +
				", memberPersistency=" + memberPersistency +
				", autoInsurancePersistency=" + autoInsurancePersistency +
				", aaaInsurancePersistency=" + aaaInsurancePersistency +
				", aaaAsdInsurancePersistency=" + aaaAsdInsurancePersistency +
				", isAARP=" + isAARP +
				", isEmployee=" + isEmployee +
				", isAdvanceShopping=" + isAdvanceShopping +
				", paymentPlanType='" + paymentPlanType + '\'' +
				", distributionChannel='" + distributionChannel + '\'' +
				", unacceptableRisk=" + unacceptableRisk +
				", priorBILimit='" + priorBILimit + '\'' +
				", reinstatements=" + reinstatements +
				", yearsAtFaultAccidentFree=" + yearsAtFaultAccidentFree +
				", yearsIncidentFree=" + yearsIncidentFree +
				", aggregateCompClaims=" + aggregateCompClaims +
				", nafAccidents=" + nafAccidents +
				", avgAnnualERSperMember=" + avgAnnualERSperMember +
				", insuredAge=" + insuredAge +
				", cappingDetails=" + cappingDetails +
				", vehicles=" + vehicles +
				", noOfVehiclesExcludingTrailer=" + noOfVehiclesExcludingTrailer +
				", multiCar=" + multiCar +
				", drivers=" + drivers +
				'}';
	}
}
