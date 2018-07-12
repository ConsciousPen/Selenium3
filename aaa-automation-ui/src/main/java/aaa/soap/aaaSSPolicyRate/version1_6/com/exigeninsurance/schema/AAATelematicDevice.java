
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAATelematicDevice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAATelematicDevice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="usageBasedInsuranceInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAATelematicDevice", propOrder = {
    "usageBasedInsuranceInd"
})
public class AAATelematicDevice {

    @XmlElement(defaultValue = "false")
    protected Boolean usageBasedInsuranceInd;

    /**
     * Gets the value of the usageBasedInsuranceInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUsageBasedInsuranceInd() {
        return usageBasedInsuranceInd;
    }

    /**
     * Sets the value of the usageBasedInsuranceInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUsageBasedInsuranceInd(Boolean value) {
        this.usageBasedInsuranceInd = value;
    }

}
