package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoSSOpenLPolicy extends OpenLPolicy {
	private int term;
	private boolean isHomeOwner;
	private int creditScore;
	private boolean isAAAMember;
	private String aaaHomePolicy;
	private String aaaRentersPolicy;
	private String aaaCondoPolicy;
	private boolean aaaLifePolicy;
	private boolean aaaMotorcyclePolicy;
	private boolean isEMember;
	private int memberPersistency;
	private int autoInsurancePersistency;
	private int aaaInsurancePersistency;
	private int aaaAsdInsurancePersistency;
	private boolean isAARP;
	private boolean isEmployee;
	private boolean isAdvanceShopping;
	private String paymentPlanType;
	private String distributionChannel;
	private boolean unacceptableRisk;
	private String priorBILimit;
	private int reinstatements;
	private int yearsAtFaultAccidentFree;
	private int yearsIncidentFree;
	private int aggregateCompClaims;
	private int nafAccidents;
	private int avgAnnualERSperMember;
	private int insuredAge;
	private int noOfVehiclesExcludingTrailer;
	private boolean multiCar;
	private boolean supplementalSpousalLiability;

	@ExcelTableElement(sheetName = "Batch- CappingDetails")
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTableElement(sheetName = "Batch- VehicleAZ")
	private List<OpenLVehicle> vehicles;

	@ExcelTableElement(sheetName = "Batch- DriverAZ")
	private List<OpenLDriver> drivers;

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public boolean isHomeOwner() {
		return isHomeOwner;
	}

	public void setHomeOwner(boolean homeOwner) {
		isHomeOwner = homeOwner;
	}

	public int getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(int creditScore) {
		this.creditScore = creditScore;
	}

	public boolean isAAAMember() {
		return isAAAMember;
	}

	public void setAAAMember(boolean isAAAMember) {
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

	public boolean isAaaLifePolicy() {
		return aaaLifePolicy;
	}

	public void setAaaLifePolicy(boolean aaaLifePolicy) {
		this.aaaLifePolicy = aaaLifePolicy;
	}

	public boolean isAaaMotorcyclePolicy() {
		return aaaMotorcyclePolicy;
	}

	public void setAaaMotorcyclePolicy(boolean aaaMotorcyclePolicy) {
		this.aaaMotorcyclePolicy = aaaMotorcyclePolicy;
	}

	public boolean isEMember() {
		return isEMember;
	}

	public void setEMember(boolean isEMember) {
		this.isEMember = isEMember;
	}

	public int getMemberPersistency() {
		return memberPersistency;
	}

	public void setMemberPersistency(int memberPersistency) {
		this.memberPersistency = memberPersistency;
	}

	public int getAutoInsurancePersistency() {
		return autoInsurancePersistency;
	}

	public void setAutoInsurancePersistency(int autoInsurancePersistency) {
		this.autoInsurancePersistency = autoInsurancePersistency;
	}

	public int getAaaInsurancePersistency() {
		return aaaInsurancePersistency;
	}

	public void setAaaInsurancePersistency(int aaaInsurancePersistency) {
		this.aaaInsurancePersistency = aaaInsurancePersistency;
	}

	public int getAaaAsdInsurancePersistency() {
		return aaaAsdInsurancePersistency;
	}

	public void setAaaAsdInsurancePersistency(int aaaAsdInsurancePersistency) {
		this.aaaAsdInsurancePersistency = aaaAsdInsurancePersistency;
	}

	public boolean isAARP() {
		return isAARP;
	}

	public void setAARP(boolean isAARP) {
		this.isAARP = isAARP;
	}

	public boolean isEmployee() {
		return isEmployee;
	}

	public void setEmployee(boolean employee) {
		isEmployee = employee;
	}

	public boolean isAdvanceShopping() {
		return isAdvanceShopping;
	}

	public void setAdvanceShopping(boolean advanceShopping) {
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

	public boolean isUnacceptableRisk() {
		return unacceptableRisk;
	}

	public void setUnacceptableRisk(boolean unacceptableRisk) {
		this.unacceptableRisk = unacceptableRisk;
	}

	public String getPriorBILimit() {
		return priorBILimit;
	}

	public void setPriorBILimit(String priorBILimit) {
		this.priorBILimit = priorBILimit;
	}

	public int getReinstatements() {
		return reinstatements;
	}

	public void setReinstatements(int reinstatements) {
		this.reinstatements = reinstatements;
	}

	public int getYearsAtFaultAccidentFree() {
		return yearsAtFaultAccidentFree;
	}

	public void setYearsAtFaultAccidentFree(int yearsAtFaultAccidentFree) {
		this.yearsAtFaultAccidentFree = yearsAtFaultAccidentFree;
	}

	public int getYearsIncidentFree() {
		return yearsIncidentFree;
	}

	public void setYearsIncidentFree(int yearsIncidentFree) {
		this.yearsIncidentFree = yearsIncidentFree;
	}

	public int getAggregateCompClaims() {
		return aggregateCompClaims;
	}

	public void setAggregateCompClaims(int aggregateCompClaims) {
		this.aggregateCompClaims = aggregateCompClaims;
	}

	public int getNafAccidents() {
		return nafAccidents;
	}

	public void setNafAccidents(int nafAccidents) {
		this.nafAccidents = nafAccidents;
	}

	public int getAvgAnnualERSperMember() {
		return avgAnnualERSperMember;
	}

	public void setAvgAnnualERSperMember(int avgAnnualERSperMember) {
		this.avgAnnualERSperMember = avgAnnualERSperMember;
	}

	public int getInsuredAge() {
		return insuredAge;
	}

	public void setInsuredAge(int insuredAge) {
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

	public int getNoOfVehiclesExcludingTrailer() {
		return noOfVehiclesExcludingTrailer;
	}

	public void setNoOfVehiclesExcludingTrailer(int noOfVehiclesExcludingTrailer) {
		this.noOfVehiclesExcludingTrailer = noOfVehiclesExcludingTrailer;
	}

	public boolean isMultiCar() {
		return multiCar;
	}

	public void setMultiCar(boolean multiCar) {
		this.multiCar = multiCar;
	}

	public List<OpenLDriver> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<OpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public boolean isSupplementalSpousalLiability() {
		return supplementalSpousalLiability;
	}

	public void setSupplementalSpousalLiability(boolean supplementalSpousalLiability) {
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
