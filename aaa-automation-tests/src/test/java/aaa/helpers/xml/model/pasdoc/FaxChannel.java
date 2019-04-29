
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FaxChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"faxCoverPageTemplateId",
		"faxRecipient",
		"faxSender",
		"isFaxCoverPageIncluded"
})
public class FaxChannel extends DistributionChannel {

	@XmlElement(name = "FaxCoverPageTemplateId", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String faxCoverPageTemplateId;
	@XmlElement(name = "FaxRecipient", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
	protected String faxRecipient;
	@XmlElement(name = "FaxSender", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
	protected String faxSender;
	@XmlElement(name = "IsFaxCoverPageIncluded", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	protected Boolean isFaxCoverPageIncluded;

	/**
	 * Gets the value of the faxCoverPageTemplateId property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public String getFaxCoverPageTemplateId() {
		return faxCoverPageTemplateId;
	}

	/**
	 * Sets the value of the faxCoverPageTemplateId property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setFaxCoverPageTemplateId(String value) {
		this.faxCoverPageTemplateId = value;
	}

	/**
	 * Gets the value of the faxRecipient property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getFaxRecipient() {
		return faxRecipient;
	}

	/**
	 * Sets the value of the faxRecipient property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setFaxRecipient(String value) {
		this.faxRecipient = value;
	}

	/**
	 * Gets the value of the faxSender property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getFaxSender() {
		return faxSender;
	}

	/**
	 * Sets the value of the faxSender property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setFaxSender(String value) {
		this.faxSender = value;
	}

	/**
	 * Gets the value of the isFaxCoverPageIncluded property.
	 *
	 * @return
	 *     possible object is
	 *     {@link Boolean }
	 *
	 */
	public Boolean isIsFaxCoverPageIncluded() {
		return isFaxCoverPageIncluded;
	}

	/**
	 * Sets the value of the isFaxCoverPageIncluded property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link Boolean }
	 *
	 */
	public void setIsFaxCoverPageIncluded(Boolean value) {
		this.isFaxCoverPageIncluded = value;
	}

}
