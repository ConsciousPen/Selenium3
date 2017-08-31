package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1")
public class ArchiveData {
	@XmlElement(name = "SectionName")
	private String sectionName;

	@XmlElement(name = "DocumentDataElements")
	private DocumentDataElements documentDataElements;

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public DocumentDataElements getDocumentDataElements() {
		return documentDataElements;
	}

	public void setDocumentDataElements(DocumentDataElements documentDataElements) {
		this.documentDataElements = documentDataElements;
	}

	@Override
	public String toString() {
		return "ArchiveData{" +
				"sectionName='" + sectionName + '\'' +
				", documentDataElements=" + documentDataElements +
				'}';
	}
}
