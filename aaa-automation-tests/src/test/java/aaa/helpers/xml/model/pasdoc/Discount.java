package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Discount {

	@XmlElement(name = "Code")
	private String code;

	@XmlElement(name = "Description")
	private String description;

	@XmlElement(name = "Percentage")
	private String percentage;

	public String getCode() {
		return code;
	}

	public Discount setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Discount setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getPercentage() {
		return percentage;
	}

	public Discount setPercentage(String percentage) {
		this.percentage = percentage;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Discount)) {
			return false;
		}
		Discount discount = (Discount) o;
		return Objects.equals(code, discount.code) &&
				Objects.equals(description, discount.description) &&
				Objects.equals(percentage, discount.percentage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, description, percentage);
	}

	@Override
	public String toString() {
		return "Discount{" +
				"code='" + code + '\'' +
				", description='" + description + '\'' +
				", percentage='" + percentage + '\'' +
				'}';
	}
}
