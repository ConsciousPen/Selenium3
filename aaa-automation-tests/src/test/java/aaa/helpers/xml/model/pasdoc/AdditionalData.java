package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalData {

	@XmlElement(name = "DataElement")
	private DataElement dataElement;

	public DataElement getDataElement() {
		return dataElement;
	}

	public AdditionalData setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
		return this;
	}

	@Override
	public String toString() {
		return "AdditionalData{" +
				"dataElement=" + dataElement +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AdditionalData)) {
			return false;
		}
		AdditionalData that = (AdditionalData) o;
		return Objects.equals(dataElement, that.dataElement);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataElement);
	}
}
