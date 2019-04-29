
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkPrintChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"deliveryStatus",
		"printerIdentification"
})
public class NetworkPrintChannel
		extends DistributionChannel {

	@XmlElement(name = "DeliveryStatus", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String deliveryStatus;
	@XmlElement(name = "PrinterIdentification", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
	protected String printerIdentification;

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
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setDeliveryStatus(String value) {
		this.deliveryStatus = value;
	}

	/**
	 * Gets the value of the printerIdentification property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getPrinterIdentification() {
		return printerIdentification;
	}

	/**
	 * Sets the value of the printerIdentification property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setPrinterIdentification(String value) {
		this.printerIdentification = value;
	}

}
