package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistributionChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
		"failureNotificationSettings"
})
@XmlSeeAlso({
		ESignatureChannel.class,
		CentralPrintChannel.class,
		LocalPrintChannel.class,
		PreviewChannel.class,
		EmailChannel.class,
		FaxChannel.class,
		NoDistribution.class,
		PreferredChannel.class,
		NetworkPrintChannel.class
})
public class DistributionChannel {

	@XmlElement(name = "FailureNotificationSettings", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
	protected String failureNotificationSettings;

	/**
	 * Gets the value of the failureNotificationSettings property.
	 *
	 * @return
	 *     possible object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public String getFailureNotificationSettings() {
		return failureNotificationSettings;
	}

	/**
	 * Sets the value of the failureNotificationSettings property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 */
	public void setFailureNotificationSettings(String value) {
		this.failureNotificationSettings = value;
	}

}
