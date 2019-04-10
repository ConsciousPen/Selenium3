package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Endorsement {

	@XmlElementWrapper(name = "AdditionalData")
	@XmlElement(name = "AdditionalData")
	private List<DataElement> additionalData = new LinkedList<>();

	@XmlElement(name = "FormNumber")
	private String formNumber;

	@XmlElement(name = "Limits")
	private Boolean limits;

	@XmlElement(name = "Premium")
	private Double premium;

	public List<DataElement> getAdditionalData() {
		return additionalData;
	}

	public Endorsement setAdditionalData(List<DataElement> additionalData) {
		this.additionalData = additionalData;
		return this;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public Endorsement setFormNumber(String formNumber) {
		this.formNumber = formNumber;
		return this;
	}

	public Boolean getLimits() {
		return limits;
	}

	public Endorsement setLimits(Boolean limits) {
		this.limits = limits;
		return this;
	}

	public Double getPremium() {
		return premium;
	}

	public Endorsement setPremium(Double premium) {
		this.premium = premium;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Endorsement that = (Endorsement) o;
		return Objects.equals(additionalData, that.additionalData) &&
				Objects.equals(formNumber, that.formNumber) &&
				Objects.equals(limits, that.limits) &&
				Objects.equals(premium, that.premium);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalData, formNumber, limits, premium);
	}

	@Override
	public String toString() {
		return "Endorsement{" +
				"additionalData=" + additionalData +
				", formNumber='" + formNumber + '\'' +
				", limits=" + limits +
				", premium=" + premium +
				'}';
	}
}
