package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataElement {

	@XmlElement(name = "Name")
	private String name;
	@XmlElement(name = "Value")
	private String value;

	public String getName() {
		return name;
	}

	public DataElement setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public DataElement setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DataElement)) {
			return false;
		}
		DataElement that = (DataElement) o;
		return name.equals(that.name) &&
				value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	public String toString() {
		return "DataElement{" +
				"name='" + name + '\'' +
				", value='" + value + '\'' +
				'}';
	}

}
