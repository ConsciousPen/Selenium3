
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.AAAOwnershipType;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.StateProvCd;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAAVehicleOwnership complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAVehicleOwnership"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="addressLine1" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine"/&gt;
 *         &lt;element name="addressLine2" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine" minOccurs="0"/&gt;
 *         &lt;element name="addressLine3" type="{http://www.exigeninsurance.com/data/EIS/1.0}AddressLine" minOccurs="0"/&gt;
 *         &lt;element name="city" type="{http://www.exigeninsurance.com/data/EIS/1.0}City"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="postalCode" type="{http://www.exigeninsurance.com/data/EIS/1.0}USPostalCode"/&gt;
 *         &lt;element name="ownershipTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAOwnershipType" minOccurs="0"/&gt;
 *         &lt;element name="secondName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stateProvCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd"/&gt;
 *         &lt;element name="county" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAACountyTownship" minOccurs="0"/&gt;
 *         &lt;element name="regOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="primaryOwnerFirstLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="addOwnerFirstLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "AAAVehicleOwnership", propOrder = {
    "addressLine1",
    "addressLine2",
    "addressLine3",
    "city",
    "name",
    "otherName",
    "postalCode",
    "ownershipTypeCd",
    "secondName",
    "stateProvCd",
    "county",
    "regOwner",
    "primaryOwnerFirstLastName",
    "addOwnerFirstLastName"
})
public class AAAVehicleOwnership {

    @XmlElement(required = true)
    protected String addressLine1;
    protected String addressLine2;
    protected String addressLine3;
    @XmlElement(required = true)
    protected String city;
    protected String name;
    protected String otherName;
    @XmlElement(required = true)
    protected String postalCode;
    @XmlSchemaType(name = "string")
    protected AAAOwnershipType ownershipTypeCd;
    protected String secondName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StateProvCd stateProvCd;
    protected String county;
    @XmlElement(defaultValue = "false")
    protected Boolean regOwner;
    protected String primaryOwnerFirstLastName;
    protected String addOwnerFirstLastName;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

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
     * Gets the value of the otherName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherName() {
        return otherName;
    }

    /**
     * Sets the value of the otherName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherName(String value) {
        this.otherName = value;
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
     * Gets the value of the ownershipTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOwnershipType }
     *     
     */
    public AAAOwnershipType getOwnershipTypeCd() {
        return ownershipTypeCd;
    }

    /**
     * Sets the value of the ownershipTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOwnershipType }
     *     
     */
    public void setOwnershipTypeCd(AAAOwnershipType value) {
        this.ownershipTypeCd = value;
    }

    /**
     * Gets the value of the secondName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondName() {
        return secondName;
    }

    /**
     * Sets the value of the secondName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondName(String value) {
        this.secondName = value;
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
     * Gets the value of the regOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRegOwner() {
        return regOwner;
    }

    /**
     * Sets the value of the regOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRegOwner(Boolean value) {
        this.regOwner = value;
    }

    /**
     * Gets the value of the primaryOwnerFirstLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryOwnerFirstLastName() {
        return primaryOwnerFirstLastName;
    }

    /**
     * Sets the value of the primaryOwnerFirstLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryOwnerFirstLastName(String value) {
        this.primaryOwnerFirstLastName = value;
    }

    /**
     * Gets the value of the addOwnerFirstLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddOwnerFirstLastName() {
        return addOwnerFirstLastName;
    }

    /**
     * Sets the value of the addOwnerFirstLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddOwnerFirstLastName(String value) {
        this.addOwnerFirstLastName = value;
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
