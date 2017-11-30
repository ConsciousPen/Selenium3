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
public class DocumentPackageData {
	@XmlElement(name = "DocumentDataSection")
	private List<DocumentDataSection> documentDataSection = new ArrayList<>();

	public List<DocumentDataSection> getDocumentDataSection() {
		return documentDataSection;
	}

	public DocumentPackageData setDocumentDataSection(List<DocumentDataSection> documentDataSection) {
		this.documentDataSection = documentDataSection;
		return this;
	}

	@Override
	public String toString() {
		return "DocumentPackageData{" +
				"documentDataSection=" + documentDataSection +
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

		DocumentPackageData that = (DocumentPackageData) o;

		return documentDataSection != null ? documentDataSection.equals(that.documentDataSection) : that.documentDataSection == null;
	}

	@Override
	public int hashCode() {
		return documentDataSection != null ? documentDataSection.hashCode() : 0;
	}
}
