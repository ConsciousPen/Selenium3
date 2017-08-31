package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1")
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
