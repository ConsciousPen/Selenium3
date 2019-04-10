package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Agency {

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "Number")
	private String number;

	@XmlElement(name = "Phone")
	private Phone phone;

	@XmlElement(name = "Address")
	private Address address;

	public String getName() {
		return name;
	}

	public Agency setName(String name) {
		this.name = name;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Agency setNumber(String number) {
		this.number = number;
		return this;
	}

	public Phone getPhone() {
		return phone;
	}

	public Agency setPhone(Phone phone) {
		this.phone = phone;
		return this;
	}

	public Address getAddress() {
		return address;
	}

	public Agency setAddress(Address address) {
		this.address = address;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Agency)) {
			return false;
		}
		Agency agency = (Agency) o;
		return Objects.equals(name, agency.name) &&
				Objects.equals(number, agency.number) &&
				Objects.equals(phone, agency.phone) &&
				Objects.equals(address, agency.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, number, phone, address);
	}

	@Override
	public String toString() {
		return "Agency{" +
				"name='" + name + '\'' +
				", number='" + number + '\'' +
				", phone=" + phone +
				", address=" + address +
				'}';
	}
}
