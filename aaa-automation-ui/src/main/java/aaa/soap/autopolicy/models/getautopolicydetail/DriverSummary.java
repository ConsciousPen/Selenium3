
package aaa.soap.autopolicy.models.getautopolicydetail;

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
 * <p>Java class for DriverSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriverSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="occupation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maritalStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalPoints" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="employeeNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="age" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="yearsLicensed" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="goodStudent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="excludedFromPolicy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="distantStudent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="matureDriver" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="adbCoverageIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="totalDisabilityIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="relationToInsured" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yearsAccidentFreeAmount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="threeYearsLicRestriction" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="personIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="dateOfDeath" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="socialSecurityNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yearsLicensedInUS" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="excludedDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="smartDriver" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="defensiveDriver" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="discounts" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Discounts" minOccurs="0"/>
 *         &lt;element name="namedInsuredIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodDriver" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="rideshareInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nonSmokerInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="driverLicense" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriversLicense" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="emailAddresses" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EmailAddresses" minOccurs="0"/>
 *         &lt;element name="telephoneNumbers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TelephoneNumbers" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PersonName" minOccurs="0"/>
 *         &lt;element name="sr22" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverSR22" minOccurs="0"/>
 *         &lt;element name="endorsementForms" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EndorsementForms" minOccurs="0"/>
 *         &lt;element name="violations" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverViolations" minOccurs="0"/>
 *         &lt;element name="accidents" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverAccidents" minOccurs="0"/>
 *         &lt;element name="otherFee" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}SurchargeAndFee" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="driverSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriverSummary", propOrder = {
    "occupation",
    "maritalStatus",
    "totalPoints",
    "employeeNumber",
    "employee",
    "age",
    "yearsLicensed",
    "sequenceNumber",
    "goodStudent",
    "excludedFromPolicy",
    "distantStudent",
    "matureDriver",
    "adbCoverageIndicator",
    "totalDisabilityIndicator",
    "relationToInsured",
    "yearsAccidentFreeAmount",
    "threeYearsLicRestriction",
    "personIdentifier",
    "dateOfBirth",
    "dateOfDeath",
    "socialSecurityNumber",
    "driverIdentifier",
    "yearsLicensedInUS",
    "gender",
    "excludedDate",
    "type",
    "effectiveDate",
    "expirationDate",
    "smartDriver",
    "defensiveDriver",
    "discounts",
    "namedInsuredIdentifier",
    "goodDriver",
    "rideshareInd",
    "nonSmokerInd",
    "driverLicense",
    "address",
    "emailAddresses",
    "telephoneNumbers",
    "name",
    "sr22",
    "endorsementForms",
    "violations",
    "accidents",
    "otherFee",
    "driverSummaryExtension"
})
public class DriverSummary {

    protected String occupation;
    protected String maritalStatus;
    protected BigInteger totalPoints;
    protected String employeeNumber;
    protected Boolean employee;
    protected BigInteger age;
    protected BigInteger yearsLicensed;
    protected BigInteger sequenceNumber;
    protected Boolean goodStudent;
    protected Boolean excludedFromPolicy;
    protected Boolean distantStudent;
    protected Boolean matureDriver;
    protected Boolean adbCoverageIndicator;
    protected Boolean totalDisabilityIndicator;
    protected String relationToInsured;
    protected BigInteger yearsAccidentFreeAmount;
    protected BigInteger threeYearsLicRestriction;
    protected String personIdentifier;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfDeath;
    protected String socialSecurityNumber;
    protected String driverIdentifier;
    protected BigInteger yearsLicensedInUS;
    protected String gender;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar excludedDate;
    protected String type;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effectiveDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expirationDate;
    protected Boolean smartDriver;
    protected Boolean defensiveDriver;
    protected Discounts discounts;
    protected String namedInsuredIdentifier;
    protected Boolean goodDriver;
    protected Boolean rideshareInd;
    protected Boolean nonSmokerInd;
    protected DriversLicense driverLicense;
    protected PostalAddressSummary address;
    protected EmailAddresses emailAddresses;
    protected TelephoneNumbers telephoneNumbers;
    protected PersonName name;
    protected DriverSR22 sr22;
    protected EndorsementForms endorsementForms;
    protected DriverViolations violations;
    protected DriverAccidents accidents;
    @XmlElement(nillable = true)
    protected List<SurchargeAndFee> otherFee;
    @XmlElement(nillable = true)
    protected List<Object> driverSummaryExtension;

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
     * Gets the value of the totalPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalPoints() {
        return totalPoints;
    }

    /**
     * Sets the value of the totalPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalPoints(BigInteger value) {
        this.totalPoints = value;
    }

    /**
     * Gets the value of the employeeNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * Sets the value of the employeeNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeNumber(String value) {
        this.employeeNumber = value;
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
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAge(BigInteger value) {
        this.age = value;
    }

    /**
     * Gets the value of the yearsLicensed property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getYearsLicensed() {
        return yearsLicensed;
    }

    /**
     * Sets the value of the yearsLicensed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setYearsLicensed(BigInteger value) {
        this.yearsLicensed = value;
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
     * Gets the value of the goodStudent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGoodStudent() {
        return goodStudent;
    }

    /**
     * Sets the value of the goodStudent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGoodStudent(Boolean value) {
        this.goodStudent = value;
    }

    /**
     * Gets the value of the excludedFromPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludedFromPolicy() {
        return excludedFromPolicy;
    }

    /**
     * Sets the value of the excludedFromPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludedFromPolicy(Boolean value) {
        this.excludedFromPolicy = value;
    }

    /**
     * Gets the value of the distantStudent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDistantStudent() {
        return distantStudent;
    }

    /**
     * Sets the value of the distantStudent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDistantStudent(Boolean value) {
        this.distantStudent = value;
    }

    /**
     * Gets the value of the matureDriver property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMatureDriver() {
        return matureDriver;
    }

    /**
     * Sets the value of the matureDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMatureDriver(Boolean value) {
        this.matureDriver = value;
    }

    /**
     * Gets the value of the adbCoverageIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdbCoverageIndicator() {
        return adbCoverageIndicator;
    }

    /**
     * Sets the value of the adbCoverageIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdbCoverageIndicator(Boolean value) {
        this.adbCoverageIndicator = value;
    }

    /**
     * Gets the value of the totalDisabilityIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTotalDisabilityIndicator() {
        return totalDisabilityIndicator;
    }

    /**
     * Sets the value of the totalDisabilityIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTotalDisabilityIndicator(Boolean value) {
        this.totalDisabilityIndicator = value;
    }

    /**
     * Gets the value of the relationToInsured property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationToInsured() {
        return relationToInsured;
    }

    /**
     * Sets the value of the relationToInsured property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationToInsured(String value) {
        this.relationToInsured = value;
    }

    /**
     * Gets the value of the yearsAccidentFreeAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getYearsAccidentFreeAmount() {
        return yearsAccidentFreeAmount;
    }

    /**
     * Sets the value of the yearsAccidentFreeAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setYearsAccidentFreeAmount(BigInteger value) {
        this.yearsAccidentFreeAmount = value;
    }

    /**
     * Gets the value of the threeYearsLicRestriction property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getThreeYearsLicRestriction() {
        return threeYearsLicRestriction;
    }

    /**
     * Sets the value of the threeYearsLicRestriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setThreeYearsLicRestriction(BigInteger value) {
        this.threeYearsLicRestriction = value;
    }

    /**
     * Gets the value of the personIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonIdentifier() {
        return personIdentifier;
    }

    /**
     * Sets the value of the personIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonIdentifier(String value) {
        this.personIdentifier = value;
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
     * Gets the value of the dateOfDeath property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfDeath() {
        return dateOfDeath;
    }

    /**
     * Sets the value of the dateOfDeath property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfDeath(XMLGregorianCalendar value) {
        this.dateOfDeath = value;
    }

    /**
     * Gets the value of the socialSecurityNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    /**
     * Sets the value of the socialSecurityNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialSecurityNumber(String value) {
        this.socialSecurityNumber = value;
    }

    /**
     * Gets the value of the driverIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverIdentifier() {
        return driverIdentifier;
    }

    /**
     * Sets the value of the driverIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverIdentifier(String value) {
        this.driverIdentifier = value;
    }

    /**
     * Gets the value of the yearsLicensedInUS property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getYearsLicensedInUS() {
        return yearsLicensedInUS;
    }

    /**
     * Sets the value of the yearsLicensedInUS property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setYearsLicensedInUS(BigInteger value) {
        this.yearsLicensedInUS = value;
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
     * Gets the value of the excludedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExcludedDate() {
        return excludedDate;
    }

    /**
     * Sets the value of the excludedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExcludedDate(XMLGregorianCalendar value) {
        this.excludedDate = value;
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
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveDate(XMLGregorianCalendar value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpirationDate(XMLGregorianCalendar value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the smartDriver property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmartDriver() {
        return smartDriver;
    }

    /**
     * Sets the value of the smartDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmartDriver(Boolean value) {
        this.smartDriver = value;
    }

    /**
     * Gets the value of the defensiveDriver property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefensiveDriver() {
        return defensiveDriver;
    }

    /**
     * Sets the value of the defensiveDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefensiveDriver(Boolean value) {
        this.defensiveDriver = value;
    }

    /**
     * Gets the value of the discounts property.
     * 
     * @return
     *     possible object is
     *     {@link Discounts }
     *     
     */
    public Discounts getDiscounts() {
        return discounts;
    }

    /**
     * Sets the value of the discounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Discounts }
     *     
     */
    public void setDiscounts(Discounts value) {
        this.discounts = value;
    }

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
     * Gets the value of the goodDriver property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGoodDriver() {
        return goodDriver;
    }

    /**
     * Sets the value of the goodDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGoodDriver(Boolean value) {
        this.goodDriver = value;
    }

    /**
     * Gets the value of the rideshareInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRideshareInd() {
        return rideshareInd;
    }

    /**
     * Sets the value of the rideshareInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRideshareInd(Boolean value) {
        this.rideshareInd = value;
    }

    /**
     * Gets the value of the nonSmokerInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonSmokerInd() {
        return nonSmokerInd;
    }

    /**
     * Sets the value of the nonSmokerInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonSmokerInd(Boolean value) {
        this.nonSmokerInd = value;
    }

    /**
     * Gets the value of the driverLicense property.
     * 
     * @return
     *     possible object is
     *     {@link DriversLicense }
     *     
     */
    public DriversLicense getDriverLicense() {
        return driverLicense;
    }

    /**
     * Sets the value of the driverLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriversLicense }
     *     
     */
    public void setDriverLicense(DriversLicense value) {
        this.driverLicense = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link PersonName }
     *     
     */
    public PersonName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonName }
     *     
     */
    public void setName(PersonName value) {
        this.name = value;
    }

    /**
     * Gets the value of the sr22 property.
     * 
     * @return
     *     possible object is
     *     {@link DriverSR22 }
     *     
     */
    public DriverSR22 getSr22() {
        return sr22;
    }

    /**
     * Sets the value of the sr22 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverSR22 }
     *     
     */
    public void setSr22(DriverSR22 value) {
        this.sr22 = value;
    }

    /**
     * Gets the value of the endorsementForms property.
     * 
     * @return
     *     possible object is
     *     {@link EndorsementForms }
     *     
     */
    public EndorsementForms getEndorsementForms() {
        return endorsementForms;
    }

    /**
     * Sets the value of the endorsementForms property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndorsementForms }
     *     
     */
    public void setEndorsementForms(EndorsementForms value) {
        this.endorsementForms = value;
    }

    /**
     * Gets the value of the violations property.
     * 
     * @return
     *     possible object is
     *     {@link DriverViolations }
     *     
     */
    public DriverViolations getViolations() {
        return violations;
    }

    /**
     * Sets the value of the violations property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverViolations }
     *     
     */
    public void setViolations(DriverViolations value) {
        this.violations = value;
    }

    /**
     * Gets the value of the accidents property.
     * 
     * @return
     *     possible object is
     *     {@link DriverAccidents }
     *     
     */
    public DriverAccidents getAccidents() {
        return accidents;
    }

    /**
     * Sets the value of the accidents property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverAccidents }
     *     
     */
    public void setAccidents(DriverAccidents value) {
        this.accidents = value;
    }

    /**
     * Gets the value of the otherFee property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherFee property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherFee().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SurchargeAndFee }
     * 
     * 
     */
    public List<SurchargeAndFee> getOtherFee() {
        if (otherFee == null) {
            otherFee = new ArrayList<SurchargeAndFee>();
        }
        return this.otherFee;
    }

    /**
     * Gets the value of the driverSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driverSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriverSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDriverSummaryExtension() {
        if (driverSummaryExtension == null) {
            driverSummaryExtension = new ArrayList<Object>();
        }
        return this.driverSummaryExtension;
    }

}
