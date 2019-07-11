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
 *         &lt;element name="jobGroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
		"jobGroupName",
		"requestID"
})
@XmlRootElement(name = "JobGroupStartRequest")
public class JobGroupStartRequest implements IModel {

	@XmlElement(required = true)
	protected String jobGroupName;
	@XmlElement(required = true)
	protected String requestID;

	/**
	 * Gets the value of the jobGroupName property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getJobGroupName() {
		return jobGroupName;
	}

	/**
	 * Sets the value of the jobGroupName property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setJobGroupName(String value) {
		this.jobGroupName = value;
	}

	/**
	 * Gets the value of the requestID property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getRequestID() {
		return requestID;
	}

	/**
	 * Sets the value of the requestID property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setRequestID(String value) {
		this.requestID = value;
	}

}
