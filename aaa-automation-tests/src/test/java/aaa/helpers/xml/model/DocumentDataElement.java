package aaa.helpers.xml.model;

import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class DocumentDataElement {
	@XmlElement(name = "DataElementChoice")
	private DataElementChoice dataElementChoice;

	@XmlElement(name = "Name")
	private String name;

	public DataElementChoice getDataElementChoice() {
		return dataElementChoice;
	}



	public String getName() {
		return name;
	}

	public DocumentDataElement setDataElementChoice(DataElementChoice dataElementChoice) {
		this.dataElementChoice = dataElementChoice;
		return this;
	}

	public DocumentDataElement setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String toString() {
		return "DocumentDataElement{" +
				"dataElementChoice=" + dataElementChoice +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}

		DocumentDataElement that = (DocumentDataElement) o;

		if (dataElementChoice != null ? !dataElementChoice.equals(that.dataElementChoice) : that.dataElementChoice != null) {
			return false;
		}
		return name != null ? name.equals(that.name) : that.name == null;
	}

	@Override
	public int hashCode() {
		int result = dataElementChoice != null ? dataElementChoice.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
