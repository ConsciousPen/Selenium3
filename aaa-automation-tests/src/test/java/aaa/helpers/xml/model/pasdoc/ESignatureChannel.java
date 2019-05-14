
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESignatureChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"deliveryStatus",
		"esignatureDetails"
})
public class ESignatureChannel  extends DistributionChannel {

	@XmlElement(name = "DeliveryStatus", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String deliveryStatus;
	@XmlElement(name = "EsignatureDetails", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, type = EsignatureDetails.class, required = false)
	protected EsignatureDetails esignatureDetails;

	/**
	 * Gets the value of the deliveryStatus property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	/**
	 * Sets the value of the deliveryStatus property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }{@code <}{@link String }{@code >}
	 *
	 */
	public void setDeliveryStatus(String value) {
		this.deliveryStatus = value;
	}

	/**
	 * Gets the value of the esignatureDetails property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link EsignatureDetails }{@code >}
	 *
	 */
	public EsignatureDetails getEsignatureDetails() {
		return esignatureDetails;
	}

	/**
	 * Sets the value of the esignatureDetails property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link EsignatureDetails }{@code >}
	 *
	 */
	public void setEsignatureDetails(EsignatureDetails value) {
		this.esignatureDetails = value;
	}

}
