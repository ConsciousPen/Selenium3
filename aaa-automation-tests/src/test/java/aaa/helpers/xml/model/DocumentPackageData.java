package aaa.helpers.xml.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class DocumentPackageData {
	@XmlElement(name = "DocumentDataSection")
	private List<DocumentDataSection> documentDataSection = new ArrayList<>();

	public List<DocumentDataSection> getDocumentDataSection() {
		return documentDataSection;
	}

	public void setDocumentDataSection(List<DocumentDataSection> documentDataSection) {
		this.documentDataSection = documentDataSection;
	}

	@Override
	public String toString() {
		return "DocumentPackageData{" +
				"documentDataSection=" + documentDataSection +
				'}';
	}
}
