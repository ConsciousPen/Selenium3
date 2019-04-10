package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataElement {

	@XmlElement(name = "DataType")
	private String dataType;

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

	public String getDataType() {
		return dataType;
	}

	public DataElement setDataType(String dataType) {
		this.dataType = dataType;
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
		DataElement that = (DataElement) o;
		return Objects.equals(dataType, that.dataType) &&
				Objects.equals(name, that.name) &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataType, name, value);
	}

	@Override
	public String toString() {
		return "DataElement{" +
				"dataType='" + dataType + '\'' +
				", name='" + name + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
