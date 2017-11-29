package aaa.helpers.xml.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class ArchiveData {
	@XmlElement(name = "SectionName")
	private String sectionName;

	@XmlElementWrapper(name = "DocumentDataElements")
	@XmlElement(name = "DocumentDataElement")
	private List<DocumentDataElement> documentDataElements = new ArrayList<>();

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public List<DocumentDataElement> getDocumentDataElements() {
		return documentDataElements;
	}

	public void setDocumentDataElements(List<DocumentDataElement> documentDataElements) {
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
