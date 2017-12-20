package aaa.helpers.xml.model;

import java.util.ArrayList;
import java.util.List;
import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class DocumentDataSection {
	@XmlElement(name = "SectionName")
	private String sectionName;

	@XmlElementWrapper(name = "DocumentDataElements")
	@XmlElement(name = "DocumentDataElement")
	private List<DocumentDataElement> documentDataElements = new ArrayList<>();

	public String getSectionName() {
		return sectionName;
	}

	public List<DocumentDataElement> getDocumentDataElements() {
		return documentDataElements;
	}

	public DocumentDataSection setSectionName(String sectionName) {
		this.sectionName = sectionName;
		return this;
	}

	public DocumentDataSection setDocumentDataElements(List<DocumentDataElement> documentDataElements) {
		this.documentDataElements = documentDataElements;
		return this;
	}

	@Override
	public String toString() {
		return "DocumentDataSection{" +
				"sectionName='" + sectionName + '\'' +
				", documentDataElements=" + documentDataElements +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DocumentDataSection that = (DocumentDataSection) o;

		if (sectionName != null ? !sectionName.equals(that.sectionName) : that.sectionName != null) {
			return false;
		}
		return documentDataElements != null ? documentDataElements.equals(that.documentDataElements) : that.documentDataElements == null;
	}

	@Override
	public int hashCode() {
		int result = sectionName != null ? sectionName.hashCode() : 0;
		result = 31 * result + (documentDataElements != null ? documentDataElements.hashCode() : 0);
		return result;
	}
}
