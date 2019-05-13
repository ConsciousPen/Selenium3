
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CentralPrintChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"deliveryStatus"
})
public class CentralPrintChannel extends DistributionChannel {

	@XmlElement(name = "DeliveryStatus", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, type = JAXBElement.class, required = false)
	protected JAXBElement<String> deliveryStatus;

	/**
	 * Gets the value of the deliveryStatus property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public JAXBElement<String> getDeliveryStatus() {
		return deliveryStatus;
	}

	/**
	 * Sets the value of the deliveryStatus property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setDeliveryStatus(JAXBElement<String> value) {
		this.deliveryStatus = value;
	}

}
