package aaa.soap.batchJobService.endpoint;

import javax.xml.bind.annotation.*;
import aaa.rest.IModel;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pollingInterval" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"status",
		"pollingInterval"
})
@XmlRootElement(name = "JobGroupStartResponse")
public class JobGroupStartResponse implements IModel {

	@XmlElement(required = true)
	protected String status;
	protected long pollingInterval;

	/**
	 * Gets the value of the status property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the value of the status property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setStatus(String value) {
		this.status = value;
	}

	/**
	 * Gets the value of the pollingInterval property.
	 *
	 */
	public long getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Sets the value of the pollingInterval property.
	 *
	 */
	public void setPollingInterval(long value) {
		this.pollingInterval = value;
	}

}
