package aaa.helpers.xml.models;

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

	@XmlElement(name = "Sequence")
	private String sequence;

	@XmlElement(name = "TemplateId")
	private String templateId;

	@XmlElement(name = "XPathInfo")
	private String xPathInfo;

	public List<DocumentDataSection> getDocumentDataSections() {
		return documentDataSections;
	}

	public void setDocumentDataSections(List<DocumentDataSection> documentDataSections) {
		this.documentDataSections = documentDataSections;
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

	@Override
	public String toString() {
		return "Document{" +
				"documentDataSections=" + documentDataSections +
				", sequence='" + sequence + '\'' +
				", templateId='" + templateId + '\'' +
				", xPathInfo='" + xPathInfo + '\'' +
				'}';
	}
}
