package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Premium {

	@XmlElement(name = "ActivityIncreasedRenewalPremiumInd")
	private Boolean activityIncreasedRenewalPremiumInd;

	@XmlElement(name = "EndorsementPremiumChange")
	private Double endorsementPremiumChange;

	@XmlElement(name = "PayInFullAmount")
	private Double payInFullAmount;

	@XmlElement(name = "TotalPremium")
	private Double totalPremium;

	@XmlElement(name = "TotalVehiclePremium")
	private Double totalVehiclePremium;

	@XmlElement(name = "TotalAmount")
	private Double totalAmount;

	public Boolean getActivityIncreasedRenewalPremiumInd() {
		return activityIncreasedRenewalPremiumInd;
	}

	public Premium setActivityIncreasedRenewalPremiumInd(Boolean activityIncreasedRenewalPremiumInd) {
		this.activityIncreasedRenewalPremiumInd = activityIncreasedRenewalPremiumInd;
		return this;
	}

	public Double getEndorsementPremiumChange() {
		return endorsementPremiumChange;
	}

	public Premium setEndorsementPremiumChange(Double endorsementPremiumChange) {
		this.endorsementPremiumChange = endorsementPremiumChange;
		return this;
	}

	public Double getPayInFullAmount() {
		return payInFullAmount;
	}

	public Premium setPayInFullAmount(Double payInFullAmount) {
		this.payInFullAmount = payInFullAmount;
		return this;
	}

	public Double getTotalPremium() {
		return totalPremium;
	}

	public Premium setTotalPremium(Double totalPremium) {
		this.totalPremium = totalPremium;
		return this;
	}

	public Double getTotalVehiclePremium() {
		return totalVehiclePremium;
	}

	public Premium setTotalVehiclePremium(Double totalVehiclePremium) {
		this.totalVehiclePremium = totalVehiclePremium;
		return this;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public Premium setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Premium)) {
			return false;
		}
		Premium premium = (Premium) o;
		return Objects.equals(activityIncreasedRenewalPremiumInd, premium.activityIncreasedRenewalPremiumInd) &&
				Objects.equals(endorsementPremiumChange, premium.endorsementPremiumChange) &&
				Objects.equals(payInFullAmount, premium.payInFullAmount) &&
				Objects.equals(totalPremium, premium.totalPremium) &&
				Objects.equals(totalVehiclePremium, premium.totalVehiclePremium) &&
				Objects.equals(totalAmount, premium.totalAmount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(activityIncreasedRenewalPremiumInd, endorsementPremiumChange, payInFullAmount, totalPremium, totalVehiclePremium, totalAmount);
	}

	@Override
	public String toString() {
		return "Premium{" +
				"activityIncreasedRenewalPremiumInd=" + activityIncreasedRenewalPremiumInd +
				", endorsementPremiumChange=" + endorsementPremiumChange +
				", payInFullAmount=" + payInFullAmount +
				", totalPremium=" + totalPremium +
				", totalVehiclePremium=" + totalVehiclePremium +
				", totalAmount=" + totalAmount +
				'}';
	}
}
