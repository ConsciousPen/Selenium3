package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

	@XmlElement(name = "FirstName")
	private String firstName;

	@XmlElement(name = "LastName")
	private String lastName;

	@XmlElement(name = "Number")
	private String number;

	@XmlElement(name = "MiddleName")
	private String middleName;

	@XmlElement(name = "EmailAddress")
	private String emailAddress;

	@XmlElement(name = "Phone")
	private Phone phone;

	public String getFirstName() {
		return firstName;
	}

	public Agent setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public Agent setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Agent setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getMiddleName() {
		return middleName;
	}

	public Agent setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public Agent setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
	}

	public Phone getPhone() {
		return phone;
	}

	public Agent setPhone(Phone phone) {
		this.phone = phone;
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
		Agent agent = (Agent) o;
		return Objects.equals(firstName, agent.firstName) &&
				Objects.equals(lastName, agent.lastName) &&
				Objects.equals(number, agent.number) &&
				Objects.equals(middleName, agent.middleName) &&
				Objects.equals(emailAddress, agent.emailAddress) &&
				Objects.equals(phone, agent.phone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, number, middleName, emailAddress, phone);
	}

	@Override
	public String toString() {
		return "Agent{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", number='" + number + '\'' +
				", middleName='" + middleName + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", phone=" + phone +
				'}';
	}
}
