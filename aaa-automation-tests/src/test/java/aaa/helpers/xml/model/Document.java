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


	public String getDocumentsData() {
		return documentsData;
	}


	public String getSequence() {
		return sequence;
	}

	public String getTemplateId() {
		return templateId;
	}

	public String getxPathInfo() {
		return xPathInfo;
	}

	public String geteSignatureDocument() {
		return eSignatureDocument;
	}

	public Document setDocumentDataSections(List<DocumentDataSection> documentDataSections) {
		this.documentDataSections = documentDataSections;
		return this;
	}

	public Document setDocumentsData(String documentsData) {
		this.documentsData = documentsData;
		return this;
	}

	public Document setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}

	public Document setTemplateId(String templateId) {
		this.templateId = templateId;
		return this;
	}

	public Document setxPathInfo(String xPathInfo) {
		this.xPathInfo = xPathInfo;
		return this;
	}

	public Document seteSignatureDocument(String eSignatureDocument) {
		this.eSignatureDocument = eSignatureDocument;
		return this;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Document document = (Document) o;

		if (documentDataSections != null ? !documentDataSections.equals(document.documentDataSections) : document.documentDataSections != null) {
			return false;
		}
		if (documentsData != null ? !documentsData.equals(document.documentsData) : document.documentsData != null) {
			return false;
		}
		if (sequence != null ? !sequence.equals(document.sequence) : document.sequence != null) {
			return false;
		}
		if (templateId != null ? !templateId.equals(document.templateId) : document.templateId != null) {
			return false;
		}
		if (xPathInfo != null ? !xPathInfo.equals(document.xPathInfo) : document.xPathInfo != null) {
			return false;
		}
		return eSignatureDocument != null ? eSignatureDocument.equals(document.eSignatureDocument) : document.eSignatureDocument == null;
	}

	@Override
	public int hashCode() {
		int result = documentDataSections != null ? documentDataSections.hashCode() : 0;
		result = 31 * result + (documentsData != null ? documentsData.hashCode() : 0);
		result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
		result = 31 * result + (templateId != null ? templateId.hashCode() : 0);
		result = 31 * result + (xPathInfo != null ? xPathInfo.hashCode() : 0);
		result = 31 * result + (eSignatureDocument != null ? eSignatureDocument.hashCode() : 0);
		return result;
	}
}
