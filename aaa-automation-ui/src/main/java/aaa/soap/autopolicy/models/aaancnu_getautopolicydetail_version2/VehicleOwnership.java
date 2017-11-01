
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VehicleOwnership complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehicleOwnership">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loanNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="interestIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="telephoneNumbers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TelephoneNumbers" minOccurs="0"/>
 *         &lt;element name="emailAddresses" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EmailAddresses" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PartyName" minOccurs="0"/>
 *         &lt;element name="otherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regOwner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryOwnerFirstLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addOwnerFirstLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameOfOwner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleOwnershipExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehicleOwnership", propOrder = {
    "loanNumber",
    "sequenceNumber",
    "interestIdentifier",
    "billTo",
    "type",
    "address",
    "telephoneNumbers",
    "emailAddresses",
    "name",
    "otherName",
    "regOwner",
    "primaryOwnerFirstLastName",
    "addOwnerFirstLastName",
    "nameOfOwner",
    "vehicleOwnershipExtension"
})
public class VehicleOwnership {

    protected String loanNumber;
    protected BigInteger sequenceNumber;
    protected String interestIdentifier;
    protected String billTo;
    protected String type;
    protected PostalAddressSummary address;
    protected TelephoneNumbers telephoneNumbers;
    protected EmailAddresses emailAddresses;
    protected PartyName name;
    protected String otherName;
    protected String regOwner;
    protected String primaryOwnerFirstLastName;
    protected String addOwnerFirstLastName;
    protected String nameOfOwner;
    @XmlElement(nillable = true)
    protected List<Object> vehicleOwnershipExtension;

    /**
     * Gets the value of the loanNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoanNumber() {
        return loanNumber;
    }

    /**
     * Sets the value of the loanNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoanNumber(String value) {
        this.loanNumber = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the interestIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterestIdentifier() {
        return interestIdentifier;
    }

    /**
     * Sets the value of the interestIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterestIdentifier(String value) {
        this.interestIdentifier = value;
    }

    /**
     * Gets the value of the billTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillTo() {
        return billTo;
    }

    /**
     * Sets the value of the billTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillTo(String value) {
        this.billTo = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSummary }
     *     
     */
    public PostalAddressSummary getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSummary }
     *     
     */
    public void setAddress(PostalAddressSummary value) {
        this.address = value;
    }

    /**
     * Gets the value of the telephoneNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link TelephoneNumbers }
     *     
     */
    public TelephoneNumbers getTelephoneNumbers() {
        return telephoneNumbers;
    }

    /**
     * Sets the value of the telephoneNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link TelephoneNumbers }
     *     
     */
    public void setTelephoneNumbers(TelephoneNumbers value) {
        this.telephoneNumbers = value;
    }

    /**
     * Gets the value of the emailAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link EmailAddresses }
     *     
     */
    public EmailAddresses getEmailAddresses() {
        return emailAddresses;
    }

    /**
     * Sets the value of the emailAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailAddresses }
     *     
     */
    public void setEmailAddresses(EmailAddresses value) {
        this.emailAddresses = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link PartyName }
     *     
     */
    public PartyName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyName }
     *     
     */
    public void setName(PartyName value) {
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
     * Gets the value of the regOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegOwner() {
        return regOwner;
    }

    /**
     * Sets the value of the regOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegOwner(String value) {
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
     * Gets the value of the nameOfOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfOwner() {
        return nameOfOwner;
    }

    /**
     * Sets the value of the nameOfOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfOwner(String value) {
        this.nameOfOwner = value;
    }

    /**
     * Gets the value of the vehicleOwnershipExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicleOwnershipExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicleOwnershipExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getVehicleOwnershipExtension() {
        if (vehicleOwnershipExtension == null) {
            vehicleOwnershipExtension = new ArrayList<Object>();
        }
        return this.vehicleOwnershipExtension;
    }

}
