package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

	@XmlElement(name = "FirstName")
	private String firstName;

	@XmlElement(name = "LastName")
	private String lastName;

	@XmlElement(name = "Number")
	private String number;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Agent)) {
			return false;
		}
		Agent agent = (Agent) o;
		return firstName.equals(agent.firstName) &&
				lastName.equals(agent.lastName) &&
				number.equals(agent.number);
	}

	@Override
	public String toString() {
		return "Agent{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", number='" + number + '\'' +
				'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, number);
	}
}
