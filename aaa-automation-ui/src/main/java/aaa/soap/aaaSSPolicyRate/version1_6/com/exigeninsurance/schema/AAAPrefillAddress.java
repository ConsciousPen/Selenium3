
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.StateProvCd;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAAPrefillAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAPrefillAddress"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="addressLine" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="stateProvCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAPrefillAddress", propOrder = {
    "postalCode",
    "city",
    "addressLine",
    "stateProvCd"
})
public class AAAPrefillAddress {

    @XmlElement(required = true)
    protected String postalCode;
    @XmlElement(required = true)
    protected String city;
    @XmlElement(required = true)
    protected String addressLine;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StateProvCd stateProvCd;

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the addressLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine() {
        return addressLine;
    }

    /**
     * Sets the value of the addressLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine(String value) {
        this.addressLine = value;
    }

    /**
     * Gets the value of the stateProvCd property.
     * 
     * @return
     *     possible object is
     *     {@link StateProvCd }
     *     
     */
    public StateProvCd getStateProvCd() {
        return stateProvCd;
    }

    /**
     * Sets the value of the stateProvCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateProvCd }
     *     
     */
    public void setStateProvCd(StateProvCd value) {
        this.stateProvCd = value;
    }

}
