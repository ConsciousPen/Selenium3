package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Premium {

	@XmlElement(name = "ActivityIncreasedRenewalPremiumInd")
	private String activityIncreasedRenewalPremiumInd;

	@XmlElement(name = "EndorsementPremiumChange")
	private String endorsementPremiumChange;

	@XmlElement(name = "TotalAmount")
	private String totalAmount;
	@XmlElement(name = "TotalPremium")
	private String totalPremium;
	@XmlElement(name = "TotalVehiclePremium")
	private String totalVehiclePremium;

	public String getEndorsementPremiumChange() {
		return endorsementPremiumChange;
	}

	public Premium setEndorsementPremiumChange(String endorsementPremiumChange) {
		this.endorsementPremiumChange = endorsementPremiumChange;
		return this;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public Premium setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public String getTotalPremium() {
		return totalPremium;
	}

	public Premium setTotalPremium(String totalPremium) {
		this.totalPremium = totalPremium;
		return this;
	}

	public String getTotalVehiclePremium() {
		return totalVehiclePremium;
	}

	public Premium setTotalVehiclePremium(String totalVehiclePremium) {
		this.totalVehiclePremium = totalVehiclePremium;
		return this;
	}

	public String getActivityIncreasedRenewalPremiumInd() {
		return activityIncreasedRenewalPremiumInd;
	}

	public Premium setActivityIncreasedRenewalPremiumInd(String activityIncreasedRenewalPremiumInd) {
		this.activityIncreasedRenewalPremiumInd = activityIncreasedRenewalPremiumInd;
		return this;
	}

	@Override
	public String toString() {
		return "Premium{" +
				"activityIncreasedRenewalPremiumInd='" + activityIncreasedRenewalPremiumInd + '\'' +
				", endorsementPremiumChange='" + endorsementPremiumChange + '\'' +
				", totalAmount='" + totalAmount + '\'' +
				", totalPremium='" + totalPremium + '\'' +
				", totalVehiclePremium='" + totalVehiclePremium + '\'' +
				'}';
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
		return activityIncreasedRenewalPremiumInd.equals(premium.activityIncreasedRenewalPremiumInd) &&
				endorsementPremiumChange.equals(premium.endorsementPremiumChange) &&
				totalAmount.equals(premium.totalAmount) &&
				totalPremium.equals(premium.totalPremium) &&
				totalVehiclePremium.equals(premium.totalVehiclePremium);
	}

	@Override
	public int hashCode() {
		return Objects.hash(activityIncreasedRenewalPremiumInd, endorsementPremiumChange, totalAmount, totalPremium, totalVehiclePremium);
	}
}
