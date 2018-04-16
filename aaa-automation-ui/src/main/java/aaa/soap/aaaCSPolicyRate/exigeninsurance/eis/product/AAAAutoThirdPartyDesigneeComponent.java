
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAAutoThirdPartyDesigneeComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAAutoThirdPartyDesigneeComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addressLine1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addressLine2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addressLine3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stateProvCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAAutoThirdPartyDesigneeComponent", propOrder = {
    "addressLine1",
    "addressLine2",
    "addressLine3",
    "city",
    "countryCd",
    "name",
    "postalCode",
    "stateProvCd"
})
public class AAAAutoThirdPartyDesigneeComponent {

    protected String addressLine1;
    protected String addressLine2;
    protected String addressLine3;
    protected String city;
    protected String countryCd;
    protected String name;
    protected String postalCode;
    protected String stateProvCd;

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
     *     {@link String }
     *     
     */
    public String getCountryCd() {
        return countryCd;
    }

    /**
     * Sets the value of the countryCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCd(String value) {
        this.countryCd = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     *     {@link String }
     *     
     */
    public String getStateProvCd() {
        return stateProvCd;
    }

    /**
     * Sets the value of the stateProvCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateProvCd(String value) {
        this.stateProvCd = value;
    }

}
