
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for NamedInsuredSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NamedInsuredSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="namedInsuredIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lengthOfTimeKnown" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="preferredEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maritalStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occupation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="knownSinceYear" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="homeTelephoneNumber" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceTelephone" minOccurs="0"/>
 *         &lt;element name="businessTelephoneNumber" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceTelephone" minOccurs="0"/>
 *         &lt;element name="cellularTelephoneNumber" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceTelephone" minOccurs="0"/>
 *         &lt;element name="mailingAddress" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="employee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PartyName" minOccurs="0"/>
 *         &lt;element name="preferredPostalAddress" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="namedInsuredSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamedInsuredSummary", propOrder = {
    "namedInsuredIdentifier",
    "primary",
    "type",
    "lengthOfTimeKnown",
    "preferredEmailAddress",
    "dateOfBirth",
    "gender",
    "maritalStatus",
    "occupation",
    "knownSinceYear",
    "homeTelephoneNumber",
    "businessTelephoneNumber",
    "cellularTelephoneNumber",
    "mailingAddress",
    "employee",
    "name",
    "preferredPostalAddress",
    "namedInsuredSummaryExtension"
})
public class NamedInsuredSummary {

    protected String namedInsuredIdentifier;
    @XmlElement(defaultValue = "false")
    protected Boolean primary;
    protected String type;
    protected BigInteger lengthOfTimeKnown;
    protected String preferredEmailAddress;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    protected String gender;
    protected String maritalStatus;
    protected String occupation;
    protected BigInteger knownSinceYear;
    protected PreferenceTelephone homeTelephoneNumber;
    protected PreferenceTelephone businessTelephoneNumber;
    protected PreferenceTelephone cellularTelephoneNumber;
    protected PostalAddressSummary mailingAddress;
    protected Boolean employee;
    protected PartyName name;
    protected PostalAddressSummary preferredPostalAddress;
    @XmlElement(nillable = true)
    protected List<Object> namedInsuredSummaryExtension;

    /**
     * Gets the value of the namedInsuredIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamedInsuredIdentifier() {
        return namedInsuredIdentifier;
    }

    /**
     * Sets the value of the namedInsuredIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamedInsuredIdentifier(String value) {
        this.namedInsuredIdentifier = value;
    }

    /**
     * Gets the value of the primary property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrimary(Boolean value) {
        this.primary = value;
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
     * Gets the value of the lengthOfTimeKnown property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLengthOfTimeKnown() {
        return lengthOfTimeKnown;
    }

    /**
     * Sets the value of the lengthOfTimeKnown property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLengthOfTimeKnown(BigInteger value) {
        this.lengthOfTimeKnown = value;
    }

    /**
     * Gets the value of the preferredEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredEmailAddress() {
        return preferredEmailAddress;
    }

    /**
     * Sets the value of the preferredEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredEmailAddress(String value) {
        this.preferredEmailAddress = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the maritalStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the value of the maritalStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaritalStatus(String value) {
        this.maritalStatus = value;
    }

    /**
     * Gets the value of the occupation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the value of the occupation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupation(String value) {
        this.occupation = value;
    }

    /**
     * Gets the value of the knownSinceYear property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getKnownSinceYear() {
        return knownSinceYear;
    }

    /**
     * Sets the value of the knownSinceYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setKnownSinceYear(BigInteger value) {
        this.knownSinceYear = value;
    }

    /**
     * Gets the value of the homeTelephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PreferenceTelephone }
     *     
     */
    public PreferenceTelephone getHomeTelephoneNumber() {
        return homeTelephoneNumber;
    }

    /**
     * Sets the value of the homeTelephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PreferenceTelephone }
     *     
     */
    public void setHomeTelephoneNumber(PreferenceTelephone value) {
        this.homeTelephoneNumber = value;
    }

    /**
     * Gets the value of the businessTelephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PreferenceTelephone }
     *     
     */
    public PreferenceTelephone getBusinessTelephoneNumber() {
        return businessTelephoneNumber;
    }

    /**
     * Sets the value of the businessTelephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PreferenceTelephone }
     *     
     */
    public void setBusinessTelephoneNumber(PreferenceTelephone value) {
        this.businessTelephoneNumber = value;
    }

    /**
     * Gets the value of the cellularTelephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PreferenceTelephone }
     *     
     */
    public PreferenceTelephone getCellularTelephoneNumber() {
        return cellularTelephoneNumber;
    }

    /**
     * Sets the value of the cellularTelephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PreferenceTelephone }
     *     
     */
    public void setCellularTelephoneNumber(PreferenceTelephone value) {
        this.cellularTelephoneNumber = value;
    }

    /**
     * Gets the value of the mailingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSummary }
     *     
     */
    public PostalAddressSummary getMailingAddress() {
        return mailingAddress;
    }

    /**
     * Sets the value of the mailingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSummary }
     *     
     */
    public void setMailingAddress(PostalAddressSummary value) {
        this.mailingAddress = value;
    }

    /**
     * Gets the value of the employee property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmployee() {
        return employee;
    }

    /**
     * Sets the value of the employee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmployee(Boolean value) {
        this.employee = value;
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
     * Gets the value of the preferredPostalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSummary }
     *     
     */
    public PostalAddressSummary getPreferredPostalAddress() {
        return preferredPostalAddress;
    }

    /**
     * Sets the value of the preferredPostalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSummary }
     *     
     */
    public void setPreferredPostalAddress(PostalAddressSummary value) {
        this.preferredPostalAddress = value;
    }

    /**
     * Gets the value of the namedInsuredSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the namedInsuredSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamedInsuredSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getNamedInsuredSummaryExtension() {
        if (namedInsuredSummaryExtension == null) {
            namedInsuredSummaryExtension = new ArrayList<Object>();
        }
        return this.namedInsuredSummaryExtension;
    }

}
