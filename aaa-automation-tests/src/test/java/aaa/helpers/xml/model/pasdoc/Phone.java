package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Phone {

	@XmlElement(name = "PhoneNumber")
	private String phoneNumber;

	@XmlElement(name = "PhoneType")
	private String phoneType;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Phone setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public Phone setPhoneType(String phoneType) {
		this.phoneType = phoneType;
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
		Phone phone = (Phone) o;
		return Objects.equals(phoneNumber, phone.phoneNumber) &&
				Objects.equals(phoneType, phone.phoneType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(phoneNumber, phoneType);
	}

	@Override
	public String toString() {
		return "Phone{" +
				"phoneNumber='" + phoneNumber + '\'' +
				", phoneType='" + phoneType + '\'' +
				'}';
	}
}
