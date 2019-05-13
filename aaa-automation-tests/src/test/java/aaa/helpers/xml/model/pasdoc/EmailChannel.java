
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmailChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"emailTemplateId",
		"encryptMail",
		"fromAddress",
		"isAttachmentRequired",
		"isEmailTemplateIncluded",
		"mailBodyType",
		"message",
		"subject"
})
public class EmailChannel extends DistributionChannel {

	@XmlElement(name = "EmailTemplateId", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, type = JAXBElement.class, required = false)
	protected JAXBElement<String> emailTemplateId;
	@XmlElement(name = "EncryptMail", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	protected Boolean encryptMail;
	@XmlElement(name = "FromAddress", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
	protected String fromAddress;
	@XmlElement(name = "IsAttachmentRequired", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	protected Boolean isAttachmentRequired;
	@XmlElement(name = "IsEmailTemplateIncluded", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	protected Boolean isEmailTemplateIncluded;
	@XmlElement(name = "MailBodyType", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	@XmlSchemaType(name = "string")
	protected String mailBodyType;
	@XmlElement(name = "Message", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String message;
	@XmlElement(name = "Subject", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
	protected String subject;

	/**
	 * Gets the value of the emailTemplateId property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public JAXBElement<String> getEmailTemplateId() {
		return emailTemplateId;
	}

	/**
	 * Sets the value of the emailTemplateId property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setEmailTemplateId(JAXBElement<String> value) {
		this.emailTemplateId = value;
	}

	/**
	 * Gets the value of the encryptMail property.
	 *
	 * @return
	 *     possible object is
	 *     {@link Boolean }
	 *
	 */
	public Boolean isEncryptMail() {
		return encryptMail;
	}

	/**
	 * Sets the value of the encryptMail property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link Boolean }
	 *
	 */
	public void setEncryptMail(Boolean value) {
		this.encryptMail = value;
	}

	/**
	 * Gets the value of the fromAddress property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * Sets the value of the fromAddress property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setFromAddress(String value) {
		this.fromAddress = value;
	}

	/**
	 * Gets the value of the isAttachmentRequired property.
	 *
	 * @return
	 *     possible object is
	 *     {@link Boolean }
	 *
	 */
	public Boolean isIsAttachmentRequired() {
		return isAttachmentRequired;
	}

	/**
	 * Sets the value of the isAttachmentRequired property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link Boolean }
	 *
	 */
	public void setIsAttachmentRequired(Boolean value) {
		this.isAttachmentRequired = value;
	}

	/**
	 * Gets the value of the isEmailTemplateIncluded property.
	 *
	 * @return
	 *     possible object is
	 *     {@link Boolean }
	 *
	 */
	public Boolean isIsEmailTemplateIncluded() {
		return isEmailTemplateIncluded;
	}

	/**
	 * Sets the value of the isEmailTemplateIncluded property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link Boolean }
	 *
	 */
	public void setIsEmailTemplateIncluded(Boolean value) {
		this.isEmailTemplateIncluded = value;
	}

	/**
	 * Gets the value of the mailBodyType property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getMailBodyType() {
		return mailBodyType;
	}

	/**
	 * Sets the value of the mailBodyType property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setMailBodyType(String value) {
		this.mailBodyType = value;
	}

	/**
	 * Gets the value of the message property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the value of the message property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setMessage(String value) {
		this.message = value;
	}

	/**
	 * Gets the value of the subject property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the value of the subject property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setSubject(String value) {
		this.subject = value;
	}

}
