
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.*;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;



/**
 * <p>Java class for AAACSAAutoPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAACSAAutoPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAADriver" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAADriver" maxOccurs="99"/&gt;
 *         &lt;element name="AAAInsured" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAInsured" maxOccurs="unbounded"/&gt;
 *         &lt;element name="AAAMPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAMPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPDCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAPDCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUIMBICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAUIMBICoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUMBICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAUMBICoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicle" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicle" maxOccurs="unbounded"/&gt;
 *         &lt;element name="agentType" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAAgentType" minOccurs="0"/&gt;
 *         &lt;element name="AHTMHEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AHTMHEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="authorizedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}BICoverage" minOccurs="0"/&gt;
 *         &lt;element name="branchCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}Branch" minOccurs="0"/&gt;
 *         &lt;element name="CSAAEEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}CSAAEEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="doNotRenewInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="effective" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="ENOCCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}ENOCCEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="expiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="forceAUNoticeGeneration" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="importedFrom" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}LegacySystem"/&gt;
 *         &lt;element name="inceptionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="officeType" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAOfficeType" minOccurs="0"/&gt;
 *         &lt;element name="OtherOrPriorPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}OtherOrPriorPolicy" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="policyStatusCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PremiumEntry" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="producerCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="productCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="riskStateCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}StateProvCd"/&gt;
 *         &lt;element name="sourcePolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="subProducerCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="agencyCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="agentCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="agentGeneralNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="agencyLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="typeOfBusinessCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}TypeOfBusiness" minOccurs="0"/&gt;
 *         &lt;element name="typeOfPolicyCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAPolicyType" minOccurs="0"/&gt;
 *         &lt;element name="UMCDEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}UMCDEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="UMREndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}UMREndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="underwriterCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tollNumberCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="salesChannelType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AA41CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AA41CAEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="AAAAutoThirdPartyDesigneeComponent" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAAutoThirdPartyDesigneeComponent" minOccurs="0"/&gt;
 *         &lt;element name="AAARideSharingCoverageEndorsement" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAARideSharingCoverageEndorsement" minOccurs="0"/&gt;
 *         &lt;element name="Choice_AA52CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA52CAEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="Choice_AA57CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA57CAEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="contractTermTypeCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="policyFormCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="prevPolicyFormCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "AAACSAAutoPolicy", propOrder = {
    "aaaDriver",
    "aaaInsured",
    "aaampCoverage",
    "aaapdCoverage",
    "aaauimbiCoverage",
    "aaaumbiCoverage",
    "aaaVehicle",
    "agentType",
    "ahtmhEndorsementForm",
    "authorizedBy",
    "biCoverage",
    "branchCd",
    "csaaeEndorsementForm",
    "doNotRenewInd",
    "effective",
    "enoccEndorsementForm",
    "expiration",
    "forceAUNoticeGeneration",
    "importedFrom",
    "inceptionDate",
    "officeType",
    "otherOrPriorPolicy",
    "policyNumber",
    "policyStatusCd",
    "premiumEntry",
    "producerCd",
    "productCd",
    "riskStateCd",
    "sourcePolicyNum",
    "subProducerCd",
    "agencyCd",
    "agentCd",
    "agentGeneralNumber",
    "agencyLocation",
    "typeOfBusinessCd",
    "typeOfPolicyCd",
    "umcdEndorsementForm",
    "umrEndorsementForm",
    "underwriterCd",
    "tollNumberCd",
    "salesChannelType",
    "aa41CAEndorsementForm",
    "aaaAutoThirdPartyDesigneeComponent",
    "aaaRideSharingCoverageEndorsement",
    "choiceAA52CAEndorsementForm",
    "choiceAA57CAEndorsementForm",
    "contractTermTypeCd",
    "policyFormCd",
    "prevPolicyFormCd"
})
public class AAACSAAutoPolicy {

    @XmlElement(name = "AAADriver", required = true)
    protected List<AAADriver> aaaDriver;
    @XmlElement(name = "AAAInsured", required = true)
    protected List<AAAInsured> aaaInsured;
    @XmlElement(name = "AAAMPCoverage")
    protected AAAMPCoverage aaampCoverage;
    @XmlElement(name = "AAAPDCoverage")
    protected AAAPDCoverage aaapdCoverage;
    @XmlElement(name = "AAAUIMBICoverage")
    protected AAAUIMBICoverage aaauimbiCoverage;
    @XmlElement(name = "AAAUMBICoverage")
    protected AAAUMBICoverage aaaumbiCoverage;
    @XmlElement(name = "AAAVehicle", required = true)
    protected List<AAAVehicle> aaaVehicle;
    @XmlSchemaType(name = "string")
    protected AAAAgentType agentType;
    @XmlElement(name = "AHTMHEndorsementForm")
    protected AHTMHEndorsementForm ahtmhEndorsementForm;
    protected String authorizedBy;
    @XmlElement(name = "BICoverage")
    protected BICoverage biCoverage;
    @XmlSchemaType(name = "string")
    protected Branch branchCd;
    @XmlElement(name = "CSAAEEndorsementForm")
    protected CSAAEEndorsementForm csaaeEndorsementForm;
    protected Boolean doNotRenewInd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effective;
    @XmlElement(name = "ENOCCEndorsementForm")
    protected ENOCCEndorsementForm enoccEndorsementForm;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expiration;
    @XmlElement(defaultValue = "false")
    protected Boolean forceAUNoticeGeneration;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LegacySystem importedFrom;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar inceptionDate;
    @XmlSchemaType(name = "string")
    protected AAAOfficeType officeType;
    @XmlElement(name = "OtherOrPriorPolicy")
    protected List<OtherOrPriorPolicy> otherOrPriorPolicy;
    protected String policyNumber;
    protected String policyStatusCd;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    @XmlElement(required = true)
    protected String producerCd;
    @XmlElement(required = true)
    protected String productCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StateProvCd riskStateCd;
    protected String sourcePolicyNum;
    protected String subProducerCd;
    protected String agencyCd;
    protected String agentCd;
    protected String agentGeneralNumber;
    protected String agencyLocation;
    @XmlElement(defaultValue = "STD")
    @XmlSchemaType(name = "string")
    protected TypeOfBusiness typeOfBusinessCd;
    @XmlElement(defaultValue = "STD")
    @XmlSchemaType(name = "string")
    protected AAAPolicyType typeOfPolicyCd;
    @XmlElement(name = "UMCDEndorsementForm")
    protected UMCDEndorsementForm umcdEndorsementForm;
    @XmlElement(name = "UMREndorsementForm")
    protected UMREndorsementForm umrEndorsementForm;
    @XmlElement(defaultValue = "CSAAIB")
    protected String underwriterCd;
    protected String tollNumberCd;
    protected String salesChannelType;
    @XmlElement(name = "AA41CAEndorsementForm")
    protected AA41CAEndorsementForm aa41CAEndorsementForm;
    @XmlElement(name = "AAAAutoThirdPartyDesigneeComponent")
    protected AAAAutoThirdPartyDesigneeComponent aaaAutoThirdPartyDesigneeComponent;
    @XmlElement(name = "AAARideSharingCoverageEndorsement")
    protected AAARideSharingCoverageEndorsement aaaRideSharingCoverageEndorsement;
    @XmlElement(name = "Choice_AA52CAEndorsementForm")
    protected ChoiceAA52CAEndorsementForm choiceAA52CAEndorsementForm;
    @XmlElement(name = "Choice_AA57CAEndorsementForm")
    protected ChoiceAA57CAEndorsementForm choiceAA57CAEndorsementForm;
    protected String contractTermTypeCd;
    protected String policyFormCd;
    protected String prevPolicyFormCd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the aaaDriver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaDriver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAADriver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAADriver }
     * 
     * 
     */
    public List<AAADriver> getAAADriver() {
        if (aaaDriver == null) {
            aaaDriver = new ArrayList<AAADriver>();
        }
        return this.aaaDriver;
    }

    public void setAAADriver(List<AAADriver> aaaDriver) {
        this.aaaDriver = aaaDriver;
    }

    /**
     * Gets the value of the aaaInsured property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaInsured property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAInsured().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAInsured }
     * 
     * 
     */
    public List<AAAInsured> getAAAInsured() {
        if (aaaInsured == null) {
            aaaInsured = new ArrayList<AAAInsured>();
        }
        return this.aaaInsured;
    }

    public void setAAAInsured(List<AAAInsured> aaaInsured) {
        this.aaaInsured = aaaInsured;
    }

    /**
     * Gets the value of the aaampCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAMPCoverage }
     *     
     */
    public AAAMPCoverage getAAAMPCoverage() {
        return aaampCoverage;
    }

    /**
     * Sets the value of the aaampCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAMPCoverage }
     *     
     */
    public void setAAAMPCoverage(AAAMPCoverage value) {
        this.aaampCoverage = value;
    }

    /**
     * Gets the value of the aaapdCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPDCoverage }
     *     
     */
    public AAAPDCoverage getAAAPDCoverage() {
        return aaapdCoverage;
    }

    /**
     * Sets the value of the aaapdCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPDCoverage }
     *     
     */
    public void setAAAPDCoverage(AAAPDCoverage value) {
        this.aaapdCoverage = value;
    }

    /**
     * Gets the value of the aaauimbiCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUIMBICoverage }
     *     
     */
    public AAAUIMBICoverage getAAAUIMBICoverage() {
        return aaauimbiCoverage;
    }

    /**
     * Sets the value of the aaauimbiCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUIMBICoverage }
     *     
     */
    public void setAAAUIMBICoverage(AAAUIMBICoverage value) {
        this.aaauimbiCoverage = value;
    }

    /**
     * Gets the value of the aaaumbiCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUMBICoverage }
     *     
     */
    public AAAUMBICoverage getAAAUMBICoverage() {
        return aaaumbiCoverage;
    }

    /**
     * Sets the value of the aaaumbiCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUMBICoverage }
     *     
     */
    public void setAAAUMBICoverage(AAAUMBICoverage value) {
        this.aaaumbiCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaVehicle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAVehicle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAVehicle }
     * 
     * 
     */
    public List<AAAVehicle> getAAAVehicle() {
        if (aaaVehicle == null) {
            aaaVehicle = new ArrayList<AAAVehicle>();
        }
        return this.aaaVehicle;
    }

    public void setAAAVehicle(List<AAAVehicle> aaaVehicle) {
        this.aaaVehicle = aaaVehicle;
    }

    /**
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link AAAAgentType }
     *     
     */
    public AAAAgentType getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAAgentType }
     *     
     */
    public void setAgentType(AAAAgentType value) {
        this.agentType = value;
    }

    /**
     * Gets the value of the ahtmhEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AHTMHEndorsementForm }
     *     
     */
    public AHTMHEndorsementForm getAHTMHEndorsementForm() {
        return ahtmhEndorsementForm;
    }

    /**
     * Sets the value of the ahtmhEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AHTMHEndorsementForm }
     *     
     */
    public void setAHTMHEndorsementForm(AHTMHEndorsementForm value) {
        this.ahtmhEndorsementForm = value;
    }

    /**
     * Gets the value of the authorizedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizedBy() {
        return authorizedBy;
    }

    /**
     * Sets the value of the authorizedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizedBy(String value) {
        this.authorizedBy = value;
    }

    /**
     * Gets the value of the biCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link BICoverage }
     *     
     */
    public BICoverage getBICoverage() {
        return biCoverage;
    }

    /**
     * Sets the value of the biCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BICoverage }
     *     
     */
    public void setBICoverage(BICoverage value) {
        this.biCoverage = value;
    }

    /**
     * Gets the value of the branchCd property.
     * 
     * @return
     *     possible object is
     *     {@link Branch }
     *     
     */
    public Branch getBranchCd() {
        return branchCd;
    }

    /**
     * Sets the value of the branchCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Branch }
     *     
     */
    public void setBranchCd(Branch value) {
        this.branchCd = value;
    }

    /**
     * Gets the value of the csaaeEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link CSAAEEndorsementForm }
     *     
     */
    public CSAAEEndorsementForm getCSAAEEndorsementForm() {
        return csaaeEndorsementForm;
    }

    /**
     * Sets the value of the csaaeEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link CSAAEEndorsementForm }
     *     
     */
    public void setCSAAEEndorsementForm(CSAAEEndorsementForm value) {
        this.csaaeEndorsementForm = value;
    }

    /**
     * Gets the value of the doNotRenewInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDoNotRenewInd() {
        return doNotRenewInd;
    }

    /**
     * Sets the value of the doNotRenewInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDoNotRenewInd(Boolean value) {
        this.doNotRenewInd = value;
    }

    /**
     * Gets the value of the effective property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffective() {
        return effective;
    }

    /**
     * Sets the value of the effective property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffective(XMLGregorianCalendar value) {
        this.effective = value;
    }

    /**
     * Gets the value of the enoccEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ENOCCEndorsementForm }
     *     
     */
    public ENOCCEndorsementForm getENOCCEndorsementForm() {
        return enoccEndorsementForm;
    }

    /**
     * Sets the value of the enoccEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ENOCCEndorsementForm }
     *     
     */
    public void setENOCCEndorsementForm(ENOCCEndorsementForm value) {
        this.enoccEndorsementForm = value;
    }

    /**
     * Gets the value of the expiration property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiration() {
        return expiration;
    }

    /**
     * Sets the value of the expiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiration(XMLGregorianCalendar value) {
        this.expiration = value;
    }

    /**
     * Gets the value of the forceAUNoticeGeneration property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isForceAUNoticeGeneration() {
        return forceAUNoticeGeneration;
    }

    /**
     * Sets the value of the forceAUNoticeGeneration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setForceAUNoticeGeneration(Boolean value) {
        this.forceAUNoticeGeneration = value;
    }

    /**
     * Gets the value of the importedFrom property.
     * 
     * @return
     *     possible object is
     *     {@link LegacySystem }
     *     
     */
    public LegacySystem getImportedFrom() {
        return importedFrom;
    }

    /**
     * Sets the value of the importedFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link LegacySystem }
     *     
     */
    public void setImportedFrom(LegacySystem value) {
        this.importedFrom = value;
    }

    /**
     * Gets the value of the inceptionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInceptionDate() {
        return inceptionDate;
    }

    /**
     * Sets the value of the inceptionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInceptionDate(XMLGregorianCalendar value) {
        this.inceptionDate = value;
    }

    /**
     * Gets the value of the officeType property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOfficeType }
     *     
     */
    public AAAOfficeType getOfficeType() {
        return officeType;
    }

    /**
     * Sets the value of the officeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOfficeType }
     *     
     */
    public void setOfficeType(AAAOfficeType value) {
        this.officeType = value;
    }

    /**
     * Gets the value of the otherOrPriorPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherOrPriorPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherOrPriorPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OtherOrPriorPolicy }
     * 
     * 
     */
    public List<OtherOrPriorPolicy> getOtherOrPriorPolicy() {
        if (otherOrPriorPolicy == null) {
            otherOrPriorPolicy = new ArrayList<OtherOrPriorPolicy>();
        }
        return this.otherOrPriorPolicy;
    }

    public void setOtherOrPriorPolicy(List<OtherOrPriorPolicy> otherOrPriorPolicy) {
        this.otherOrPriorPolicy = otherOrPriorPolicy;
    }

    /**
     * Gets the value of the policyNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Sets the value of the policyNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyNumber(String value) {
        this.policyNumber = value;
    }

    /**
     * Gets the value of the policyStatusCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyStatusCd() {
        return policyStatusCd;
    }

    /**
     * Sets the value of the policyStatusCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyStatusCd(String value) {
        this.policyStatusCd = value;
    }

    /**
     * Gets the value of the premiumEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiumEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiumEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiumEntry }
     * 
     * 
     */
    public List<PremiumEntry> getPremiumEntry() {
        if (premiumEntry == null) {
            premiumEntry = new ArrayList<PremiumEntry>();
        }
        return this.premiumEntry;
    }

    /**
     * Gets the value of the producerCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProducerCd() {
        return producerCd;
    }

    /**
     * Sets the value of the producerCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProducerCd(String value) {
        this.producerCd = value;
    }

    /**
     * Gets the value of the productCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCd() {
        return productCd;
    }

    /**
     * Sets the value of the productCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCd(String value) {
        this.productCd = value;
    }

    /**
     * Gets the value of the riskStateCd property.
     * 
     * @return
     *     possible object is
     *     {@link StateProvCd }
     *     
     */
    public StateProvCd getRiskStateCd() {
        return riskStateCd;
    }

    /**
     * Sets the value of the riskStateCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateProvCd }
     *     
     */
    public void setRiskStateCd(StateProvCd value) {
        this.riskStateCd = value;
    }

    /**
     * Gets the value of the sourcePolicyNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcePolicyNum() {
        return sourcePolicyNum;
    }

    /**
     * Sets the value of the sourcePolicyNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcePolicyNum(String value) {
        this.sourcePolicyNum = value;
    }

    /**
     * Gets the value of the subProducerCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubProducerCd() {
        return subProducerCd;
    }

    /**
     * Sets the value of the subProducerCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubProducerCd(String value) {
        this.subProducerCd = value;
    }

    /**
     * Gets the value of the agencyCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgencyCd() {
        return agencyCd;
    }

    /**
     * Sets the value of the agencyCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgencyCd(String value) {
        this.agencyCd = value;
    }

    /**
     * Gets the value of the agentCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentCd() {
        return agentCd;
    }

    /**
     * Sets the value of the agentCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentCd(String value) {
        this.agentCd = value;
    }

    /**
     * Gets the value of the agentGeneralNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentGeneralNumber() {
        return agentGeneralNumber;
    }

    /**
     * Sets the value of the agentGeneralNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentGeneralNumber(String value) {
        this.agentGeneralNumber = value;
    }

    /**
     * Gets the value of the agencyLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgencyLocation() {
        return agencyLocation;
    }

    /**
     * Sets the value of the agencyLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgencyLocation(String value) {
        this.agencyLocation = value;
    }

    /**
     * Gets the value of the typeOfBusinessCd property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOfBusiness }
     *     
     */
    public TypeOfBusiness getTypeOfBusinessCd() {
        return typeOfBusinessCd;
    }

    /**
     * Sets the value of the typeOfBusinessCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOfBusiness }
     *     
     */
    public void setTypeOfBusinessCd(TypeOfBusiness value) {
        this.typeOfBusinessCd = value;
    }

    /**
     * Gets the value of the typeOfPolicyCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPolicyType }
     *     
     */
    public AAAPolicyType getTypeOfPolicyCd() {
        return typeOfPolicyCd;
    }

    /**
     * Sets the value of the typeOfPolicyCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPolicyType }
     *     
     */
    public void setTypeOfPolicyCd(AAAPolicyType value) {
        this.typeOfPolicyCd = value;
    }

    /**
     * Gets the value of the umcdEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link UMCDEndorsementForm }
     *     
     */
    public UMCDEndorsementForm getUMCDEndorsementForm() {
        return umcdEndorsementForm;
    }

    /**
     * Sets the value of the umcdEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMCDEndorsementForm }
     *     
     */
    public void setUMCDEndorsementForm(UMCDEndorsementForm value) {
        this.umcdEndorsementForm = value;
    }

    /**
     * Gets the value of the umrEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link UMREndorsementForm }
     *     
     */
    public UMREndorsementForm getUMREndorsementForm() {
        return umrEndorsementForm;
    }

    /**
     * Sets the value of the umrEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMREndorsementForm }
     *     
     */
    public void setUMREndorsementForm(UMREndorsementForm value) {
        this.umrEndorsementForm = value;
    }

    /**
     * Gets the value of the underwriterCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnderwriterCd() {
        return underwriterCd;
    }

    /**
     * Sets the value of the underwriterCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnderwriterCd(String value) {
        this.underwriterCd = value;
    }

    /**
     * Gets the value of the tollNumberCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTollNumberCd() {
        return tollNumberCd;
    }

    /**
     * Sets the value of the tollNumberCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTollNumberCd(String value) {
        this.tollNumberCd = value;
    }

    /**
     * Gets the value of the salesChannelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesChannelType() {
        return salesChannelType;
    }

    /**
     * Sets the value of the salesChannelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesChannelType(String value) {
        this.salesChannelType = value;
    }

    /**
     * Gets the value of the aa41CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AA41CAEndorsementForm }
     *     
     */
    public AA41CAEndorsementForm getAA41CAEndorsementForm() {
        return aa41CAEndorsementForm;
    }

    /**
     * Sets the value of the aa41CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AA41CAEndorsementForm }
     *     
     */
    public void setAA41CAEndorsementForm(AA41CAEndorsementForm value) {
        this.aa41CAEndorsementForm = value;
    }

    /**
     * Gets the value of the aaaAutoThirdPartyDesigneeComponent property.
     * 
     * @return
     *     possible object is
     *     {@link AAAAutoThirdPartyDesigneeComponent }
     *     
     */
    public AAAAutoThirdPartyDesigneeComponent getAAAAutoThirdPartyDesigneeComponent() {
        return aaaAutoThirdPartyDesigneeComponent;
    }

    /**
     * Sets the value of the aaaAutoThirdPartyDesigneeComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAAutoThirdPartyDesigneeComponent }
     *     
     */
    public void setAAAAutoThirdPartyDesigneeComponent(AAAAutoThirdPartyDesigneeComponent value) {
        this.aaaAutoThirdPartyDesigneeComponent = value;
    }

    /**
     * Gets the value of the aaaRideSharingCoverageEndorsement property.
     * 
     * @return
     *     possible object is
     *     {@link AAARideSharingCoverageEndorsement }
     *     
     */
    public AAARideSharingCoverageEndorsement getAAARideSharingCoverageEndorsement() {
        return aaaRideSharingCoverageEndorsement;
    }

    /**
     * Sets the value of the aaaRideSharingCoverageEndorsement property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAARideSharingCoverageEndorsement }
     *     
     */
    public void setAAARideSharingCoverageEndorsement(AAARideSharingCoverageEndorsement value) {
        this.aaaRideSharingCoverageEndorsement = value;
    }

    /**
     * Gets the value of the choiceAA52CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA52CAEndorsementForm }
     *     
     */
    public ChoiceAA52CAEndorsementForm getChoiceAA52CAEndorsementForm() {
        return choiceAA52CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA52CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA52CAEndorsementForm }
     *     
     */
    public void setChoiceAA52CAEndorsementForm(ChoiceAA52CAEndorsementForm value) {
        this.choiceAA52CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA57CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA57CAEndorsementForm }
     *     
     */
    public ChoiceAA57CAEndorsementForm getChoiceAA57CAEndorsementForm() {
        return choiceAA57CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA57CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA57CAEndorsementForm }
     *     
     */
    public void setChoiceAA57CAEndorsementForm(ChoiceAA57CAEndorsementForm value) {
        this.choiceAA57CAEndorsementForm = value;
    }

    /**
     * Gets the value of the contractTermTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractTermTypeCd() {
        return contractTermTypeCd;
    }

    /**
     * Sets the value of the contractTermTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractTermTypeCd(String value) {
        this.contractTermTypeCd = value;
    }

    /**
     * Gets the value of the policyFormCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyFormCd() {
        return policyFormCd;
    }

    /**
     * Sets the value of the policyFormCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyFormCd(String value) {
        this.policyFormCd = value;
    }

    /**
     * Gets the value of the prevPolicyFormCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrevPolicyFormCd() {
        return prevPolicyFormCd;
    }

    /**
     * Sets the value of the prevPolicyFormCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrevPolicyFormCd(String value) {
        this.prevPolicyFormCd = value;
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
