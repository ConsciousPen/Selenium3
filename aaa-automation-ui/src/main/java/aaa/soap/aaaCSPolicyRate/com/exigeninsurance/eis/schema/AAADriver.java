
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.*;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

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
 *         &lt;element name="AAADriverRatingInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAADriverRatingInfo"/&gt;
 *         &lt;element name="AAADrivingLicense" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAADrivingLicense"/&gt;
 *         &lt;element name="AAADrivingRecord" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAADrivingRecord" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="adbCoverageInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="ADBEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}ADBEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="age" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="carrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="carrierPolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CIPCS22EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}CIPCS22EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="CIPCSR1PEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}CIPCSR1PEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="clueReportStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="driverBaseDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="driverDisabledInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="driverRelToApplicantCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAARelationshipFNInsured" minOccurs="0"/&gt;
 *         &lt;element name="driverTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAADriverType"/&gt;
 *         &lt;element name="employeeBenefitType" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAEmployeeBenefitCd" minOccurs="0"/&gt;
 *         &lt;element name="employeeNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="employerDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fillingTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAFillingType" minOccurs="0"/&gt;
 *         &lt;element name="financialResponsibilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="firstLicenseAge" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fullTimeEmploymentInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="genderCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}GenderCd" minOccurs="0"/&gt;
 *         &lt;element name="insuredOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="maritalStatusCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAMaritalStatusCd" minOccurs="0"/&gt;
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mvrReportStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ncnuPolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NDEUMDLEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}NDEUMDLEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="nonDriverReason" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAADriverTypeReason" minOccurs="0"/&gt;
 *         &lt;element name="occupationDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="occupationTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAOccupation" minOccurs="0"/&gt;
 *         &lt;element name="orderClue" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="orderMVR" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="otherCarrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherOccupation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="permitInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="POPEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}POPEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="titleCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tyde" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="ridesharingCovDriver" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ridesharingIncome" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Choice_AA43CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA43CAEndorsementForm" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="oid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    "aaaDrivingRecord",
    "adbCoverageInd",
    "adbEndorsementForm",
    "age",
    "birthDate",
    "carrierName",
    "carrierPolicyNum",
    "cipcs22EndorsementForm",
    "cipcsr1PEndorsementForm",
    "clueReportStatus",
    "driverBaseDt",
    "driverDisabledInd",
    "driverRelToApplicantCd",
    "driverTypeCd",
    "employeeBenefitType",
    "employeeNumber",
    "employerDesc",
    "fillingTypeCd",
    "financialResponsibilityInd",
    "firstLicenseAge",
    "firstName",
    "fullTimeEmploymentInd",
    "genderCd",
    "insuredOid",
    "lastName",
    "maritalStatusCd",
    "middleName",
    "mvrReportStatus",
    "ncnuPolicyNum",
    "ndeumdlEndorsementForm",
    "nonDriverReason",
    "occupationDesc",
    "occupationTypeCd",
    "orderClue",
    "orderMVR",
    "otherCarrierName",
    "otherOccupation",
    "permitInd",
    "popEndorsementForm",
    "seqNo",
    "suffix",
    "titleCd",
    "tyde",
    "ridesharingCovDriver",
    "ridesharingIncome",
    "choiceAA43CAEndorsementForm"
})
public class AAADriver {

    @XmlElement(name = "AAADriverRatingInfo", required = true)
    protected AAADriverRatingInfo aaaDriverRatingInfo;
    @XmlElement(name = "AAADrivingLicense", required = true)
    protected AAADrivingLicense aaaDrivingLicense;
    @XmlElement(name = "AAADrivingRecord")
    protected List<AAADrivingRecord> aaaDrivingRecord;
    protected boolean adbCoverageInd;
    @XmlElement(name = "ADBEndorsementForm")
    protected ADBEndorsementForm adbEndorsementForm;
    protected BigDecimal age;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected String carrierName;
    protected String carrierPolicyNum;
    @XmlElement(name = "CIPCS22EndorsementForm")
    protected CIPCS22EndorsementForm cipcs22EndorsementForm;
    @XmlElement(name = "CIPCSR1PEndorsementForm")
    protected CIPCSR1PEndorsementForm cipcsr1PEndorsementForm;
    protected String clueReportStatus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar driverBaseDt;
    protected Boolean driverDisabledInd;
    @XmlSchemaType(name = "string")
    protected AAARelationshipFNInsured driverRelToApplicantCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAADriverType driverTypeCd;
    @XmlSchemaType(name = "string")
    protected AAAEmployeeBenefitCd employeeBenefitType;
    protected String employeeNumber;
    protected String employerDesc;
    @XmlSchemaType(name = "string")
    protected AAAFillingType fillingTypeCd;
    protected boolean financialResponsibilityInd;
    protected BigDecimal firstLicenseAge;
    @XmlElement(required = true)
    protected String firstName;
    protected Boolean fullTimeEmploymentInd;
    @XmlSchemaType(name = "string")
    protected GenderCd genderCd;
    protected String insuredOid;
    @XmlElement(required = true)
    protected String lastName;
    @XmlSchemaType(name = "string")
    protected AAAMaritalStatusCd maritalStatusCd;
    protected String middleName;
    protected String mvrReportStatus;
    protected String ncnuPolicyNum;
    @XmlElement(name = "NDEUMDLEndorsementForm")
    protected NDEUMDLEndorsementForm ndeumdlEndorsementForm;
    @XmlSchemaType(name = "string")
    protected AAADriverTypeReason nonDriverReason;
    protected String occupationDesc;
    @XmlSchemaType(name = "string")
    protected AAAOccupation occupationTypeCd;
    protected Boolean orderClue;
    protected Boolean orderMVR;
    protected String otherCarrierName;
    protected String otherOccupation;
    protected Boolean permitInd;
    @XmlElement(name = "POPEndorsementForm")
    protected POPEndorsementForm popEndorsementForm;
    protected BigDecimal seqNo;
    protected String suffix;
    protected String titleCd;
    protected BigDecimal tyde;
    protected Boolean ridesharingCovDriver;
    protected Boolean ridesharingIncome;
    @XmlElement(name = "Choice_AA43CAEndorsementForm")
    protected ChoiceAA43CAEndorsementForm choiceAA43CAEndorsementForm;
    @XmlAttribute(name = "oid", required = true)
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
     * Gets the value of the aaaDrivingRecord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaDrivingRecord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAADrivingRecord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAADrivingRecord }
     * 
     * 
     */
    public List<AAADrivingRecord> getAAADrivingRecord() {
        if (aaaDrivingRecord == null) {
            aaaDrivingRecord = new ArrayList<AAADrivingRecord>();
        }
        return this.aaaDrivingRecord;
    }

    public void setAAADrivingRecord(List<AAADrivingRecord> aaaDrivingRecord) {
        this.aaaDrivingRecord = aaaDrivingRecord;
    }

    /**
     * Gets the value of the adbCoverageInd property.
     * 
     */
    public boolean isAdbCoverageInd() {
        return adbCoverageInd;
    }

    /**
     * Sets the value of the adbCoverageInd property.
     * 
     */
    public void setAdbCoverageInd(boolean value) {
        this.adbCoverageInd = value;
    }

    /**
     * Gets the value of the adbEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ADBEndorsementForm }
     *     
     */
    public ADBEndorsementForm getADBEndorsementForm() {
        return adbEndorsementForm;
    }

    /**
     * Sets the value of the adbEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADBEndorsementForm }
     *     
     */
    public void setADBEndorsementForm(ADBEndorsementForm value) {
        this.adbEndorsementForm = value;
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
     * Gets the value of the cipcs22EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link CIPCS22EndorsementForm }
     *     
     */
    public CIPCS22EndorsementForm getCIPCS22EndorsementForm() {
        return cipcs22EndorsementForm;
    }

    /**
     * Sets the value of the cipcs22EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link CIPCS22EndorsementForm }
     *     
     */
    public void setCIPCS22EndorsementForm(CIPCS22EndorsementForm value) {
        this.cipcs22EndorsementForm = value;
    }

    /**
     * Gets the value of the cipcsr1PEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link CIPCSR1PEndorsementForm }
     *     
     */
    public CIPCSR1PEndorsementForm getCIPCSR1PEndorsementForm() {
        return cipcsr1PEndorsementForm;
    }

    /**
     * Sets the value of the cipcsr1PEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link CIPCSR1PEndorsementForm }
     *     
     */
    public void setCIPCSR1PEndorsementForm(CIPCSR1PEndorsementForm value) {
        this.cipcsr1PEndorsementForm = value;
    }

    /**
     * Gets the value of the clueReportStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClueReportStatus() {
        return clueReportStatus;
    }

    /**
     * Sets the value of the clueReportStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClueReportStatus(String value) {
        this.clueReportStatus = value;
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
     * Gets the value of the driverDisabledInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDriverDisabledInd() {
        return driverDisabledInd;
    }

    /**
     * Sets the value of the driverDisabledInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDriverDisabledInd(Boolean value) {
        this.driverDisabledInd = value;
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
     * Gets the value of the fullTimeEmploymentInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFullTimeEmploymentInd() {
        return fullTimeEmploymentInd;
    }

    /**
     * Sets the value of the fullTimeEmploymentInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFullTimeEmploymentInd(Boolean value) {
        this.fullTimeEmploymentInd = value;
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
     * Gets the value of the insuredOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getinsuredOid() {
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
    public void setinsuredOid(String value) {
        this.insuredOid = value;
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
     * Gets the value of the mvrReportStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMvrReportStatus() {
        return mvrReportStatus;
    }

    /**
     * Sets the value of the mvrReportStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMvrReportStatus(String value) {
        this.mvrReportStatus = value;
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
     * Gets the value of the ndeumdlEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link NDEUMDLEndorsementForm }
     *     
     */
    public NDEUMDLEndorsementForm getNDEUMDLEndorsementForm() {
        return ndeumdlEndorsementForm;
    }

    /**
     * Sets the value of the ndeumdlEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link NDEUMDLEndorsementForm }
     *     
     */
    public void setNDEUMDLEndorsementForm(NDEUMDLEndorsementForm value) {
        this.ndeumdlEndorsementForm = value;
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
     * Gets the value of the orderClue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOrderClue() {
        return orderClue;
    }

    /**
     * Sets the value of the orderClue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOrderClue(Boolean value) {
        this.orderClue = value;
    }

    /**
     * Gets the value of the orderMVR property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOrderMVR() {
        return orderMVR;
    }

    /**
     * Sets the value of the orderMVR property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOrderMVR(Boolean value) {
        this.orderMVR = value;
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
     * Gets the value of the otherOccupation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherOccupation() {
        return otherOccupation;
    }

    /**
     * Sets the value of the otherOccupation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherOccupation(String value) {
        this.otherOccupation = value;
    }

    /**
     * Gets the value of the permitInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPermitInd() {
        return permitInd;
    }

    /**
     * Sets the value of the permitInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPermitInd(Boolean value) {
        this.permitInd = value;
    }

    /**
     * Gets the value of the popEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link POPEndorsementForm }
     *     
     */
    public POPEndorsementForm getPOPEndorsementForm() {
        return popEndorsementForm;
    }

    /**
     * Sets the value of the popEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link POPEndorsementForm }
     *     
     */
    public void setPOPEndorsementForm(POPEndorsementForm value) {
        this.popEndorsementForm = value;
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
     * Gets the value of the ridesharingCovDriver property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRidesharingCovDriver() {
        return ridesharingCovDriver;
    }

    /**
     * Sets the value of the ridesharingCovDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRidesharingCovDriver(Boolean value) {
        this.ridesharingCovDriver = value;
    }

    /**
     * Gets the value of the ridesharingIncome property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRidesharingIncome() {
        return ridesharingIncome;
    }

    /**
     * Sets the value of the ridesharingIncome property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRidesharingIncome(Boolean value) {
        this.ridesharingIncome = value;
    }

    /**
     * Gets the value of the choiceAA43CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA43CAEndorsementForm }
     *     
     */
    public ChoiceAA43CAEndorsementForm getChoiceAA43CAEndorsementForm() {
        return choiceAA43CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA43CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA43CAEndorsementForm }
     *     
     */
    public void setChoiceAA43CAEndorsementForm(ChoiceAA43CAEndorsementForm value) {
        this.choiceAA43CAEndorsementForm = value;
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
