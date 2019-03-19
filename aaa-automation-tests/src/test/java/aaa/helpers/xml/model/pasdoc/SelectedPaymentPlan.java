package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SelectedPaymentPlan {

	@XmlElement(name = "DownPayment")
	private String downPayment;
	@XmlElement(name = "DownPaymentPercent")
	private String downPaymentPercent;
	@XmlElement(name = "InstallmentAmount")
	private String installmentAmount;
	@XmlElement(name = "InstallmentNumber")
	private String installmentNumber;
	@XmlElement(name = "PlanType")
	private String planType;
	@XmlElement(name = "TermPremium")
	private String termPremium;

	public String getDownPayment() {
		return downPayment;
	}

	public SelectedPaymentPlan setDownPayment(String downPayment) {
		this.downPayment = downPayment;
		return this;
	}

	public String getDownPaymentPercent() {
		return downPaymentPercent;
	}

	public SelectedPaymentPlan setDownPaymentPercent(String downPaymentPercent) {
		this.downPaymentPercent = downPaymentPercent;
		return this;
	}

	public String getInstallmentAmount() {
		return installmentAmount;
	}

	public SelectedPaymentPlan setInstallmentAmount(String installmentAmount) {
		this.installmentAmount = installmentAmount;
		return this;
	}

	public String getInstallmentNumber() {
		return installmentNumber;
	}

	public SelectedPaymentPlan setInstallmentNumber(String installmentNumber) {
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

	public String getTermPremium() {
		return termPremium;
	}

	public SelectedPaymentPlan setTermPremium(String termPremium) {
		this.termPremium = termPremium;
		return this;
	}

	@Override
	public String toString() {
		return "SelectedPaymentPlan{" +
				"downPayment='" + downPayment + '\'' +
				", downPaymentPercent='" + downPaymentPercent + '\'' +
				", installmentAmount='" + installmentAmount + '\'' +
				", installmentNumber='" + installmentNumber + '\'' +
				", planType='" + planType + '\'' +
				", termPremium='" + termPremium + '\'' +
				'}';
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
		return downPayment.equals(that.downPayment) &&
				downPaymentPercent.equals(that.downPaymentPercent) &&
				installmentAmount.equals(that.installmentAmount) &&
				installmentNumber.equals(that.installmentNumber) &&
				planType.equals(that.planType) &&
				termPremium.equals(that.termPremium);
	}

	@Override
	public int hashCode() {
		return Objects.hash(downPayment, downPaymentPercent, installmentAmount, installmentNumber, planType, termPremium);
	}

}
