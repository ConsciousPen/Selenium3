package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class SelectedPaymentPlan {

	@XmlElement(name = "DownPayment")
	private Double downPayment;

	@XmlElement(name = "DownPaymentPercent")
	private Double downPaymentPercent;

	@XmlElement(name = "InstallmentAmount")
	private Double installmentAmount;

	@XmlElement(name = "InstallmentNumber")
	private Integer installmentNumber;

	@XmlElement(name = "PlanType")
	private String planType;

	@XmlElement(name = "TermPremium")
	private Double termPremium;

	@XmlElement(name = "Restricted")
	private Boolean restricted;

	public Double getDownPayment() {
		return downPayment;
	}

	public SelectedPaymentPlan setDownPayment(Double downPayment) {
		this.downPayment = downPayment;
		return this;
	}

	public Double getDownPaymentPercent() {
		return downPaymentPercent;
	}

	public SelectedPaymentPlan setDownPaymentPercent(Double downPaymentPercent) {
		this.downPaymentPercent = downPaymentPercent;
		return this;
	}

	public Double getInstallmentAmount() {
		return installmentAmount;
	}

	public SelectedPaymentPlan setInstallmentAmount(Double installmentAmount) {
		this.installmentAmount = installmentAmount;
		return this;
	}

	public Integer getInstallmentNumber() {
		return installmentNumber;
	}

	public SelectedPaymentPlan setInstallmentNumber(Integer installmentNumber) {
		this.installmentNumber = installmentNumber;
		return this;
	}

	public String getPlanType() {
		return planType;
	}

	public SelectedPaymentPlan setPlanType(String planType) {
		this.planType = planType;
		return this;
	}

	public Double getTermPremium() {
		return termPremium;
	}

	public SelectedPaymentPlan setTermPremium(Double termPremium) {
		this.termPremium = termPremium;
		return this;
	}

	public Boolean getRestricted() {
		return restricted;
	}

	public SelectedPaymentPlan setRestricted(Boolean restricted) {
		this.restricted = restricted;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SelectedPaymentPlan)) {
			return false;
		}
		SelectedPaymentPlan that = (SelectedPaymentPlan) o;
		return Objects.equals(downPayment, that.downPayment) &&
				Objects.equals(downPaymentPercent, that.downPaymentPercent) &&
				Objects.equals(installmentAmount, that.installmentAmount) &&
				Objects.equals(installmentNumber, that.installmentNumber) &&
				Objects.equals(planType, that.planType) &&
				Objects.equals(termPremium, that.termPremium) &&
				Objects.equals(restricted, that.restricted);
	}

	@Override
	public int hashCode() {
		return Objects.hash(downPayment, downPaymentPercent, installmentAmount, installmentNumber, planType, termPremium, restricted);
	}

	@Override
	public String toString() {
		return "SelectedPaymentPlan{" +
				"downPayment=" + downPayment +
				", downPaymentPercent=" + downPaymentPercent +
				", installmentAmount=" + installmentAmount +
				", installmentNumber=" + installmentNumber +
				", planType='" + planType + '\'' +
				", termPremium=" + termPremium +
				", restricted=" + restricted +
				'}';
	}
}
