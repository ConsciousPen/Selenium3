
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.PIPCOVINCLUDES;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.PIPPRIMINS;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAAPIPCoverageInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAPIPCoverageInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="addtnlPersonalInjProtectnBenf" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="coverageIncludes" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPCOVINCLUDES" minOccurs="0"/&gt;
 *         &lt;element name="extendedMedicalPayments" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPEXTMEDPM" minOccurs="0"/&gt;
 *         &lt;element name="insurerName1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="insurerName2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lengthOfIncomeContinuation" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPLENINCCONT" minOccurs="0"/&gt;
 *         &lt;element name="medicalExpense" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPMEDEXP" minOccurs="0"/&gt;
 *         &lt;element name="medicalExpensedeDuctible" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPMEDEXPDED" minOccurs="0"/&gt;
 *         &lt;element name="nonMedicalExpense" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="policyOrGroupOrCertificateNum1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="policyOrGroupOrCertificateNum2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="primaryInsurer" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPPRIMINS" minOccurs="0"/&gt;
 *         &lt;element name="relativeName1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="relativeName2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="relativeName3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="relativeName4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="relativeName5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="relativeName6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="weeklyIncomeContinuationBenf" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPMAXINCCONT" minOccurs="0"/&gt;
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
@XmlType(name = "AAAPIPCoverageInfoType", propOrder = {
    "addtnlPersonalInjProtectnBenf",
    "coverageIncludes",
    "extendedMedicalPayments",
    "insurerName1",
    "insurerName2",
    "lengthOfIncomeContinuation",
    "medicalExpense",
    "medicalExpensedeDuctible",
    "nonMedicalExpense",
    "policyOrGroupOrCertificateNum1",
    "policyOrGroupOrCertificateNum2",
    "primaryInsurer",
    "relativeName1",
    "relativeName2",
    "relativeName3",
    "relativeName4",
    "relativeName5",
    "relativeName6",
    "weeklyIncomeContinuationBenf"
})
public class AAAPIPCoverageInfoType {

    protected Boolean addtnlPersonalInjProtectnBenf;
    @XmlSchemaType(name = "string")
    protected PIPCOVINCLUDES coverageIncludes;
    protected String extendedMedicalPayments;
    protected String insurerName1;
    protected String insurerName2;
    protected String lengthOfIncomeContinuation;
    protected String medicalExpense;
    protected String medicalExpensedeDuctible;
    protected Boolean nonMedicalExpense;
    protected String policyOrGroupOrCertificateNum1;
    protected String policyOrGroupOrCertificateNum2;
    @XmlSchemaType(name = "string")
    protected PIPPRIMINS primaryInsurer;
    protected String relativeName1;
    protected String relativeName2;
    protected String relativeName3;
    protected String relativeName4;
    protected String relativeName5;
    protected String relativeName6;
    protected String weeklyIncomeContinuationBenf;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the addtnlPersonalInjProtectnBenf property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAddtnlPersonalInjProtectnBenf() {
        return addtnlPersonalInjProtectnBenf;
    }

    /**
     * Sets the value of the addtnlPersonalInjProtectnBenf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAddtnlPersonalInjProtectnBenf(Boolean value) {
        this.addtnlPersonalInjProtectnBenf = value;
    }

    /**
     * Gets the value of the coverageIncludes property.
     * 
     * @return
     *     possible object is
     *     {@link PIPCOVINCLUDES }
     *     
     */
    public PIPCOVINCLUDES getCoverageIncludes() {
        return coverageIncludes;
    }

    /**
     * Sets the value of the coverageIncludes property.
     * 
     * @param value
     *     allowed object is
     *     {@link PIPCOVINCLUDES }
     *     
     */
    public void setCoverageIncludes(PIPCOVINCLUDES value) {
        this.coverageIncludes = value;
    }

    /**
     * Gets the value of the extendedMedicalPayments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedMedicalPayments() {
        return extendedMedicalPayments;
    }

    /**
     * Sets the value of the extendedMedicalPayments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedMedicalPayments(String value) {
        this.extendedMedicalPayments = value;
    }

    /**
     * Gets the value of the insurerName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerName1() {
        return insurerName1;
    }

    /**
     * Sets the value of the insurerName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerName1(String value) {
        this.insurerName1 = value;
    }

    /**
     * Gets the value of the insurerName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerName2() {
        return insurerName2;
    }

    /**
     * Sets the value of the insurerName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerName2(String value) {
        this.insurerName2 = value;
    }

    /**
     * Gets the value of the lengthOfIncomeContinuation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLengthOfIncomeContinuation() {
        return lengthOfIncomeContinuation;
    }

    /**
     * Sets the value of the lengthOfIncomeContinuation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLengthOfIncomeContinuation(String value) {
        this.lengthOfIncomeContinuation = value;
    }

    /**
     * Gets the value of the medicalExpense property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalExpense() {
        return medicalExpense;
    }

    /**
     * Sets the value of the medicalExpense property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalExpense(String value) {
        this.medicalExpense = value;
    }

    /**
     * Gets the value of the medicalExpensedeDuctible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalExpensedeDuctible() {
        return medicalExpensedeDuctible;
    }

    /**
     * Sets the value of the medicalExpensedeDuctible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalExpensedeDuctible(String value) {
        this.medicalExpensedeDuctible = value;
    }

    /**
     * Gets the value of the nonMedicalExpense property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonMedicalExpense() {
        return nonMedicalExpense;
    }

    /**
     * Sets the value of the nonMedicalExpense property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonMedicalExpense(Boolean value) {
        this.nonMedicalExpense = value;
    }

    /**
     * Gets the value of the policyOrGroupOrCertificateNum1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyOrGroupOrCertificateNum1() {
        return policyOrGroupOrCertificateNum1;
    }

    /**
     * Sets the value of the policyOrGroupOrCertificateNum1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyOrGroupOrCertificateNum1(String value) {
        this.policyOrGroupOrCertificateNum1 = value;
    }

    /**
     * Gets the value of the policyOrGroupOrCertificateNum2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyOrGroupOrCertificateNum2() {
        return policyOrGroupOrCertificateNum2;
    }

    /**
     * Sets the value of the policyOrGroupOrCertificateNum2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyOrGroupOrCertificateNum2(String value) {
        this.policyOrGroupOrCertificateNum2 = value;
    }

    /**
     * Gets the value of the primaryInsurer property.
     * 
     * @return
     *     possible object is
     *     {@link PIPPRIMINS }
     *     
     */
    public PIPPRIMINS getPrimaryInsurer() {
        return primaryInsurer;
    }

    /**
     * Sets the value of the primaryInsurer property.
     * 
     * @param value
     *     allowed object is
     *     {@link PIPPRIMINS }
     *     
     */
    public void setPrimaryInsurer(PIPPRIMINS value) {
        this.primaryInsurer = value;
    }

    /**
     * Gets the value of the relativeName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName1() {
        return relativeName1;
    }

    /**
     * Sets the value of the relativeName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName1(String value) {
        this.relativeName1 = value;
    }

    /**
     * Gets the value of the relativeName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName2() {
        return relativeName2;
    }

    /**
     * Sets the value of the relativeName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName2(String value) {
        this.relativeName2 = value;
    }

    /**
     * Gets the value of the relativeName3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName3() {
        return relativeName3;
    }

    /**
     * Sets the value of the relativeName3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName3(String value) {
        this.relativeName3 = value;
    }

    /**
     * Gets the value of the relativeName4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName4() {
        return relativeName4;
    }

    /**
     * Sets the value of the relativeName4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName4(String value) {
        this.relativeName4 = value;
    }

    /**
     * Gets the value of the relativeName5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName5() {
        return relativeName5;
    }

    /**
     * Sets the value of the relativeName5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName5(String value) {
        this.relativeName5 = value;
    }

    /**
     * Gets the value of the relativeName6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelativeName6() {
        return relativeName6;
    }

    /**
     * Sets the value of the relativeName6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelativeName6(String value) {
        this.relativeName6 = value;
    }

    /**
     * Gets the value of the weeklyIncomeContinuationBenf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeklyIncomeContinuationBenf() {
        return weeklyIncomeContinuationBenf;
    }

    /**
     * Sets the value of the weeklyIncomeContinuationBenf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeklyIncomeContinuationBenf(String value) {
        this.weeklyIncomeContinuationBenf = value;
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
