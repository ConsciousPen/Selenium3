package aaa.helpers.xml.model.pasdoc;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalData {

	@XmlElement(name = "DataElement")
	private List<DataElement> dataElement = new LinkedList<>();
	
	public List<DataElement> getDataElement() {
		return dataElement;
	}

	public AdditionalData setDataElement(List<DataElement> dataElement) {
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
