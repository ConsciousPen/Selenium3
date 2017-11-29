package aaa.helpers.xml.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
public class Document {
	@XmlElementWrapper(name = "DocumentDataSections")
	@XmlElement(name = "DocumentDataSection", namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
	private List<DocumentDataSection> documentDataSections = new ArrayList<>();

	@XmlElement(name = "DocumentsData")
	private String documentsData;

	@XmlElement(name = "Sequence")
	private String sequence;

	@XmlElement(name = "TemplateId")
	private String templateId;

	@XmlElement(name = "XPathInfo")
	private String xPathInfo;

	@XmlElement(name = "eSignatureDocument")
	private String eSignatureDocument;

	public List<DocumentDataSection> getDocumentDataSections() {
		return documentDataSections;
	}

	public void setDocumentDataSections(List<DocumentDataSection> documentDataSections) {
		this.documentDataSections = documentDataSections;
	}

	public String getDocumentsData() {
		return documentsData;
	}

	public void setDocumentsData(String documentsData) {
		this.documentsData = documentsData;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getxPathInfo() {
		return xPathInfo;
	}

	public void setxPathInfo(String xPathInfo) {
		this.xPathInfo = xPathInfo;
	}

	public String geteSignatureDocument() {
		return eSignatureDocument;
	}

	public void seteSignatureDocument(String eSignatureDocument) {
		this.eSignatureDocument = eSignatureDocument;
	}

	@Override
	public String toString() {
		return "Document{" +
				"documentDataSections=" + documentDataSections +
				", documentsData='" + documentsData + '\'' +
				", sequence='" + sequence + '\'' +
				", templateId='" + templateId + '\'' +
				", xPathInfo='" + xPathInfo + '\'' +
				", eSignatureDocument='" + eSignatureDocument + '\'' +
				'}';
	}
}
