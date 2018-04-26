
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAADriver complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAADriver"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAADriverRatingInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAADriverRatingInfo"/&gt;
 *         &lt;element name="AAADrivingLicense" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAADrivingLicense" minOccurs="0"/&gt;
 *         &lt;element name="AAASSDrivingRecord" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAASSDrivingRecord" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="adbCoverageInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="age" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="AZ_ADBEEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_ADBEEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="AZ_NDEEEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_NDEEEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="AZ_SR22FREndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_SR22FREndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="carrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="carrierPolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="driverBaseDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="driverRelToApplicantCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAARelationshipFNInsured" minOccurs="0"/&gt;
 *         &lt;element name="driverTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAADriverType"/&gt;
 *         &lt;element name="employeeBenefitType" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAEmployeeBenefitCd" minOccurs="0"/&gt;
 *         &lt;element name="employeeInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="filingStateCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAFinancialResponsibilityStateProvCd" minOccurs="0"/&gt;
 *         &lt;element name="fillingTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAFillingType" minOccurs="0"/&gt;
 *         &lt;element name="financialResponsibilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="firstLicenseAge" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="genderCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}GenderCd" minOccurs="0"/&gt;
 *         &lt;element name="genderText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="insuredOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nameDisplayValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="maritalStatusCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAMaritalStatusCd" minOccurs="0"/&gt;
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ncnuPolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nonDriverReason" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAADriverTypeReason" minOccurs="0"/&gt;
 *         &lt;element name="occupationTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAOccupation" minOccurs="0"/&gt;
 *         &lt;element name="otherCarrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="prefilled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="showDriverReasonConversion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="suffix" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAASuffix" minOccurs="0"/&gt;
 *         &lt;element name="tyde" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="specificDisabilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="totalDisabiltyInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
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
@XmlType(name = "AAADriver", propOrder = {
    "aaaDriverRatingInfo",
    "aaaDrivingLicense",
    "aaassDrivingRecord",
    "adbCoverageInd",
    "age",
    "azadbeEndorsementForm",
    "azndeeEndorsementForm",
    "azsr22FREndorsementForm",
    "birthDate",
    "carrierName",
    "carrierPolicyNum",
    "driverBaseDt",
    "driverRelToApplicantCd",
    "driverTypeCd",
    "employeeBenefitType",
    "employeeInd",
    "filingStateCd",
    "fillingTypeCd",
    "financialResponsibilityInd",
    "firstLicenseAge",
    "firstName",
    "genderCd",
    "genderText",
    "insuredOid",
    "nameDisplayValue",
    "lastName",
    "maritalStatusCd",
    "middleName",
    "ncnuPolicyNum",
    "nonDriverReason",
    "occupationTypeCd",
    "otherCarrierName",
    "prefilled",
    "seqNo",
    "showDriverReasonConversion",
    "suffix",
    "tyde",
    "specificDisabilityInd",
    "totalDisabiltyInd"
})
public class AAADriver {

    @XmlElement(name = "AAADriverRatingInfo", required = true)
    protected AAADriverRatingInfo aaaDriverRatingInfo;
    @XmlElement(name = "AAADrivingLicense")
    protected AAADrivingLicense aaaDrivingLicense;
    @XmlElement(name = "AAASSDrivingRecord")
    protected List<AAASSDrivingRecord> aaassDrivingRecord;
    protected Boolean adbCoverageInd;
    protected BigDecimal age;
    @XmlElement(name = "AZ_ADBEEndorsementForm")
    protected AZADBEEndorsementForm azadbeEndorsementForm;
    @XmlElement(name = "AZ_NDEEEndorsementForm")
    protected AZNDEEEndorsementForm azndeeEndorsementForm;
    @XmlElement(name = "AZ_SR22FREndorsementForm")
    protected AZSR22FREndorsementForm azsr22FREndorsementForm;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected String carrierName;
    protected String carrierPolicyNum;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar driverBaseDt;
    @XmlSchemaType(name = "string")
    protected AAARelationshipFNInsured driverRelToApplicantCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAADriverType driverTypeCd;
    @XmlSchemaType(name = "string")
    protected AAAEmployeeBenefitCd employeeBenefitType;
    protected Boolean employeeInd;
    protected String filingStateCd;
    @XmlSchemaType(name = "string")
    protected AAAFillingType fillingTypeCd;
    protected boolean financialResponsibilityInd;
    protected BigDecimal firstLicenseAge;
    protected String firstName;
    @XmlSchemaType(name = "string")
    protected GenderCd genderCd;
    protected String genderText;
    protected String insuredOid;
    protected String nameDisplayValue;
    protected String lastName;
    @XmlSchemaType(name = "string")
    protected AAAMaritalStatusCd maritalStatusCd;
    protected String middleName;
    protected String ncnuPolicyNum;
    @XmlSchemaType(name = "string")
    protected AAADriverTypeReason nonDriverReason;
    @XmlSchemaType(name = "string")
    protected AAAOccupation occupationTypeCd;
    protected String otherCarrierName;
    protected Boolean prefilled;
    protected BigDecimal seqNo;
    protected Boolean showDriverReasonConversion;
    @XmlSchemaType(name = "string")
    protected AAASuffix suffix;
    protected BigDecimal tyde;
    protected Boolean specificDisabilityInd;
    protected Boolean totalDisabiltyInd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the aaaDriverRatingInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AAADriverRatingInfo }
     *     
     */
    public AAADriverRatingInfo getAAADriverRatingInfo() {
        return aaaDriverRatingInfo;
    }

    /**
     * Sets the value of the aaaDriverRatingInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAADriverRatingInfo }
     *     
     */
    public void setAAADriverRatingInfo(AAADriverRatingInfo value) {
        this.aaaDriverRatingInfo = value;
    }

    /**
     * Gets the value of the aaaDrivingLicense property.
     * 
     * @return
     *     possible object is
     *     {@link AAADrivingLicense }
     *     
     */
    public AAADrivingLicense getAAADrivingLicense() {
        return aaaDrivingLicense;
    }

    /**
     * Sets the value of the aaaDrivingLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAADrivingLicense }
     *     
     */
    public void setAAADrivingLicense(AAADrivingLicense value) {
        this.aaaDrivingLicense = value;
    }

    /**
     * Gets the value of the aaassDrivingRecord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaassDrivingRecord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAASSDrivingRecord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAASSDrivingRecord }
     * 
     * 
     */
    public List<AAASSDrivingRecord> getAAASSDrivingRecord() {
        if (aaassDrivingRecord == null) {
            aaassDrivingRecord = new ArrayList<AAASSDrivingRecord>();
        }
        return this.aaassDrivingRecord;
    }

    /**
     * Gets the value of the adbCoverageInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdbCoverageInd() {
        return adbCoverageInd;
    }

    /**
     * Sets the value of the adbCoverageInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdbCoverageInd(Boolean value) {
        this.adbCoverageInd = value;
    }

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAge(BigDecimal value) {
        this.age = value;
    }

    /**
     * Gets the value of the azadbeEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZADBEEndorsementForm }
     *     
     */
    public AZADBEEndorsementForm getAZADBEEndorsementForm() {
        return azadbeEndorsementForm;
    }

    /**
     * Sets the value of the azadbeEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZADBEEndorsementForm }
     *     
     */
    public void setAZADBEEndorsementForm(AZADBEEndorsementForm value) {
        this.azadbeEndorsementForm = value;
    }

    /**
     * Gets the value of the azndeeEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZNDEEEndorsementForm }
     *     
     */
    public AZNDEEEndorsementForm getAZNDEEEndorsementForm() {
        return azndeeEndorsementForm;
    }

    /**
     * Sets the value of the azndeeEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZNDEEEndorsementForm }
     *     
     */
    public void setAZNDEEEndorsementForm(AZNDEEEndorsementForm value) {
        this.azndeeEndorsementForm = value;
    }

    /**
     * Gets the value of the azsr22FREndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZSR22FREndorsementForm }
     *     
     */
    public AZSR22FREndorsementForm getAZSR22FREndorsementForm() {
        return azsr22FREndorsementForm;
    }

    /**
     * Sets the value of the azsr22FREndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZSR22FREndorsementForm }
     *     
     */
    public void setAZSR22FREndorsementForm(AZSR22FREndorsementForm value) {
        this.azsr22FREndorsementForm = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the carrierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrierName() {
        return carrierName;
    }

    /**
     * Sets the value of the carrierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrierName(String value) {
        this.carrierName = value;
    }

    /**
     * Gets the value of the carrierPolicyNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrierPolicyNum() {
        return carrierPolicyNum;
    }

    /**
     * Sets the value of the carrierPolicyNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrierPolicyNum(String value) {
        this.carrierPolicyNum = value;
    }

    /**
     * Gets the value of the driverBaseDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDriverBaseDt() {
        return driverBaseDt;
    }

    /**
     * Sets the value of the driverBaseDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDriverBaseDt(XMLGregorianCalendar value) {
        this.driverBaseDt = value;
    }

    /**
     * Gets the value of the driverRelToApplicantCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAARelationshipFNInsured }
     *     
     */
    public AAARelationshipFNInsured getDriverRelToApplicantCd() {
        return driverRelToApplicantCd;
    }

    /**
     * Sets the value of the driverRelToApplicantCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAARelationshipFNInsured }
     *     
     */
    public void setDriverRelToApplicantCd(AAARelationshipFNInsured value) {
        this.driverRelToApplicantCd = value;
    }

    /**
     * Gets the value of the driverTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAADriverType }
     *     
     */
    public AAADriverType getDriverTypeCd() {
        return driverTypeCd;
    }

    /**
     * Sets the value of the driverTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAADriverType }
     *     
     */
    public void setDriverTypeCd(AAADriverType value) {
        this.driverTypeCd = value;
    }

    /**
     * Gets the value of the employeeBenefitType property.
     * 
     * @return
     *     possible object is
     *     {@link AAAEmployeeBenefitCd }
     *     
     */
    public AAAEmployeeBenefitCd getEmployeeBenefitType() {
        return employeeBenefitType;
    }

    /**
     * Sets the value of the employeeBenefitType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAEmployeeBenefitCd }
     *     
     */
    public void setEmployeeBenefitType(AAAEmployeeBenefitCd value) {
        this.employeeBenefitType = value;
    }

    /**
     * Gets the value of the employeeInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmployeeInd() {
        return employeeInd;
    }

    /**
     * Sets the value of the employeeInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmployeeInd(Boolean value) {
        this.employeeInd = value;
    }

    /**
     * Gets the value of the filingStateCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilingStateCd() {
        return filingStateCd;
    }

    /**
     * Sets the value of the filingStateCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilingStateCd(String value) {
        this.filingStateCd = value;
    }

    /**
     * Gets the value of the fillingTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAFillingType }
     *     
     */
    public AAAFillingType getFillingTypeCd() {
        return fillingTypeCd;
    }

    /**
     * Sets the value of the fillingTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAFillingType }
     *     
     */
    public void setFillingTypeCd(AAAFillingType value) {
        this.fillingTypeCd = value;
    }

    /**
     * Gets the value of the financialResponsibilityInd property.
     * 
     */
    public boolean isFinancialResponsibilityInd() {
        return financialResponsibilityInd;
    }

    /**
     * Sets the value of the financialResponsibilityInd property.
     * 
     */
    public void setFinancialResponsibilityInd(boolean value) {
        this.financialResponsibilityInd = value;
    }

    /**
     * Gets the value of the firstLicenseAge property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFirstLicenseAge() {
        return firstLicenseAge;
    }

    /**
     * Sets the value of the firstLicenseAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFirstLicenseAge(BigDecimal value) {
        this.firstLicenseAge = value;
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
     * Gets the value of the genderCd property.
     * 
     * @return
     *     possible object is
     *     {@link GenderCd }
     *     
     */
    public GenderCd getGenderCd() {
        return genderCd;
    }

    /**
     * Sets the value of the genderCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderCd }
     *     
     */
    public void setGenderCd(GenderCd value) {
        this.genderCd = value;
    }

    /**
     * Gets the value of the genderText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenderText() {
        return genderText;
    }

    /**
     * Sets the value of the genderText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenderText(String value) {
        this.genderText = value;
    }

    /**
     * Gets the value of the insuredOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredOid() {
        return insuredOid;
    }

    /**
     * Sets the value of the insuredOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredOid(String value) {
        this.insuredOid = value;
    }

    /**
     * Gets the value of the nameDisplayValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameDisplayValue() {
        return nameDisplayValue;
    }

    /**
     * Sets the value of the nameDisplayValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameDisplayValue(String value) {
        this.nameDisplayValue = value;
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
     * Gets the value of the ncnuPolicyNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNcnuPolicyNum() {
        return ncnuPolicyNum;
    }

    /**
     * Sets the value of the ncnuPolicyNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNcnuPolicyNum(String value) {
        this.ncnuPolicyNum = value;
    }

    /**
     * Gets the value of the nonDriverReason property.
     * 
     * @return
     *     possible object is
     *     {@link AAADriverTypeReason }
     *     
     */
    public AAADriverTypeReason getNonDriverReason() {
        return nonDriverReason;
    }

    /**
     * Sets the value of the nonDriverReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAADriverTypeReason }
     *     
     */
    public void setNonDriverReason(AAADriverTypeReason value) {
        this.nonDriverReason = value;
    }

    /**
     * Gets the value of the occupationTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOccupation }
     *     
     */
    public AAAOccupation getOccupationTypeCd() {
        return occupationTypeCd;
    }

    /**
     * Sets the value of the occupationTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOccupation }
     *     
     */
    public void setOccupationTypeCd(AAAOccupation value) {
        this.occupationTypeCd = value;
    }

    /**
     * Gets the value of the otherCarrierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherCarrierName() {
        return otherCarrierName;
    }

    /**
     * Sets the value of the otherCarrierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherCarrierName(String value) {
        this.otherCarrierName = value;
    }

    /**
     * Gets the value of the prefilled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrefilled() {
        return prefilled;
    }

    /**
     * Sets the value of the prefilled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrefilled(Boolean value) {
        this.prefilled = value;
    }

    /**
     * Gets the value of the seqNo property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSeqNo() {
        return seqNo;
    }

    /**
     * Sets the value of the seqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSeqNo(BigDecimal value) {
        this.seqNo = value;
    }

    /**
     * Gets the value of the showDriverReasonConversion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowDriverReasonConversion() {
        return showDriverReasonConversion;
    }

    /**
     * Sets the value of the showDriverReasonConversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowDriverReasonConversion(Boolean value) {
        this.showDriverReasonConversion = value;
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
     * Gets the value of the tyde property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTyde() {
        return tyde;
    }

    /**
     * Sets the value of the tyde property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTyde(BigDecimal value) {
        this.tyde = value;
    }

    /**
     * Gets the value of the specificDisabilityInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSpecificDisabilityInd() {
        return specificDisabilityInd;
    }

    /**
     * Sets the value of the specificDisabilityInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSpecificDisabilityInd(Boolean value) {
        this.specificDisabilityInd = value;
    }

    /**
     * Gets the value of the totalDisabiltyInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTotalDisabiltyInd() {
        return totalDisabiltyInd;
    }

    /**
     * Sets the value of the totalDisabiltyInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTotalDisabiltyInd(Boolean value) {
        this.totalDisabiltyInd = value;
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
