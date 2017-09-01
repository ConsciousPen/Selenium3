package aaa.helpers.xml.models;

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

	public void setDataElementChoice(DataElementChoice dataElementChoice) {
		this.dataElementChoice = dataElementChoice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DocumentDataElement{" +
				"dataElementChoice=" + dataElementChoice +
				", name='" + name + '\'' +
				'}';
	}
}
