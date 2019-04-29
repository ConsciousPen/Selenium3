
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoDistribution", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"deliveryStatus"
})
public class NoDistribution extends DistributionChannel {

	@XmlElement(name = "DeliveryStatus", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String deliveryStatus;

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

}
