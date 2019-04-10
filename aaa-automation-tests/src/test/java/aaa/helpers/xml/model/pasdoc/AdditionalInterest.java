package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalInterest {

	@XmlElement(name = "Address")
	private String address;

	@XmlElement(name = "IsAdditionalInsured")
	private Boolean isAdditionalInsured;

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "Type")
	private String type;

	public String getAddress() {
		return address;
	}

	public AdditionalInterest setAddress(String address) {
		this.address = address;
		return this;
	}

	public Boolean getAdditionalInsured() {
		return isAdditionalInsured;
	}

	public AdditionalInterest setAdditionalInsured(Boolean additionalInsured) {
		isAdditionalInsured = additionalInsured;
		return this;
	}

	public String getName() {
		return name;
	}

	public AdditionalInterest setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public AdditionalInterest setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AdditionalInterest)) {
			return false;
		}
		AdditionalInterest that = (AdditionalInterest) o;
		return Objects.equals(address, that.address) &&
				Objects.equals(isAdditionalInsured, that.isAdditionalInsured) &&
				Objects.equals(name, that.name) &&
				Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, isAdditionalInsured, name, type);
	}

	@Override
	public String toString() {
		return "AdditionalInterest{" +
				"address='" + address + '\'' +
				", isAdditionalInsured=" + isAdditionalInsured +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
