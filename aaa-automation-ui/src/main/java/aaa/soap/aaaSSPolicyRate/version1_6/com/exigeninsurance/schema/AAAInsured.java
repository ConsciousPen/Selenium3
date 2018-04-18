
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAAInsured complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAInsured"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAACreditScoreInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAACreditScoreInfo" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="AAAInsuredMailingAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAInsuredMailingAddress" minOccurs="0"/&gt;
 *         &lt;element name="AAAInsuredPrimaryAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAInsuredPrimaryAddress"/&gt;
 *         &lt;element name="AAAInsuredPriorAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAInsuredPriorAddress" minOccurs="0"/&gt;
 *         &lt;element name="communicationInfo.email" type="{http://www.exigeninsurance.com/data/EIS/1.0}EmailAddress" minOccurs="0"/&gt;
 *         &lt;element name="communicationInfo.phoneNumber1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="communicationInfo.phoneNumber2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="communicationInfo.phoneNumber3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="preferredPhone" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PreferredPhone" minOccurs="0"/&gt;
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="differentAddressInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="gender" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}GenderCd" minOccurs="0"/&gt;
 *         &lt;element name="insuredBaseDt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ageDisplay" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="legalIdentification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="maritalStatusCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAMaritalStatusCd" minOccurs="0"/&gt;
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="occupationClassCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="occupationDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="principalRoleCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}InsuredOrPrincipalRole" minOccurs="0"/&gt;
 *         &lt;element name="residentTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAResidentType"/&gt;
 *         &lt;element name="hasLivedHereInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="moveInDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="suffix" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAASuffix" minOccurs="0"/&gt;
 *         &lt;element name="titleCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAATitle" minOccurs="0"/&gt;
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
@XmlType(name = "AAAInsured", propOrder = {
    "aaaCreditScoreInfo",
    "aaaInsuredMailingAddress",
    "aaaInsuredPrimaryAddress",
    "aaaInsuredPriorAddress",
    "communicationInfoEmail",
    "communicationInfoPhoneNumber1",
    "communicationInfoPhoneNumber2",
    "communicationInfoPhoneNumber3",
    "preferredPhone",
    "dateOfBirth",
    "differentAddressInd",
    "firstName",
    "gender",
    "insuredBaseDt",
    "lastName",
    "ageDisplay",
    "legalIdentification",
    "maritalStatusCd",
    "middleName",
    "occupationClassCd",
    "occupationDesc",
    "principalRoleCd",
    "residentTypeCd",
    "hasLivedHereInd",
    "moveInDt",
    "suffix",
    "titleCd"
})
public class AAAInsured {

    @XmlElement(name = "AAACreditScoreInfo")
    protected List<AAACreditScoreInfo> aaaCreditScoreInfo;
    @XmlElement(name = "AAAInsuredMailingAddress")
    protected AAAInsuredMailingAddress aaaInsuredMailingAddress;
    @XmlElement(name = "AAAInsuredPrimaryAddress", required = true)
    protected AAAInsuredPrimaryAddress aaaInsuredPrimaryAddress;
    @XmlElement(name = "AAAInsuredPriorAddress")
    protected AAAInsuredPriorAddress aaaInsuredPriorAddress;
    @XmlElement(name = "communicationInfo.email")
    protected String communicationInfoEmail;
    @XmlElement(name = "communicationInfo.phoneNumber1")
    protected String communicationInfoPhoneNumber1;
    @XmlElement(name = "communicationInfo.phoneNumber2")
    protected String communicationInfoPhoneNumber2;
    @XmlElement(name = "communicationInfo.phoneNumber3")
    protected String communicationInfoPhoneNumber3;
    @XmlSchemaType(name = "string")
    protected PreferredPhone preferredPhone;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOfBirth;
    protected Boolean differentAddressInd;
    @XmlElement(required = true)
    protected String firstName;
    @XmlSchemaType(name = "string")
    protected GenderCd gender;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar insuredBaseDt;
    @XmlElement(required = true)
    protected String lastName;
    protected BigDecimal ageDisplay;
    protected String legalIdentification;
    @XmlSchemaType(name = "string")
    protected AAAMaritalStatusCd maritalStatusCd;
    protected String middleName;
    protected String occupationClassCd;
    protected String occupationDesc;
    @XmlSchemaType(name = "string")
    protected InsuredOrPrincipalRole principalRoleCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAAResidentType residentTypeCd;
    protected Boolean hasLivedHereInd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moveInDt;
    @XmlSchemaType(name = "string")
    protected AAASuffix suffix;
    @XmlSchemaType(name = "string")
    protected AAATitle titleCd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the aaaCreditScoreInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaCreditScoreInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAACreditScoreInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAACreditScoreInfo }
     * 
     * 
     */
    public List<AAACreditScoreInfo> getAAACreditScoreInfo() {
        if (aaaCreditScoreInfo == null) {
            aaaCreditScoreInfo = new ArrayList<AAACreditScoreInfo>();
        }
        return this.aaaCreditScoreInfo;
    }

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
     * Gets the value of the aaaInsuredPriorAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AAAInsuredPriorAddress }
     *     
     */
    public AAAInsuredPriorAddress getAAAInsuredPriorAddress() {
        return aaaInsuredPriorAddress;
    }

    /**
     * Sets the value of the aaaInsuredPriorAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAInsuredPriorAddress }
     *     
     */
    public void setAAAInsuredPriorAddress(AAAInsuredPriorAddress value) {
        this.aaaInsuredPriorAddress = value;
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
     * Gets the value of the communicationInfoPhoneNumber2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommunicationInfoPhoneNumber2() {
        return communicationInfoPhoneNumber2;
    }

    /**
     * Sets the value of the communicationInfoPhoneNumber2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommunicationInfoPhoneNumber2(String value) {
        this.communicationInfoPhoneNumber2 = value;
    }

    /**
     * Gets the value of the communicationInfoPhoneNumber3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommunicationInfoPhoneNumber3() {
        return communicationInfoPhoneNumber3;
    }

    /**
     * Sets the value of the communicationInfoPhoneNumber3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommunicationInfoPhoneNumber3(String value) {
        this.communicationInfoPhoneNumber3 = value;
    }

    /**
     * Gets the value of the preferredPhone property.
     * 
     * @return
     *     possible object is
     *     {@link PreferredPhone }
     *     
     */
    public PreferredPhone getPreferredPhone() {
        return preferredPhone;
    }

    /**
     * Sets the value of the preferredPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link PreferredPhone }
     *     
     */
    public void setPreferredPhone(PreferredPhone value) {
        this.preferredPhone = value;
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
     * Gets the value of the ageDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAgeDisplay() {
        return ageDisplay;
    }

    /**
     * Sets the value of the ageDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAgeDisplay(BigDecimal value) {
        this.ageDisplay = value;
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
     * Gets the value of the principalRoleCd property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredOrPrincipalRole }
     *     
     */
    public InsuredOrPrincipalRole getPrincipalRoleCd() {
        return principalRoleCd;
    }

    /**
     * Sets the value of the principalRoleCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredOrPrincipalRole }
     *     
     */
    public void setPrincipalRoleCd(InsuredOrPrincipalRole value) {
        this.principalRoleCd = value;
    }

    /**
     * Gets the value of the residentTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAResidentType }
     *     
     */
    public AAAResidentType getResidentTypeCd() {
        return residentTypeCd;
    }

    /**
     * Sets the value of the residentTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAResidentType }
     *     
     */
    public void setResidentTypeCd(AAAResidentType value) {
        this.residentTypeCd = value;
    }

    /**
     * Gets the value of the hasLivedHereInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasLivedHereInd() {
        return hasLivedHereInd;
    }

    /**
     * Sets the value of the hasLivedHereInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasLivedHereInd(Boolean value) {
        this.hasLivedHereInd = value;
    }

    /**
     * Gets the value of the moveInDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMoveInDt() {
        return moveInDt;
    }

    /**
     * Sets the value of the moveInDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMoveInDt(XMLGregorianCalendar value) {
        this.moveInDt = value;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * @return
     *     possible object is
     *     {@link AAASuffix }
     *     
     */
    public AAASuffix getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAASuffix }
     *     
     */
    public void setSuffix(AAASuffix value) {
        this.suffix = value;
    }

    /**
     * Gets the value of the titleCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAATitle }
     *     
     */
    public AAATitle getTitleCd() {
        return titleCd;
    }

    /**
     * Sets the value of the titleCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAATitle }
     *     
     */
    public void setTitleCd(AAATitle value) {
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
