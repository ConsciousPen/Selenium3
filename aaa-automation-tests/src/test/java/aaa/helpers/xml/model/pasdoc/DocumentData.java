package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentData {

	@XmlElement(name = "PolicyNumber")
	private String policyNumber;

	@XmlElement(name = "ProductType")
	private String productType;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "TransactionCode")
	private String transactionCode;

	@XmlElement(name = "TransactionEffectiveDate")
	private String transactionEffectiveDate;

	@XmlElement(name = "TransactionReason")
	private String transactionReason;

	@XmlElement(name = "TransactionDate")
	private String transactionDate;

	@XmlElement(name = "EffectiveDate")
	private String effectiveDate;

	@XmlElement(name = "ExpiredDate")
	private String expiredDate;

	@XmlElement(name = "TermDurationInMonths")
	private String termDurationInMonths;

	@XmlElement(name = "NamedNonOwnerPolicy")
	private String namedNonOwnerPolicy;

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "AAAInsuredSince")
	private String aaaInsuredSince;

	@XmlElement(name = "RenewalCycle")
	private String renewalCycle;

	@XmlElement(name = "CancellationEffectiveDate")
	private String cancellationEffectiveDate;

	@XmlElement(name = "Insured")
	private String insured;

	@XmlElement(name = "Agent")
	private Agent agent;

	@XmlElement(name = "Agency")
	private Agency agency;

	@XmlElement(name = "InsuranceScore")
	private String insuranceScore;

	@XmlElementWrapper(name = "OtherOrPriorPolicies")
	@XmlElement(name = "OtherOrPriorPolicy")
	private List<String> otherOrPriorPolicy = new LinkedList<>();

	@XmlElement(name = "PriorCarrier")
	private String priorCarrier;

	@XmlElementWrapper(name = "AlternateCoverages")
	@XmlElement(name = "AlternateCoverage")
	private List<String> alternateCoverage = new LinkedList<>();

	@XmlElementWrapper(name = "Endorsements")
	@XmlElement(name = "Endorsement")
	private List<String> endorsement = new LinkedList<>();

	@XmlElementWrapper(name = "Discounts")
	@XmlElement(name = "Discount")
	private List<String> discount = new LinkedList<>();

	@XmlElement(name = "SelectedPaymentPlan")
	private SelectedPaymentPlan selectedPaymentPlan;

	@XmlElementWrapper(name = "PaymentPlans")
	@XmlElement(name = "PaymentPlan")
	private List<String> paymentPlan = new LinkedList<>();

	@XmlElement(name = "Premium")
	private Premium premium;

	@XmlElement(name = "Billing")
	private String billing;

	@XmlElementWrapper(name = "Drivers")
	@XmlElement(name = "Driver")
	private List<String> driver = new LinkedList<>();

	@XmlElementWrapper(name = "Vehicles")
	@XmlElement(name = "Vehicle")
	private List<Vehicle> vehicles = new ArrayList<>();

	public String getPolicyNumber() {
		return policyNumber;
	}

	public DocumentData setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
		return this;
	}

	public String getProductType() {
		return productType;
	}

	public DocumentData setProductType(String productType) {
		this.productType = productType;
		return this;
	}

	public String getState() {
		return state;
	}

	public DocumentData setState(String state) {
		this.state = state;
		return this;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public DocumentData setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
		return this;
	}

	public String getTransactionEffectiveDate() {
		return transactionEffectiveDate;
	}

	public DocumentData setTransactionEffectiveDate(String transactionEffectiveDate) {
		this.transactionEffectiveDate = transactionEffectiveDate;
		return this;
	}

	public String getTransactionReason() {
		return transactionReason;
	}

	public DocumentData setTransactionReason(String transactionReason) {
		this.transactionReason = transactionReason;
		return this;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public DocumentData setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
		return this;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public DocumentData setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
		return this;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public DocumentData setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
		return this;
	}

	public String getTermDurationInMonths() {
		return termDurationInMonths;
	}

	public DocumentData setTermDurationInMonths(String termDurationInMonths) {
		this.termDurationInMonths = termDurationInMonths;
		return this;
	}

	public String getNamedNonOwnerPolicy() {
		return namedNonOwnerPolicy;
	}

	public DocumentData setNamedNonOwnerPolicy(String namedNonOwnerPolicy) {
		this.namedNonOwnerPolicy = namedNonOwnerPolicy;
		return this;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public DocumentData setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
		return this;
	}

	public String getAaaInsuredSince() {
		return aaaInsuredSince;
	}

	public DocumentData setAaaInsuredSince(String aaaInsuredSince) {
		this.aaaInsuredSince = aaaInsuredSince;
		return this;
	}

	public String getRenewalCycle() {
		return renewalCycle;
	}

	public DocumentData setRenewalCycle(String renewalCycle) {
		this.renewalCycle = renewalCycle;
		return this;
	}

	public String getCancellationEffectiveDate() {
		return cancellationEffectiveDate;
	}

	public DocumentData setCancellationEffectiveDate(String cancellationEffectiveDate) {
		this.cancellationEffectiveDate = cancellationEffectiveDate;
		return this;
	}

	public String getInsured() {
		return insured;
	}

	public DocumentData setInsured(String insured) {
		this.insured = insured;
		return this;
	}

	public Agent getAgent() {
		return agent;
	}

	public DocumentData setAgent(Agent agent) {
		this.agent = agent;
		return this;
	}

	public Agency getAgency() {
		return agency;
	}

	public DocumentData setAgency(Agency agency) {
		this.agency = agency;
		return this;
	}

	public String getInsuranceScore() {
		return insuranceScore;
	}

	public DocumentData setInsuranceScore(String insuranceScore) {
		this.insuranceScore = insuranceScore;
		return this;
	}

	public List<String> getOtherOrPriorPolicy() {
		return otherOrPriorPolicy;
	}

	public DocumentData setOtherOrPriorPolicy(List<String> otherOrPriorPolicy) {
		this.otherOrPriorPolicy = otherOrPriorPolicy;
		return this;
	}

	public String getPriorCarrier() {
		return priorCarrier;
	}

	public DocumentData setPriorCarrier(String priorCarrier) {
		this.priorCarrier = priorCarrier;
		return this;
	}

	public List<String> getAlternateCoverage() {
		return alternateCoverage;
	}

	public DocumentData setAlternateCoverage(List<String> alternateCoverage) {
		this.alternateCoverage = alternateCoverage;
		return this;
	}

	public List<String> getEndorsement() {
		return endorsement;
	}

	public DocumentData setEndorsement(List<String> endorsement) {
		this.endorsement = endorsement;
		return this;
	}

	public List<String> getDiscount() {
		return discount;
	}

	public DocumentData setDiscount(List<String> discount) {
		this.discount = discount;
		return this;
	}

	public SelectedPaymentPlan getSelectedPaymentPlan() {
		return selectedPaymentPlan;
	}

	public DocumentData setSelectedPaymentPlan(SelectedPaymentPlan selectedPaymentPlan) {
		this.selectedPaymentPlan = selectedPaymentPlan;
		return this;
	}

	public List<String> getPaymentPlan() {
		return paymentPlan;
	}

	public DocumentData setPaymentPlan(List<String> paymentPlan) {
		this.paymentPlan = paymentPlan;
		return this;
	}

	public Premium getPremium() {
		return premium;
	}

	public DocumentData setPremium(Premium premium) {
		this.premium = premium;
		return this;
	}

	public String getBilling() {
		return billing;
	}

	public DocumentData setBilling(String billing) {
		this.billing = billing;
		return this;
	}

	public List<String> getDriver() {
		return driver;
	}

	public DocumentData setDriver(List<String> driver) {
		this.driver = driver;
		return this;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public DocumentData setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
		return this;
	}

	@Override
	public String toString() {
		return "DocumentData{" +
				"policyNumber='" + policyNumber + '\'' +
				", productType='" + productType + '\'' +
				", state='" + state + '\'' +
				", transactionCode='" + transactionCode + '\'' +
				", transactionEffectiveDate='" + transactionEffectiveDate + '\'' +
				", transactionReason='" + transactionReason + '\'' +
				", transactionDate='" + transactionDate + '\'' +
				", effectiveDate='" + effectiveDate + '\'' +
				", expiredDate='" + expiredDate + '\'' +
				", termDurationInMonths='" + termDurationInMonths + '\'' +
				", namedNonOwnerPolicy='" + namedNonOwnerPolicy + '\'' +
				", companyCode='" + companyCode + '\'' +
				", aaaInsuredSince='" + aaaInsuredSince + '\'' +
				", renewalCycle='" + renewalCycle + '\'' +
				", cancellationEffectiveDate='" + cancellationEffectiveDate + '\'' +
				", insured='" + insured + '\'' +
				", agent=" + agent +
				", agency=" + agency +
				", insuranceScore='" + insuranceScore + '\'' +
				", otherOrPriorPolicy=" + otherOrPriorPolicy +
				", priorCarrier='" + priorCarrier + '\'' +
				", alternateCoverage=" + alternateCoverage +
				", endorsement=" + endorsement +
				", discount=" + discount +
				", selectedPaymentPlan=" + selectedPaymentPlan +
				", paymentPlan=" + paymentPlan +
				", premium=" + premium +
				", billing='" + billing + '\'' +
				", driver=" + driver +
				", vehicle=" + vehicles +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DocumentData)) {
			return false;
		}
		DocumentData that = (DocumentData) o;
		return policyNumber.equals(that.policyNumber) &&
				productType.equals(that.productType) &&
				state.equals(that.state) &&
				transactionCode.equals(that.transactionCode) &&
				transactionEffectiveDate.equals(that.transactionEffectiveDate) &&
				transactionReason.equals(that.transactionReason) &&
				transactionDate.equals(that.transactionDate) &&
				effectiveDate.equals(that.effectiveDate) &&
				expiredDate.equals(that.expiredDate) &&
				termDurationInMonths.equals(that.termDurationInMonths) &&
				namedNonOwnerPolicy.equals(that.namedNonOwnerPolicy) &&
				companyCode.equals(that.companyCode) &&
				aaaInsuredSince.equals(that.aaaInsuredSince) &&
				renewalCycle.equals(that.renewalCycle) &&
				cancellationEffectiveDate.equals(that.cancellationEffectiveDate) &&
				insured.equals(that.insured) &&
				agent.equals(that.agent) &&
				agency.equals(that.agency) &&
				insuranceScore.equals(that.insuranceScore) &&
				otherOrPriorPolicy.equals(that.otherOrPriorPolicy) &&
				priorCarrier.equals(that.priorCarrier) &&
				alternateCoverage.equals(that.alternateCoverage) &&
				endorsement.equals(that.endorsement) &&
				discount.equals(that.discount) &&
				selectedPaymentPlan.equals(that.selectedPaymentPlan) &&
				paymentPlan.equals(that.paymentPlan) &&
				premium.equals(that.premium) &&
				billing.equals(that.billing) &&
				driver.equals(that.driver) &&
				vehicles.equals(that.vehicles);
	}

	@Override
	public int hashCode() {
		return Objects
				.hash(policyNumber, productType, state, transactionCode, transactionEffectiveDate, transactionReason, transactionDate, effectiveDate, expiredDate, termDurationInMonths, namedNonOwnerPolicy, companyCode, aaaInsuredSince, renewalCycle, cancellationEffectiveDate, insured, agent, agency, insuranceScore, otherOrPriorPolicy, priorCarrier, alternateCoverage, endorsement, discount, selectedPaymentPlan, paymentPlan, premium, billing, driver, vehicles);
	}

}
