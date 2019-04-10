package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Document {

	@XmlElement(name = "AdditionalData")
	private AdditionalData additionalData;

	@XmlElement(name = "IsThirdPartyForm")
	private String isThirdPartyForm;

	@XmlElement(name = "Sequence")
	private String sequence;

	@XmlElement(name = "SignatureType")
	private String signatureType;

	@XmlElement(name = "TemplateId")
	private String templateId;

	@XmlElement(name = "TemplateVersion")
	private String templateVersion;

	@XmlElement(name = "XPathInfo")
	private String xPathInfo;

	public AdditionalData getAdditionalData() {
		return additionalData;
	}

	public Document setAdditionalData(AdditionalData additionalData) {
		this.additionalData = additionalData;
		return this;
	}

	public String getIsThirdPartyForm() {
		return isThirdPartyForm;
	}

	public Document setIsThirdPartyForm(String isThirdPartyForm) {
		this.isThirdPartyForm = isThirdPartyForm;
		return this;
	}

	public String getSequence() {
		return sequence;
	}

	public Document setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getSignatureType() {
		return signatureType;
	}

	public Document setSignatureType(String signatureType) {
		this.signatureType = signatureType;
		return this;
	}

	public String getTemplateId() {
		return templateId;
	}

	public Document setTemplateId(String templateId) {
		this.templateId = templateId;
		return this;
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	public Document setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
		return this;
	}

	public String getxPathInfo() {
		return xPathInfo;
	}

	public Document setxPathInfo(String xPathInfo) {
		this.xPathInfo = xPathInfo;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Document)) {
			return false;
		}
		Document document = (Document) o;
		return Objects.equals(additionalData, document.additionalData) &&
				Objects.equals(isThirdPartyForm, document.isThirdPartyForm) &&
				Objects.equals(sequence, document.sequence) &&
				Objects.equals(signatureType, document.signatureType) &&
				Objects.equals(templateId, document.templateId) &&
				Objects.equals(templateVersion, document.templateVersion) &&
				Objects.equals(xPathInfo, document.xPathInfo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalData, isThirdPartyForm, sequence, signatureType, templateId, templateVersion, xPathInfo);
	}

	@Override
	public String toString() {
		return "Document{" +
				"additionalData=" + additionalData +
				", isThirdPartyForm='" + isThirdPartyForm + '\'' +
				", sequence='" + sequence + '\'' +
				", signatureType='" + signatureType + '\'' +
				", templateId='" + templateId + '\'' +
				", templateVersion='" + templateVersion + '\'' +
				", xPathInfo='" + xPathInfo + '\'' +
				'}';
	}
}
