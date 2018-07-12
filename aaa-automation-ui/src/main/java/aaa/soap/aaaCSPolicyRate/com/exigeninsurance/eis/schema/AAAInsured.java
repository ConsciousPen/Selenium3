
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;


import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.AAAMaritalStatusCd;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.GenderCd;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.InsuredType;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.NameTypeCd;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

/**
 * <p>Java class for AAAInsured complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AAAInsured">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AAAInsuredMailingAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAInsuredMailingAddress"/>
 *         &lt;element name="AAAInsuredPrimaryAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAInsuredPrimaryAddress"/>
 *         &lt;element name="additionalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalNameInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="addressLessInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="communicationInfo.email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="communicationInfo.phoneNumber1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateOfEmployment" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="differentAddressInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="employerCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employerDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}GenderCd" minOccurs="0"/>
 *         &lt;element name="insuredBaseDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="insuredType" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}InsuredType" minOccurs="0"/>
 *         &lt;element name="knownSince" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legalEntityCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legalIdentification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lengthTimeKnown" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="maritalStatusCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAMaritalStatusCd" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}NameTypeCd" minOccurs="0"/>
 *         &lt;element name="occupationClassCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occupationDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oid0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="principalRoleCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="salutation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titleCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="oid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAInsured", propOrder = {
        "aaaInsuredMailingAddress",
        "aaaInsuredPrimaryAddress",
        "additionalName",
        "additionalNameInd",
        "addressLessInd",
        "communicationInfoEmail",
        "communicationInfoPhoneNumber1",
        "dateOfBirth",
        "dateOfEmployment",
        "differentAddressInd",
        "employerCd",
        "employerDesc",
        "firstName",
        "gender",
        "insuredBaseDt",
        "insuredType",
        "knownSince",
        "lastName",
        "legalEntityCd",
        "legalIdentification",
        "lengthTimeKnown",
        "maritalStatusCd",
        "middleName",
        "nameTypeCd",
        "occupationClassCd",
        "occupationDesc",
        "oid",
        "otherName",
        "principalRoleCd",
        "salutation",
        "suffix",
        "titleCd"
})
public class AAAInsured {

    @XmlElement(name = "AAAInsuredMailingAddress", required = true)
    protected AAAInsuredMailingAddress aaaInsuredMailingAddress;
    @XmlElement(name = "AAAInsuredPrimaryAddress", required = true)
    protected AAAInsuredPrimaryAddress aaaInsuredPrimaryAddress;
    protected String additionalName;
    protected Boolean additionalNameInd;
    protected Boolean addressLessInd;
    @XmlElement(name = "communicationInfo.email")
    protected String communicationInfoEmail;
    @XmlElement(name = "communicationInfo.phoneNumber1")
    protected String communicationInfoPhoneNumber1;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOfEmployment;
    protected Boolean differentAddressInd;
    protected String employerCd;
    protected String employerDesc;
    protected String firstName;
    @XmlSchemaType(name = "string")
    protected GenderCd gender;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar insuredBaseDt;
    @XmlSchemaType(name = "string")
    protected InsuredType insuredType;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar knownSince;
    protected String lastName;
    protected String legalEntityCd;
    protected String legalIdentification;
    protected BigDecimal lengthTimeKnown;
    @XmlSchemaType(name = "string")
    protected AAAMaritalStatusCd maritalStatusCd;
    protected String middleName;
    @XmlSchemaType(name = "string")
    protected NameTypeCd nameTypeCd;
    protected String occupationClassCd;
    protected String occupationDesc;
    protected String oid;
    protected String otherName;
    protected String principalRoleCd;
    protected String salutation;
    protected String suffix;
    protected String titleCd;
    @XmlAttribute(name = "oid", required = true)
    protected String oid0;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the aaaInsuredMailingAddress property.
     *
     * @return
     *     possible object is
     *     {@link AAAInsuredMailingAddress }
     *
     */
    public AAAInsuredMailingAddress getAAAInsuredMailingAddress() {
        return aaaInsuredMailingAddress;
    }

    /**
     * Sets the value of the aaaInsuredMailingAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link AAAInsuredMailingAddress }
     *
     */
    public void setAAAInsuredMailingAddress(AAAInsuredMailingAddress value) {
        this.aaaInsuredMailingAddress = value;
    }

    /**
     * Gets the value of the aaaInsuredPrimaryAddress property.
     *
     * @return
     *     possible object is
     *     {@link AAAInsuredPrimaryAddress }
     *
     */
    public AAAInsuredPrimaryAddress getAAAInsuredPrimaryAddress() {
        return aaaInsuredPrimaryAddress;
    }

    /**
     * Sets the value of the aaaInsuredPrimaryAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link AAAInsuredPrimaryAddress }
     *
     */
    public void setAAAInsuredPrimaryAddress(AAAInsuredPrimaryAddress value) {
        this.aaaInsuredPrimaryAddress = value;
    }

    /**
     * Gets the value of the additionalName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAdditionalName() {
        return additionalName;
    }

    /**
     * Sets the value of the additionalName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAdditionalName(String value) {
        this.additionalName = value;
    }

    /**
     * Gets the value of the additionalNameInd property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isAdditionalNameInd() {
        return additionalNameInd;
    }

    /**
     * Sets the value of the additionalNameInd property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setAdditionalNameInd(Boolean value) {
        this.additionalNameInd = value;
    }

    /**
     * Gets the value of the addressLessInd property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isAddressLessInd() {
        return addressLessInd;
    }

    /**
     * Sets the value of the addressLessInd property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setAddressLessInd(Boolean value) {
        this.addressLessInd = value;
    }

    /**
     * Gets the value of the communicationInfoEmail property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCommunicationInfoEmail() {
        return communicationInfoEmail;
    }

    /**
     * Sets the value of the communicationInfoEmail property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCommunicationInfoEmail(String value) {
        this.communicationInfoEmail = value;
    }

    /**
     * Gets the value of the communicationInfoPhoneNumber1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCommunicationInfoPhoneNumber1() {
        return communicationInfoPhoneNumber1;
    }

    /**
     * Sets the value of the communicationInfoPhoneNumber1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCommunicationInfoPhoneNumber1(String value) {
        this.communicationInfoPhoneNumber1 = value;
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
     * Gets the value of the dateOfEmployment property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDateOfEmployment() {
        return dateOfEmployment;
    }

    /**
     * Sets the value of the dateOfEmployment property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDateOfEmployment(XMLGregorianCalendar value) {
        this.dateOfEmployment = value;
    }

    /**
     * Gets the value of the differentAddressInd property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isDifferentAddressInd() {
        return differentAddressInd;
    }

    /**
     * Sets the value of the differentAddressInd property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setDifferentAddressInd(Boolean value) {
        this.differentAddressInd = value;
    }

    /**
     * Gets the value of the employerCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmployerCd() {
        return employerCd;
    }

    /**
     * Sets the value of the employerCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmployerCd(String value) {
        this.employerCd = value;
    }

    /**
     * Gets the value of the employerDesc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmployerDesc() {
        return employerDesc;
    }

    /**
     * Sets the value of the employerDesc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmployerDesc(String value) {
        this.employerDesc = value;
    }

    /**
     * Gets the value of the firstName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the gender property.
     *
     * @return
     *     possible object is
     *     {@link GenderCd }
     *
     */
    public GenderCd getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     *
     * @param value
     *     allowed object is
     *     {@link GenderCd }
     *
     */
    public void setGender(GenderCd value) {
        this.gender = value;
    }

    /**
     * Gets the value of the insuredBaseDt property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getInsuredBaseDt() {
        return insuredBaseDt;
    }

    /**
     * Sets the value of the insuredBaseDt property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setInsuredBaseDt(XMLGregorianCalendar value) {
        this.insuredBaseDt = value;
    }

    /**
     * Gets the value of the insuredType property.
     *
     * @return
     *     possible object is
     *     {@link InsuredType }
     *
     */
    public InsuredType getInsuredType() {
        return insuredType;
    }

    /**
     * Sets the value of the insuredType property.
     *
     * @param value
     *     allowed object is
     *     {@link InsuredType }
     *
     */
    public void setInsuredType(InsuredType value) {
        this.insuredType = value;
    }

    /**
     * Gets the value of the knownSince property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getKnownSince() {
        return knownSince;
    }

    /**
     * Sets the value of the knownSince property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setKnownSince(XMLGregorianCalendar value) {
        this.knownSince = value;
    }

    /**
     * Gets the value of the lastName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the legalEntityCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegalEntityCd() {
        return legalEntityCd;
    }

    /**
     * Sets the value of the legalEntityCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegalEntityCd(String value) {
        this.legalEntityCd = value;
    }

    /**
     * Gets the value of the legalIdentification property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegalIdentification() {
        return legalIdentification;
    }

    /**
     * Sets the value of the legalIdentification property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegalIdentification(String value) {
        this.legalIdentification = value;
    }

    /**
     * Gets the value of the lengthTimeKnown property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getLengthTimeKnown() {
        return lengthTimeKnown;
    }

    /**
     * Sets the value of the lengthTimeKnown property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setLengthTimeKnown(BigDecimal value) {
        this.lengthTimeKnown = value;
    }

    /**
     * Gets the value of the maritalStatusCd property.
     *
     * @return
     *     possible object is
     *     {@link AAAMaritalStatusCd }
     *
     */
    public AAAMaritalStatusCd getMaritalStatusCd() {
        return maritalStatusCd;
    }

    /**
     * Sets the value of the maritalStatusCd property.
     *
     * @param value
     *     allowed object is
     *     {@link AAAMaritalStatusCd }
     *
     */
    public void setMaritalStatusCd(AAAMaritalStatusCd value) {
        this.maritalStatusCd = value;
    }

    /**
     * Gets the value of the middleName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the nameTypeCd property.
     *
     * @return
     *     possible object is
     *     {@link NameTypeCd }
     *
     */
    public NameTypeCd getNameTypeCd() {
        return nameTypeCd;
    }

    /**
     * Sets the value of the nameTypeCd property.
     *
     * @param value
     *     allowed object is
     *     {@link NameTypeCd }
     *
     */
    public void setNameTypeCd(NameTypeCd value) {
        this.nameTypeCd = value;
    }

    /**
     * Gets the value of the occupationClassCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOccupationClassCd() {
        return occupationClassCd;
    }

    /**
     * Sets the value of the occupationClassCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOccupationClassCd(String value) {
        this.occupationClassCd = value;
    }

    /**
     * Gets the value of the occupationDesc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOccupationDesc() {
        return occupationDesc;
    }

    /**
     * Sets the value of the occupationDesc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOccupationDesc(String value) {
        this.occupationDesc = value;
    }

    /**
     * Gets the value of the oid0 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOid0() {
        return oid0;
    }

    /**
     * Sets the value of the oid0 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOid0(String value) {
        this.oid0 = value;
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
     * Gets the value of the principalRoleCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrincipalRoleCd() {
        return principalRoleCd;
    }

    /**
     * Sets the value of the principalRoleCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrincipalRoleCd(String value) {
        this.principalRoleCd = value;
    }

    /**
     * Gets the value of the salutation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSalutation() {
        return salutation;
    }

    /**
     * Sets the value of the salutation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSalutation(String value) {
        this.salutation = value;
    }

    /**
     * Gets the value of the suffix property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuffix(String value) {
        this.suffix = value;
    }

    /**
     * Gets the value of the titleCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitleCd() {
        return titleCd;
    }

    /**
     * Sets the value of the titleCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitleCd(String value) {
        this.titleCd = value;
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
