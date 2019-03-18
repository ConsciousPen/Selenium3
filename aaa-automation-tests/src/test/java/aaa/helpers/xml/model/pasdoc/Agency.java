package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Agency {

	@XmlElement(name = "Name")
	private String name;
	@XmlElement(name = "Number")
	private String number;
	@XmlElement(name = "Phone")
	private String lastName;

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

	public String getLastName() {
		return lastName;
	}

	public Agency setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	@Override
	public String toString() {
		return "Agency{" +
				"name='" + name + '\'' +
				", number='" + number + '\'' +
				", lastName='" + lastName + '\'' +
				'}';
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
		return name.equals(agency.name) &&
				number.equals(agency.number) &&
				lastName.equals(agency.lastName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, number, lastName);
	}

}
