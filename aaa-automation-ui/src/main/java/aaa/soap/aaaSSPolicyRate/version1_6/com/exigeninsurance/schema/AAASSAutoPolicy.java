
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.AAAImportedFrom;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.PolicyStatusCode;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAASSAutoPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAASSAutoPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="memberSinceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="totalERSUsage" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="bestCreditScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AAADriver" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAADriver" maxOccurs="7"/&gt;
 *         &lt;element name="AAAInsured" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAInsured" maxOccurs="999"/&gt;
 *         &lt;element name="AAAPaymentOption" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPaymentOption"/&gt;
 *         &lt;element name="paymentPlans" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}PaymentPlans" minOccurs="0"/&gt;
 *         &lt;element name="AAAMPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAMPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAOBELCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAOBELCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPIPDedCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPIPDedCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPIPMEDICALCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPIPMEDICALCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPIPWORKLOSSCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPIPWORKLOSSCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAAPIPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAAPIPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAEMBCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAEMBCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAFuneralCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAFuneralCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAADBPolicyCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAADBPolicyCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPDCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPDCoverage"/&gt;
 *         &lt;element name="AAAPolicyPrintingInfoComponent" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPolicyPrintingInfoComponent" minOccurs="0"/&gt;
 *         &lt;element name="AAAUIMBICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUIMBICoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUIMPDCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUIMPDCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUMBICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUMBICoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUMPDCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUMPDCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUMSUMCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUMSUMCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAILCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAILCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPIPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPIPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAGPIPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAGPIPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAABasicPIPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAABasicPIPCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAPIPCoverageInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPIPCoverageInfoType" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicle" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicle" maxOccurs="8"/&gt;
 *         &lt;element name="agencyCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="salesChannelType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="clubCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="agentType" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAAgentType" minOccurs="0"/&gt;
 *         &lt;element name="AHTMHEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AHTMHEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="IN_AA41XX0509EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}IN_AA41XX0509EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="IN_AA52IN0909EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}IN_AA52IN0909EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="authorizedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AZ_AA01AZ0909EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_AA01AZ0909EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="AZ_AA41XX0509EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_AA41XX0509EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="AZ_AA52AZ0909EndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AZ_AA52AZ0909EndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="BICoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}BICoverage"/&gt;
 *         &lt;element name="branchCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}Branch" minOccurs="0"/&gt;
 *         &lt;element name="Commissions" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}Commissions" minOccurs="0"/&gt;
 *         &lt;element name="countryCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="currencyCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="doNotRenewInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="effective" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="contractTermTypeCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ENOCCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}ENOCCEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="expiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="forceAUNoticeGeneration" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="importedFrom" type="{http://www.exigeninsurance.com/data/EIS/1.0}AAAImportedFrom"/&gt;
 *         &lt;element name="officeType" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAOfficeType" minOccurs="0"/&gt;
 *         &lt;element name="agentCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OtherOrPriorPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}OtherOrPriorPolicy" maxOccurs="6"/&gt;
 *         &lt;element name="AAAOtherOrPriorPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAOtherOrPriorPolicy"/&gt;
 *         &lt;element name="paymentPlanCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="contractDisplay" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}Term" minOccurs="0"/&gt;
 *         &lt;element name="imported" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAASource" minOccurs="0"/&gt;
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numberOfAdditionalPersons" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="policyStatusCd" type="{http://www.exigeninsurance.com/data/EIS/1.0}PolicyStatusCode" minOccurs="0"/&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}PremiumEntry" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="producerCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="productCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="riskStateCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd"/&gt;
 *         &lt;element name="sourcePolicyNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="customerNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="subProducerCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="supplementalSpousalLiability" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="typeOfBusinessCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}TypeOfBusiness" minOccurs="0"/&gt;
 *         &lt;element name="typeOfPolicyCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAPolicyType" minOccurs="0"/&gt;
 *         &lt;element name="UMCDEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}UMCDEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="UMREndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}UMREndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="underwriterCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="leadSourceCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAALeadSourceCd" minOccurs="0"/&gt;
 *         &lt;element name="unacceptableRiskSurchargeInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="unacceptableRiskReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AAAMembershipOrder" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAMembershipOrder" minOccurs="0"/&gt;
 *         &lt;element name="openLinkURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="orderCreditScore" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="elc" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAELCLookup" minOccurs="0"/&gt;
 *         &lt;element name="tollNumberCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="adverselyImpacted" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAAILookup" minOccurs="0"/&gt;
 *         &lt;element name="overrideAsdLevel" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="asdDefaultLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="asdOverrideLevel" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAASDLookup" minOccurs="0"/&gt;
 *         &lt;element name="asdOverridenBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AAAPrefill" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPrefill" minOccurs="0"/&gt;
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
@XmlType(name = "AAASSAutoPolicy", propOrder = {
    "memberSinceDate",
    "totalERSUsage",
    "bestCreditScore",
    "aaaDriver",
    "aaaInsured",
    "aaaPaymentOption",
    "paymentPlans",
    "aaampCoverage",
    "aaaobelCoverage",
    "aaapipDedCoverage",
    "aaapipmedicalCoverage",
    "aaapipworklossCoverage",
    "aaaapipCoverage",
    "aaaembCoverage",
    "aaaFuneralCoverage",
    "aaaadbPolicyCoverage",
    "aaapdCoverage",
    "aaaPolicyPrintingInfoComponent",
    "aaauimbiCoverage",
    "aaauimpdCoverage",
    "aaaumbiCoverage",
    "aaaumpdCoverage",
    "aaaumsumCoverage",
    "aaailCoverage",
    "aaapipCoverage",
    "aaagpipCoverage",
    "aaaBasicPIPCoverage",
    "aaapipCoverageInfo",
    "aaaVehicle",
    "agencyCd",
    "salesChannelType",
    "clubCd",
    "agentType",
    "ahtmhEndorsementForm",
    "inaa41XX0509EndorsementForm",
    "inaa52IN0909EndorsementForm",
    "authorizedBy",
    "azaa01AZ0909EndorsementForm",
    "azaa41XX0509EndorsementForm",
    "azaa52AZ0909EndorsementForm",
    "biCoverage",
    "branchCd",
    "commissions",
    "countryCd",
    "currencyCd",
    "doNotRenewInd",
    "effective",
    "contractTermTypeCd",
    "enoccEndorsementForm",
    "expiration",
    "forceAUNoticeGeneration",
    "importedFrom",
    "officeType",
    "agentCd",
    "otherOrPriorPolicy",
    "aaaOtherOrPriorPolicy",
    "paymentPlanCd",
    "contractDisplay",
    "imported",
    "policyNumber",
    "numberOfAdditionalPersons",
    "policyStatusCd",
    "premiumEntry",
    "producerCd",
    "productCd",
    "riskStateCd",
    "sourcePolicyNum",
    "customerNumber",
    "subProducerCd",
    "supplementalSpousalLiability",
    "typeOfBusinessCd",
    "typeOfPolicyCd",
    "umcdEndorsementForm",
    "umrEndorsementForm",
    "underwriterCd",
    "leadSourceCd",
    "unacceptableRiskSurchargeInd",
    "unacceptableRiskReason",
    "aaaMembershipOrder",
    "openLinkURL",
    "orderCreditScore",
    "elc",
    "tollNumberCd",
    "adverselyImpacted",
    "overrideAsdLevel",
    "asdDefaultLevel",
    "asdOverrideLevel",
    "asdOverridenBy",
    "aaaPrefill"
})
public class AAASSAutoPolicy {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar memberSinceDate;
    protected BigInteger totalERSUsage;
    protected String bestCreditScore;
    @XmlElement(name = "AAADriver", required = true)
    protected List<AAADriver> aaaDriver;
    @XmlElement(name = "AAAInsured", required = true)
    protected List<AAAInsured> aaaInsured;
    @XmlElement(name = "AAAPaymentOption", required = true)
    protected AAAPaymentOption aaaPaymentOption;
    protected PaymentPlans paymentPlans;
    @XmlElement(name = "AAAMPCoverage")
    protected AAAMPCoverage aaampCoverage;
    @XmlElement(name = "AAAOBELCoverage")
    protected AAAOBELCoverage aaaobelCoverage;
    @XmlElement(name = "AAAPIPDedCoverage")
    protected AAAPIPDedCoverage aaapipDedCoverage;
    @XmlElement(name = "AAAPIPMEDICALCoverage")
    protected AAAPIPMEDICALCoverage aaapipmedicalCoverage;
    @XmlElement(name = "AAAPIPWORKLOSSCoverage")
    protected AAAPIPWORKLOSSCoverage aaapipworklossCoverage;
    @XmlElement(name = "AAAAPIPCoverage")
    protected AAAAPIPCoverage aaaapipCoverage;
    @XmlElement(name = "AAAEMBCoverage")
    protected AAAEMBCoverage aaaembCoverage;
    @XmlElement(name = "AAAFuneralCoverage")
    protected AAAFuneralCoverage aaaFuneralCoverage;
    @XmlElement(name = "AAAADBPolicyCoverage")
    protected AAAADBPolicyCoverage aaaadbPolicyCoverage;
    @XmlElement(name = "AAAPDCoverage", required = true)
    protected AAAPDCoverage aaapdCoverage;
    @XmlElement(name = "AAAPolicyPrintingInfoComponent")
    protected AAAPolicyPrintingInfoComponent aaaPolicyPrintingInfoComponent;
    @XmlElement(name = "AAAUIMBICoverage")
    protected AAAUIMBICoverage aaauimbiCoverage;
    @XmlElement(name = "AAAUIMPDCoverage")
    protected AAAUIMPDCoverage aaauimpdCoverage;
    @XmlElement(name = "AAAUMBICoverage")
    protected AAAUMBICoverage aaaumbiCoverage;
    @XmlElement(name = "AAAUMPDCoverage")
    protected AAAUMPDCoverage aaaumpdCoverage;
    @XmlElement(name = "AAAUMSUMCoverage")
    protected AAAUMSUMCoverage aaaumsumCoverage;
    @XmlElement(name = "AAAILCoverage")
    protected AAAILCoverage aaailCoverage;
    @XmlElement(name = "AAAPIPCoverage")
    protected AAAPIPCoverage aaapipCoverage;
    @XmlElement(name = "AAAGPIPCoverage")
    protected AAAGPIPCoverage aaagpipCoverage;
    @XmlElement(name = "AAABasicPIPCoverage")
    protected AAABasicPIPCoverage aaaBasicPIPCoverage;
    @XmlElement(name = "AAAPIPCoverageInfo")
    protected AAAPIPCoverageInfoType aaapipCoverageInfo;
    @XmlElement(name = "AAAVehicle", required = true)
    protected List<AAAVehicle> aaaVehicle;
    protected String agencyCd;
    protected String salesChannelType;
    protected String clubCd;
    @XmlSchemaType(name = "string")
    protected AAAAgentType agentType;
    @XmlElement(name = "AHTMHEndorsementForm")
    protected AHTMHEndorsementForm ahtmhEndorsementForm;
    @XmlElement(name = "IN_AA41XX0509EndorsementForm")
    protected INAA41XX0509EndorsementForm inaa41XX0509EndorsementForm;
    @XmlElement(name = "IN_AA52IN0909EndorsementForm")
    protected INAA52IN0909EndorsementForm inaa52IN0909EndorsementForm;
    protected String authorizedBy;
    @XmlElement(name = "AZ_AA01AZ0909EndorsementForm")
    protected AZAA01AZ0909EndorsementForm azaa01AZ0909EndorsementForm;
    @XmlElement(name = "AZ_AA41XX0509EndorsementForm")
    protected AZAA41XX0509EndorsementForm azaa41XX0509EndorsementForm;
    @XmlElement(name = "AZ_AA52AZ0909EndorsementForm")
    protected AZAA52AZ0909EndorsementForm azaa52AZ0909EndorsementForm;
    @XmlElement(name = "BICoverage", required = true)
    protected BICoverage biCoverage;
    @XmlSchemaType(name = "string")
    protected Branch branchCd;
    @XmlElement(name = "Commissions")
    protected Commissions commissions;
    @XmlElement(defaultValue = "US")
    protected String countryCd;
    @XmlElement(defaultValue = "USD")
    protected String currencyCd;
    protected Boolean doNotRenewInd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effective;
    @XmlElement(required = true, defaultValue = "AN")
    protected String contractTermTypeCd;
    @XmlElement(name = "ENOCCEndorsementForm")
    protected ENOCCEndorsementForm enoccEndorsementForm;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expiration;
    @XmlElement(defaultValue = "false")
    protected Boolean forceAUNoticeGeneration;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAAImportedFrom importedFrom;
    @XmlSchemaType(name = "string")
    protected AAAOfficeType officeType;
    protected String agentCd;
    @XmlElement(name = "OtherOrPriorPolicy", required = true)
    protected List<OtherOrPriorPolicy> otherOrPriorPolicy;
    @XmlElement(name = "AAAOtherOrPriorPolicy", required = true)
    protected AAAOtherOrPriorPolicy aaaOtherOrPriorPolicy;
    protected String paymentPlanCd;
    @XmlElement(defaultValue = "12")
    protected String contractDisplay;
    @XmlElement(defaultValue = "NEW")
    @XmlSchemaType(name = "string")
    protected AAASource imported;
    protected String policyNumber;
    protected BigDecimal numberOfAdditionalPersons;
    @XmlSchemaType(name = "string")
    protected PolicyStatusCode policyStatusCd;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    protected String producerCd;
    @XmlElement(required = true, defaultValue = "AAA_SS")
    protected String productCd;
    @XmlElement(required = true, defaultValue = "AZ")
    @XmlSchemaType(name = "string")
    protected StateProvCd riskStateCd;
    protected String sourcePolicyNum;
    protected String customerNumber;
    protected String subProducerCd;
    protected Boolean supplementalSpousalLiability;
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
    @XmlElement(defaultValue = "WUIC")
    protected String underwriterCd;
    protected String leadSourceCd;
    protected Boolean unacceptableRiskSurchargeInd;
    protected String unacceptableRiskReason;
    @XmlElement(name = "AAAMembershipOrder")
    protected AAAMembershipOrder aaaMembershipOrder;
    protected String openLinkURL;
    @XmlElement(defaultValue = "true")
    protected Boolean orderCreditScore;
    @XmlSchemaType(name = "string")
    protected AAAELCLookup elc;
    protected String tollNumberCd;
    @XmlSchemaType(name = "string")
    protected AAAAILookup adverselyImpacted;
    protected Boolean overrideAsdLevel;
    protected String asdDefaultLevel;
    @XmlSchemaType(name = "string")
    protected AAAASDLookup asdOverrideLevel;
    protected String asdOverridenBy;
    @XmlElement(name = "AAAPrefill")
    protected AAAPrefill aaaPrefill;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the memberSinceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMemberSinceDate() {
        return memberSinceDate;
    }

    /**
     * Sets the value of the memberSinceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMemberSinceDate(XMLGregorianCalendar value) {
        this.memberSinceDate = value;
    }

    /**
     * Gets the value of the totalERSUsage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalERSUsage() {
        return totalERSUsage;
    }

    /**
     * Sets the value of the totalERSUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalERSUsage(BigInteger value) {
        this.totalERSUsage = value;
    }

    /**
     * Gets the value of the bestCreditScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBestCreditScore() {
        return bestCreditScore;
    }

    /**
     * Sets the value of the bestCreditScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBestCreditScore(String value) {
        this.bestCreditScore = value;
    }

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
     * Gets the value of the aaaPaymentOption property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPaymentOption }
     *     
     */
    public AAAPaymentOption getAAAPaymentOption() {
        return aaaPaymentOption;
    }

    /**
     * Sets the value of the aaaPaymentOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPaymentOption }
     *     
     */
    public void setAAAPaymentOption(AAAPaymentOption value) {
        this.aaaPaymentOption = value;
    }

    /**
     * Gets the value of the paymentPlans property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentPlans }
     *     
     */
    public PaymentPlans getPaymentPlans() {
        return paymentPlans;
    }

    /**
     * Sets the value of the paymentPlans property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentPlans }
     *     
     */
    public void setPaymentPlans(PaymentPlans value) {
        this.paymentPlans = value;
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
     * Gets the value of the aaaobelCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOBELCoverage }
     *     
     */
    public AAAOBELCoverage getAAAOBELCoverage() {
        return aaaobelCoverage;
    }

    /**
     * Sets the value of the aaaobelCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOBELCoverage }
     *     
     */
    public void setAAAOBELCoverage(AAAOBELCoverage value) {
        this.aaaobelCoverage = value;
    }

    /**
     * Gets the value of the aaapipDedCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPIPDedCoverage }
     *     
     */
    public AAAPIPDedCoverage getAAAPIPDedCoverage() {
        return aaapipDedCoverage;
    }

    /**
     * Sets the value of the aaapipDedCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPIPDedCoverage }
     *     
     */
    public void setAAAPIPDedCoverage(AAAPIPDedCoverage value) {
        this.aaapipDedCoverage = value;
    }

    /**
     * Gets the value of the aaapipmedicalCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPIPMEDICALCoverage }
     *     
     */
    public AAAPIPMEDICALCoverage getAAAPIPMEDICALCoverage() {
        return aaapipmedicalCoverage;
    }

    /**
     * Sets the value of the aaapipmedicalCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPIPMEDICALCoverage }
     *     
     */
    public void setAAAPIPMEDICALCoverage(AAAPIPMEDICALCoverage value) {
        this.aaapipmedicalCoverage = value;
    }

    /**
     * Gets the value of the aaapipworklossCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPIPWORKLOSSCoverage }
     *     
     */
    public AAAPIPWORKLOSSCoverage getAAAPIPWORKLOSSCoverage() {
        return aaapipworklossCoverage;
    }

    /**
     * Sets the value of the aaapipworklossCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPIPWORKLOSSCoverage }
     *     
     */
    public void setAAAPIPWORKLOSSCoverage(AAAPIPWORKLOSSCoverage value) {
        this.aaapipworklossCoverage = value;
    }

    /**
     * Gets the value of the aaaapipCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAAPIPCoverage }
     *     
     */
    public AAAAPIPCoverage getAAAAPIPCoverage() {
        return aaaapipCoverage;
    }

    /**
     * Sets the value of the aaaapipCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAAPIPCoverage }
     *     
     */
    public void setAAAAPIPCoverage(AAAAPIPCoverage value) {
        this.aaaapipCoverage = value;
    }

    /**
     * Gets the value of the aaaembCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAEMBCoverage }
     *     
     */
    public AAAEMBCoverage getAAAEMBCoverage() {
        return aaaembCoverage;
    }

    /**
     * Sets the value of the aaaembCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAEMBCoverage }
     *     
     */
    public void setAAAEMBCoverage(AAAEMBCoverage value) {
        this.aaaembCoverage = value;
    }

    /**
     * Gets the value of the aaaFuneralCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAFuneralCoverage }
     *     
     */
    public AAAFuneralCoverage getAAAFuneralCoverage() {
        return aaaFuneralCoverage;
    }

    /**
     * Sets the value of the aaaFuneralCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAFuneralCoverage }
     *     
     */
    public void setAAAFuneralCoverage(AAAFuneralCoverage value) {
        this.aaaFuneralCoverage = value;
    }

    /**
     * Gets the value of the aaaadbPolicyCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAADBPolicyCoverage }
     *     
     */
    public AAAADBPolicyCoverage getAAAADBPolicyCoverage() {
        return aaaadbPolicyCoverage;
    }

    /**
     * Sets the value of the aaaadbPolicyCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAADBPolicyCoverage }
     *     
     */
    public void setAAAADBPolicyCoverage(AAAADBPolicyCoverage value) {
        this.aaaadbPolicyCoverage = value;
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
     * Gets the value of the aaaPolicyPrintingInfoComponent property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPolicyPrintingInfoComponent }
     *     
     */
    public AAAPolicyPrintingInfoComponent getAAAPolicyPrintingInfoComponent() {
        return aaaPolicyPrintingInfoComponent;
    }

    /**
     * Sets the value of the aaaPolicyPrintingInfoComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPolicyPrintingInfoComponent }
     *     
     */
    public void setAAAPolicyPrintingInfoComponent(AAAPolicyPrintingInfoComponent value) {
        this.aaaPolicyPrintingInfoComponent = value;
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
     * Gets the value of the aaauimpdCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUIMPDCoverage }
     *     
     */
    public AAAUIMPDCoverage getAAAUIMPDCoverage() {
        return aaauimpdCoverage;
    }

    /**
     * Sets the value of the aaauimpdCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUIMPDCoverage }
     *     
     */
    public void setAAAUIMPDCoverage(AAAUIMPDCoverage value) {
        this.aaauimpdCoverage = value;
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
     * Gets the value of the aaaumpdCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUMPDCoverage }
     *     
     */
    public AAAUMPDCoverage getAAAUMPDCoverage() {
        return aaaumpdCoverage;
    }

    /**
     * Sets the value of the aaaumpdCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUMPDCoverage }
     *     
     */
    public void setAAAUMPDCoverage(AAAUMPDCoverage value) {
        this.aaaumpdCoverage = value;
    }

    /**
     * Gets the value of the aaaumsumCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUMSUMCoverage }
     *     
     */
    public AAAUMSUMCoverage getAAAUMSUMCoverage() {
        return aaaumsumCoverage;
    }

    /**
     * Sets the value of the aaaumsumCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUMSUMCoverage }
     *     
     */
    public void setAAAUMSUMCoverage(AAAUMSUMCoverage value) {
        this.aaaumsumCoverage = value;
    }

    /**
     * Gets the value of the aaailCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAILCoverage }
     *     
     */
    public AAAILCoverage getAAAILCoverage() {
        return aaailCoverage;
    }

    /**
     * Sets the value of the aaailCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAILCoverage }
     *     
     */
    public void setAAAILCoverage(AAAILCoverage value) {
        this.aaailCoverage = value;
    }

    /**
     * Gets the value of the aaapipCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPIPCoverage }
     *     
     */
    public AAAPIPCoverage getAAAPIPCoverage() {
        return aaapipCoverage;
    }

    /**
     * Sets the value of the aaapipCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPIPCoverage }
     *     
     */
    public void setAAAPIPCoverage(AAAPIPCoverage value) {
        this.aaapipCoverage = value;
    }

    /**
     * Gets the value of the aaagpipCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAGPIPCoverage }
     *     
     */
    public AAAGPIPCoverage getAAAGPIPCoverage() {
        return aaagpipCoverage;
    }

    /**
     * Sets the value of the aaagpipCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAGPIPCoverage }
     *     
     */
    public void setAAAGPIPCoverage(AAAGPIPCoverage value) {
        this.aaagpipCoverage = value;
    }

    /**
     * Gets the value of the aaaBasicPIPCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAABasicPIPCoverage }
     *     
     */
    public AAABasicPIPCoverage getAAABasicPIPCoverage() {
        return aaaBasicPIPCoverage;
    }

    /**
     * Sets the value of the aaaBasicPIPCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAABasicPIPCoverage }
     *     
     */
    public void setAAABasicPIPCoverage(AAABasicPIPCoverage value) {
        this.aaaBasicPIPCoverage = value;
    }

    /**
     * Gets the value of the aaapipCoverageInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPIPCoverageInfoType }
     *     
     */
    public AAAPIPCoverageInfoType getAAAPIPCoverageInfo() {
        return aaapipCoverageInfo;
    }

    /**
     * Sets the value of the aaapipCoverageInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPIPCoverageInfoType }
     *     
     */
    public void setAAAPIPCoverageInfo(AAAPIPCoverageInfoType value) {
        this.aaapipCoverageInfo = value;
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
     * Gets the value of the clubCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClubCd() {
        return clubCd;
    }

    /**
     * Sets the value of the clubCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClubCd(String value) {
        this.clubCd = value;
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
     * Gets the value of the inaa41XX0509EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link INAA41XX0509EndorsementForm }
     *     
     */
    public INAA41XX0509EndorsementForm getINAA41XX0509EndorsementForm() {
        return inaa41XX0509EndorsementForm;
    }

    /**
     * Sets the value of the inaa41XX0509EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link INAA41XX0509EndorsementForm }
     *     
     */
    public void setINAA41XX0509EndorsementForm(INAA41XX0509EndorsementForm value) {
        this.inaa41XX0509EndorsementForm = value;
    }

    /**
     * Gets the value of the inaa52IN0909EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link INAA52IN0909EndorsementForm }
     *     
     */
    public INAA52IN0909EndorsementForm getINAA52IN0909EndorsementForm() {
        return inaa52IN0909EndorsementForm;
    }

    /**
     * Sets the value of the inaa52IN0909EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link INAA52IN0909EndorsementForm }
     *     
     */
    public void setINAA52IN0909EndorsementForm(INAA52IN0909EndorsementForm value) {
        this.inaa52IN0909EndorsementForm = value;
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
     * Gets the value of the azaa01AZ0909EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZAA01AZ0909EndorsementForm }
     *     
     */
    public AZAA01AZ0909EndorsementForm getAZAA01AZ0909EndorsementForm() {
        return azaa01AZ0909EndorsementForm;
    }

    /**
     * Sets the value of the azaa01AZ0909EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZAA01AZ0909EndorsementForm }
     *     
     */
    public void setAZAA01AZ0909EndorsementForm(AZAA01AZ0909EndorsementForm value) {
        this.azaa01AZ0909EndorsementForm = value;
    }

    /**
     * Gets the value of the azaa41XX0509EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZAA41XX0509EndorsementForm }
     *     
     */
    public AZAA41XX0509EndorsementForm getAZAA41XX0509EndorsementForm() {
        return azaa41XX0509EndorsementForm;
    }

    /**
     * Sets the value of the azaa41XX0509EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZAA41XX0509EndorsementForm }
     *     
     */
    public void setAZAA41XX0509EndorsementForm(AZAA41XX0509EndorsementForm value) {
        this.azaa41XX0509EndorsementForm = value;
    }

    /**
     * Gets the value of the azaa52AZ0909EndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link AZAA52AZ0909EndorsementForm }
     *     
     */
    public AZAA52AZ0909EndorsementForm getAZAA52AZ0909EndorsementForm() {
        return azaa52AZ0909EndorsementForm;
    }

    /**
     * Sets the value of the azaa52AZ0909EndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AZAA52AZ0909EndorsementForm }
     *     
     */
    public void setAZAA52AZ0909EndorsementForm(AZAA52AZ0909EndorsementForm value) {
        this.azaa52AZ0909EndorsementForm = value;
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
     * Gets the value of the commissions property.
     * 
     * @return
     *     possible object is
     *     {@link Commissions }
     *     
     */
    public Commissions getCommissions() {
        return commissions;
    }

    /**
     * Sets the value of the commissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Commissions }
     *     
     */
    public void setCommissions(Commissions value) {
        this.commissions = value;
    }

    /**
     * Gets the value of the countryCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCd() {
        return countryCd;
    }

    /**
     * Sets the value of the countryCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCd(String value) {
        this.countryCd = value;
    }

    /**
     * Gets the value of the currencyCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCd() {
        return currencyCd;
    }

    /**
     * Sets the value of the currencyCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCd(String value) {
        this.currencyCd = value;
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
     *     {@link AAAImportedFrom }
     *     
     */
    public AAAImportedFrom getImportedFrom() {
        return importedFrom;
    }

    /**
     * Sets the value of the importedFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAImportedFrom }
     *     
     */
    public void setImportedFrom(AAAImportedFrom value) {
        this.importedFrom = value;
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
     * Gets the value of the aaaOtherOrPriorPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOtherOrPriorPolicy }
     *     
     */
    public AAAOtherOrPriorPolicy getAAAOtherOrPriorPolicy() {
        return aaaOtherOrPriorPolicy;
    }

    /**
     * Sets the value of the aaaOtherOrPriorPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOtherOrPriorPolicy }
     *     
     */
    public void setAAAOtherOrPriorPolicy(AAAOtherOrPriorPolicy value) {
        this.aaaOtherOrPriorPolicy = value;
    }

    /**
     * Gets the value of the paymentPlanCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentPlanCd() {
        return paymentPlanCd;
    }

    /**
     * Sets the value of the paymentPlanCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentPlanCd(String value) {
        this.paymentPlanCd = value;
    }

    /**
     * Gets the value of the contractDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractDisplay() {
        return contractDisplay;
    }

    /**
     * Sets the value of the contractDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractDisplay(String value) {
        this.contractDisplay = value;
    }

    /**
     * Gets the value of the imported property.
     * 
     * @return
     *     possible object is
     *     {@link AAASource }
     *     
     */
    public AAASource getImported() {
        return imported;
    }

    /**
     * Sets the value of the imported property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAASource }
     *     
     */
    public void setImported(AAASource value) {
        this.imported = value;
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
     * Gets the value of the numberOfAdditionalPersons property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfAdditionalPersons() {
        return numberOfAdditionalPersons;
    }

    /**
     * Sets the value of the numberOfAdditionalPersons property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfAdditionalPersons(BigDecimal value) {
        this.numberOfAdditionalPersons = value;
    }

    /**
     * Gets the value of the policyStatusCd property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyStatusCode }
     *     
     */
    public PolicyStatusCode getPolicyStatusCd() {
        return policyStatusCd;
    }

    /**
     * Sets the value of the policyStatusCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyStatusCode }
     *     
     */
    public void setPolicyStatusCd(PolicyStatusCode value) {
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
     * Gets the value of the customerNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the value of the customerNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerNumber(String value) {
        this.customerNumber = value;
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
     * Gets the value of the supplementalSpousalLiability property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupplementalSpousalLiability() {
        return supplementalSpousalLiability;
    }

    /**
     * Sets the value of the supplementalSpousalLiability property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupplementalSpousalLiability(Boolean value) {
        this.supplementalSpousalLiability = value;
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
     * Gets the value of the leadSourceCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeadSourceCd() {
        return leadSourceCd;
    }

    /**
     * Sets the value of the leadSourceCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeadSourceCd(String value) {
        this.leadSourceCd = value;
    }

    /**
     * Gets the value of the unacceptableRiskSurchargeInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnacceptableRiskSurchargeInd() {
        return unacceptableRiskSurchargeInd;
    }

    /**
     * Sets the value of the unacceptableRiskSurchargeInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnacceptableRiskSurchargeInd(Boolean value) {
        this.unacceptableRiskSurchargeInd = value;
    }

    /**
     * Gets the value of the unacceptableRiskReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnacceptableRiskReason() {
        return unacceptableRiskReason;
    }

    /**
     * Sets the value of the unacceptableRiskReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnacceptableRiskReason(String value) {
        this.unacceptableRiskReason = value;
    }

    /**
     * Gets the value of the aaaMembershipOrder property.
     * 
     * @return
     *     possible object is
     *     {@link AAAMembershipOrder }
     *     
     */
    public AAAMembershipOrder getAAAMembershipOrder() {
        return aaaMembershipOrder;
    }

    /**
     * Sets the value of the aaaMembershipOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAMembershipOrder }
     *     
     */
    public void setAAAMembershipOrder(AAAMembershipOrder value) {
        this.aaaMembershipOrder = value;
    }

    /**
     * Gets the value of the openLinkURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenLinkURL() {
        return openLinkURL;
    }

    /**
     * Sets the value of the openLinkURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenLinkURL(String value) {
        this.openLinkURL = value;
    }

    /**
     * Gets the value of the orderCreditScore property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOrderCreditScore() {
        return orderCreditScore;
    }

    /**
     * Sets the value of the orderCreditScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOrderCreditScore(Boolean value) {
        this.orderCreditScore = value;
    }

    /**
     * Gets the value of the elc property.
     * 
     * @return
     *     possible object is
     *     {@link AAAELCLookup }
     *     
     */
    public AAAELCLookup getElc() {
        return elc;
    }

    /**
     * Sets the value of the elc property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAELCLookup }
     *     
     */
    public void setElc(AAAELCLookup value) {
        this.elc = value;
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
     * Gets the value of the adverselyImpacted property.
     * 
     * @return
     *     possible object is
     *     {@link AAAAILookup }
     *     
     */
    public AAAAILookup getAdverselyImpacted() {
        return adverselyImpacted;
    }

    /**
     * Sets the value of the adverselyImpacted property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAAILookup }
     *     
     */
    public void setAdverselyImpacted(AAAAILookup value) {
        this.adverselyImpacted = value;
    }

    /**
     * Gets the value of the overrideAsdLevel property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOverrideAsdLevel() {
        return overrideAsdLevel;
    }

    /**
     * Sets the value of the overrideAsdLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOverrideAsdLevel(Boolean value) {
        this.overrideAsdLevel = value;
    }

    /**
     * Gets the value of the asdDefaultLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsdDefaultLevel() {
        return asdDefaultLevel;
    }

    /**
     * Sets the value of the asdDefaultLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsdDefaultLevel(String value) {
        this.asdDefaultLevel = value;
    }

    /**
     * Gets the value of the asdOverrideLevel property.
     * 
     * @return
     *     possible object is
     *     {@link AAAASDLookup }
     *     
     */
    public AAAASDLookup getAsdOverrideLevel() {
        return asdOverrideLevel;
    }

    /**
     * Sets the value of the asdOverrideLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAASDLookup }
     *     
     */
    public void setAsdOverrideLevel(AAAASDLookup value) {
        this.asdOverrideLevel = value;
    }

    /**
     * Gets the value of the asdOverridenBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsdOverridenBy() {
        return asdOverridenBy;
    }

    /**
     * Sets the value of the asdOverridenBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsdOverridenBy(String value) {
        this.asdOverridenBy = value;
    }

    /**
     * Gets the value of the aaaPrefill property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPrefill }
     *     
     */
    public AAAPrefill getAAAPrefill() {
        return aaaPrefill;
    }

    /**
     * Sets the value of the aaaPrefill property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPrefill }
     *     
     */
    public void setAAAPrefill(AAAPrefill value) {
        this.aaaPrefill = value;
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
