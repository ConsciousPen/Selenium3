
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.AddressType;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.Country;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.StateProvCd;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAAInsuredPriorAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAInsuredPriorAddress"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="addressDisplayValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="addressLine1" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine"/&gt;
 *         &lt;element name="addressLine2" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine" minOccurs="0"/&gt;
 *         &lt;element name="addressLine3" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine" minOccurs="0"/&gt;
 *         &lt;element name="city" type="{http://www.exigeninsurance.com/data/EIS/1.0}City"/&gt;
 *         &lt;element name="countryCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}Country"/&gt;
 *         &lt;element name="isAddressValidated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="postalCode" type="{http://www.exigeninsurance.com/data/EIS/1.0}USPostalCode"/&gt;
 *         &lt;element name="stateProvCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd"/&gt;
 *         &lt;element name="usageType" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AddressType"/&gt;
 *         &lt;element name="county" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAACountyTownship" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAInsuredPriorAddress", propOrder = {
    "addressDisplayValue",
    "addressLine1",
    "addressLine2",
    "addressLine3",
    "city",
    "countryCd",
    "isAddressValidated",
    "postalCode",
    "stateProvCd",
    "usageType",
    "county"
})
public class AAAInsuredPriorAddress {

    protected String addressDisplayValue;
    @XmlElement(required = true)
    protected String addressLine1;
    protected String addressLine2;
    protected String addressLine3;
    @XmlElement(required = true)
    protected String city;
    @XmlElement(required = true, defaultValue = "US")
    @XmlSchemaType(name = "string")
    protected Country countryCd;
    protected Boolean isAddressValidated;
    @XmlElement(required = true)
    protected String postalCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StateProvCd stateProvCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AddressType usageType;
    protected String county;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the addressDisplayValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressDisplayValue() {
        return addressDisplayValue;
    }

    /**
     * Sets the value of the addressDisplayValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressDisplayValue(String value) {
        this.addressDisplayValue = value;
    }

    /**
     * Gets the value of the addressLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the value of the addressLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine1(String value) {
        this.addressLine1 = value;
    }

    /**
     * Gets the value of the addressLine2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the value of the addressLine2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine2(String value) {
        this.addressLine2 = value;
    }

    /**
     * Gets the value of the addressLine3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine3() {
        return addressLine3;
    }

    /**
     * Sets the value of the addressLine3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine3(String value) {
        this.addressLine3 = value;
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
     * Gets the value of the countryCd property.
     * 
     * @return
     *     possible object is
     *     {@link Country }
     *     
     */
    public Country getCountryCd() {
        return countryCd;
    }

    /**
     * Sets the value of the countryCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Country }
     *     
     */
    public void setCountryCd(Country value) {
        this.countryCd = value;
    }

    /**
     * Gets the value of the isAddressValidated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAddressValidated() {
        return isAddressValidated;
    }

    /**
     * Sets the value of the isAddressValidated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAddressValidated(Boolean value) {
        this.isAddressValidated = value;
    }

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

    /**
     * Gets the value of the usageType property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getUsageType() {
        return usageType;
    }

    /**
     * Sets the value of the usageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setUsageType(AddressType value) {
        this.usageType = value;
    }

    /**
     * Gets the value of the county property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCounty() {
        return county;
    }

    /**
     * Sets the value of the county property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCounty(String value) {
        this.county = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentState }
     *     
     */
    public ComponentState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentState }
     *     
     */
    public void setState(ComponentState value) {
        this.state = value;
    }

}
