package aaa.helpers.xml.models;

import aaa.main.enums.DocGenEnum;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class DocumentDataSections {
	@XmlElement(name = "DocumentDataSection")
	private List<DocumentDataSection> documentDataSections = new ArrayList<>();

	public List<DocumentDataSection> getDocumentDataSections() {
		return documentDataSections;
	}

	public void setDocumentDataSections(List<DocumentDataSection> documentDataSections) {
		this.documentDataSections = documentDataSections;
	}

	@Override
	public String toString() {
		return "DocumentDataSections{" +
				"documentDataSections=" + documentDataSections +
				'}';
	}
}
