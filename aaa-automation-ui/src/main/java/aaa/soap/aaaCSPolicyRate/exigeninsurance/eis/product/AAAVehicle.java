
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_db.AntiTheftCode;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_db.AntiTheftCode2;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup.AAAOwnershipType;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup.AAAVehicleType;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.ComponentState;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAAVehicle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAVehicle">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AAAAllRiskCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAAllRiskCoverage"/>
 *         &lt;element name="AAACOLLCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAACOLLCoverage"/>
 *         &lt;element name="AAACOMPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAACOMPCoverage"/>
 *         &lt;element name="AAAETECoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAETECoverage" minOccurs="0"/>
 *         &lt;element name="AAAGaragingAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAGaragingAddress"/>
 *         &lt;element name="AAAVehicleCoverageBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoverageBI"/>
 *         &lt;element name="AAAVehicleCoverageMP" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoverageMP"/>
 *         &lt;element name="AAAVehicleCoveragePD" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoveragePD"/>
 *         &lt;element name="AAAVehicleCoverageUIMBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoverageUIMBI"/>
 *         &lt;element name="AAAVehicleCoverageUMBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoverageUMBI"/>
 *         &lt;element name="AAAGlassCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAGlassCoverage" minOccurs="0"/>
 *         &lt;element name="AAALoanLeaseCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAALoanLeaseCoverage" minOccurs="0"/>
 *         &lt;element name="AAANewCarCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAANewCarCoverage" minOccurs="0"/>
 *         &lt;element name="AAAOEMCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAOEMCoverage" minOccurs="0"/>
 *         &lt;element name="AAAVehicleCoverageUMPD" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleCoverageUMPD" minOccurs="0"/>
 *         &lt;element name="AAARideShareCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAARideShareCoverage" minOccurs="0"/>
 *         &lt;element name="AAACollisionDeductibleWaiverCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAACollisionDeductibleWaiverCoverage" minOccurs="0"/>
 *         &lt;element name="AAASpecialEquipmentCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAASpecialEquipmentCoverage" minOccurs="0"/>
 *         &lt;element name="AAARentalReimbursementCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAARentalReimbursementCoverage" minOccurs="0"/>
 *         &lt;element name="AAATowingAndLaborCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAATowingAndLaborCoverage" minOccurs="0"/>
 *         &lt;element name="AAAVehicleOwnership" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleOwnership" minOccurs="0"/>
 *         &lt;element name="AAAVehicleRatingInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAVehicleRatingInfo"/>
 *         &lt;element name="airBagStatusCd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="antitheft" type="{http://www.exigeninsurance.com/data/AAA_CSA_DB/1.0}AntiTheftCode"/>
 *         &lt;element name="antitheft2" type="{http://www.exigeninsurance.com/data/AAA_CSA_DB/1.0}AntiTheftCode2" minOccurs="0"/>
 *         &lt;element name="antiLockBreaks2" type="{http://www.exigeninsurance.com/data/AAA_CSA_DB/1.0}AAAAntiLockBrakes" minOccurs="0"/>
 *         &lt;element name="assignedDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GEEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}GEEndorsementForm" minOccurs="0"/>
 *         &lt;element name="hybrid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="kitCarInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="LCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}LCEndorsementForm" minOccurs="0"/>
 *         &lt;element name="LCEndorsementOptForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}LCEndorsementOptForm" minOccurs="0"/>
 *         &lt;element name="LSOPCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}LSOPCEndorsementForm" minOccurs="0"/>
 *         &lt;element name="LSOPCEndorsementOptForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}LSOPCEndorsementOptForm" minOccurs="0"/>
 *         &lt;element name="manufacturer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modelYear" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="otherBodyStyleCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherManufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherSeries" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownershipTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAOwnershipType"/>
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PremiumEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="principalDriverOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ratedDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="series" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="showAntiqueQuestionnaire" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="showBusinessQuest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="showFarmQuest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="showKitCarQuestionnaire" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="systemRatedDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehBodyTypeCd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vehIdentificationNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAVehicleType"/>
 *         &lt;element name="vinMatchedInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="vehicleDiscount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="AAAAdditionalInterestChoice" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAAAdditionalInterestChoice" maxOccurs="3" minOccurs="0"/>
 *         &lt;element name="Choice_AA09CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA09CAEndorsementForm" minOccurs="0"/>
 *         &lt;element name="Choice_AA47CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA47CAEndorsementForm" minOccurs="0"/>
 *         &lt;element name="Choice_AA49CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA49CAEndorsementForm" minOccurs="0"/>
 *         &lt;element name="Choice_AA53CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA53CAEndorsementForm" minOccurs="0"/>
 *         &lt;element name="Choice_AA54CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA54CAEndorsementForm" minOccurs="0"/>
 *         &lt;element name="Choice_AA59CAEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}Choice_AA59CAEndorsementForm" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAVehicle", propOrder = {
    "aaaAllRiskCoverage",
    "aaacollCoverage",
    "aaacompCoverage",
    "aaaeteCoverage",
    "aaaGaragingAddress",
    "aaaVehicleCoverageBI",
    "aaaVehicleCoverageMP",
    "aaaVehicleCoveragePD",
    "aaaVehicleCoverageUIMBI",
    "aaaVehicleCoverageUMBI",
    "aaaGlassCoverage",
    "aaaLoanLeaseCoverage",
    "aaaNewCarCoverage",
    "aaaoemCoverage",
    "aaaVehicleCoverageUMPD",
    "aaaRideShareCoverage",
    "aaaCollisionDeductibleWaiverCoverage",
    "aaaSpecialEquipmentCoverage",
    "aaaRentalReimbursementCoverage",
    "aaaTowingAndLaborCoverage",
    "aaaVehicleOwnership",
    "aaaVehicleRatingInfo",
    "airBagStatusCd",
    "antitheft",
    "antitheft2",
    "antiLockBreaks2",
    "assignedDriverOID",
    "geEndorsementForm",
    "hybrid",
    "kitCarInd",
    "lcEndorsementForm",
    "lcEndorsementOptForm",
    "lsopcEndorsementForm",
    "lsopcEndorsementOptForm",
    "manufacturer",
    "model",
    "modelYear",
    "otherBodyStyleCd",
    "otherManufacturer",
    "otherModel",
    "otherSeries",
    "ownershipTypeCd",
    "premiumEntry",
    "principalDriverOID",
    "ratedDriverOID",
    "seqNo",
    "series",
    "showAntiqueQuestionnaire",
    "showBusinessQuest",
    "showFarmQuest",
    "showKitCarQuestionnaire",
    "systemRatedDriverOID",
    "vehBodyTypeCd",
    "vehIdentificationNo",
    "vehTypeCd",
    "vinMatchedInd",
    "vehicleDiscount",
    "aaaAdditionalInterestChoice",
    "choiceAA09CAEndorsementForm",
    "choiceAA47CAEndorsementForm",
    "choiceAA49CAEndorsementForm",
    "choiceAA53CAEndorsementForm",
    "choiceAA54CAEndorsementForm",
    "choiceAA59CAEndorsementForm"
})
public class AAAVehicle {

    @XmlElement(name = "AAAAllRiskCoverage", required = true)
    protected AAAAllRiskCoverage aaaAllRiskCoverage;
    @XmlElement(name = "AAACOLLCoverage", required = true)
    protected AAACOLLCoverage aaacollCoverage;
    @XmlElement(name = "AAACOMPCoverage", required = true)
    protected AAACOMPCoverage aaacompCoverage;
    @XmlElement(name = "AAAETECoverage")
    protected AAAETECoverage aaaeteCoverage;
    @XmlElement(name = "AAAGaragingAddress", required = true)
    protected AAAGaragingAddress aaaGaragingAddress;
    @XmlElement(name = "AAAVehicleCoverageBI", required = true)
    protected AAAVehicleCoverageBI aaaVehicleCoverageBI;
    @XmlElement(name = "AAAVehicleCoverageMP", required = true)
    protected AAAVehicleCoverageMP aaaVehicleCoverageMP;
    @XmlElement(name = "AAAVehicleCoveragePD", required = true)
    protected AAAVehicleCoveragePD aaaVehicleCoveragePD;
    @XmlElement(name = "AAAVehicleCoverageUIMBI", required = true)
    protected AAAVehicleCoverageUIMBI aaaVehicleCoverageUIMBI;
    @XmlElement(name = "AAAVehicleCoverageUMBI", required = true)
    protected AAAVehicleCoverageUMBI aaaVehicleCoverageUMBI;
    @XmlElement(name = "AAAGlassCoverage")
    protected AAAGlassCoverage aaaGlassCoverage;
    @XmlElement(name = "AAALoanLeaseCoverage")
    protected AAALoanLeaseCoverage aaaLoanLeaseCoverage;
    @XmlElement(name = "AAANewCarCoverage")
    protected AAANewCarCoverage aaaNewCarCoverage;
    @XmlElement(name = "AAAOEMCoverage")
    protected AAAOEMCoverage aaaoemCoverage;
    @XmlElement(name = "AAAVehicleCoverageUMPD")
    protected AAAVehicleCoverageUMPD aaaVehicleCoverageUMPD;
    @XmlElement(name = "AAARideShareCoverage")
    protected AAARideShareCoverage aaaRideShareCoverage;
    @XmlElement(name = "AAACollisionDeductibleWaiverCoverage")
    protected AAACollisionDeductibleWaiverCoverage aaaCollisionDeductibleWaiverCoverage;
    @XmlElement(name = "AAASpecialEquipmentCoverage")
    protected AAASpecialEquipmentCoverage aaaSpecialEquipmentCoverage;
    @XmlElement(name = "AAARentalReimbursementCoverage")
    protected AAARentalReimbursementCoverage aaaRentalReimbursementCoverage;
    @XmlElement(name = "AAATowingAndLaborCoverage")
    protected AAATowingAndLaborCoverage aaaTowingAndLaborCoverage;
    @XmlElement(name = "AAAVehicleOwnership")
    protected AAAVehicleOwnership aaaVehicleOwnership;
    @XmlElement(name = "AAAVehicleRatingInfo", required = true)
    protected AAAVehicleRatingInfo aaaVehicleRatingInfo;
    @XmlElement(required = true)
    protected String airBagStatusCd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AntiTheftCode antitheft;
    @XmlSchemaType(name = "string")
    protected AntiTheftCode2 antitheft2;
    protected String antiLockBreaks2;
    protected String assignedDriverOID;
    @XmlElement(name = "GEEndorsementForm")
    protected GEEndorsementForm geEndorsementForm;
    protected boolean hybrid;
    protected boolean kitCarInd;
    @XmlElement(name = "LCEndorsementForm")
    protected LCEndorsementForm lcEndorsementForm;
    @XmlElement(name = "LCEndorsementOptForm")
    protected LCEndorsementOptForm lcEndorsementOptForm;
    @XmlElement(name = "LSOPCEndorsementForm")
    protected LSOPCEndorsementForm lsopcEndorsementForm;
    @XmlElement(name = "LSOPCEndorsementOptForm")
    protected LSOPCEndorsementOptForm lsopcEndorsementOptForm;
    @XmlElement(required = true)
    protected String manufacturer;
    @XmlElement(required = true)
    protected String model;
    @XmlElement(required = true)
    protected String modelYear;
    protected String otherBodyStyleCd;
    protected String otherManufacturer;
    protected String otherModel;
    protected String otherSeries;
    @XmlElement(required = true, defaultValue = "OWN")
    @XmlSchemaType(name = "string")
    protected AAAOwnershipType ownershipTypeCd;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    @XmlElement(required = true)
    protected String principalDriverOID;
    protected String ratedDriverOID;
    protected BigDecimal seqNo;
    @XmlElement(required = true)
    protected String series;
    protected Boolean showAntiqueQuestionnaire;
    protected Boolean showBusinessQuest;
    protected Boolean showFarmQuest;
    protected Boolean showKitCarQuestionnaire;
    protected String systemRatedDriverOID;
    @XmlElement(required = true)
    protected String vehBodyTypeCd;
    protected String vehIdentificationNo;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAAVehicleType vehTypeCd;
    protected Boolean vinMatchedInd;
    protected BigDecimal vehicleDiscount;
    @XmlElement(name = "AAAAdditionalInterestChoice")
    protected List<AAAAdditionalInterestChoice> aaaAdditionalInterestChoice;
    @XmlElement(name = "Choice_AA09CAEndorsementForm")
    protected ChoiceAA09CAEndorsementForm choiceAA09CAEndorsementForm;
    @XmlElement(name = "Choice_AA47CAEndorsementForm")
    protected ChoiceAA47CAEndorsementForm choiceAA47CAEndorsementForm;
    @XmlElement(name = "Choice_AA49CAEndorsementForm")
    protected ChoiceAA49CAEndorsementForm choiceAA49CAEndorsementForm;
    @XmlElement(name = "Choice_AA53CAEndorsementForm")
    protected ChoiceAA53CAEndorsementForm choiceAA53CAEndorsementForm;
    @XmlElement(name = "Choice_AA54CAEndorsementForm")
    protected ChoiceAA54CAEndorsementForm choiceAA54CAEndorsementForm;
    @XmlElement(name = "Choice_AA59CAEndorsementForm")
    protected ChoiceAA59CAEndorsementForm choiceAA59CAEndorsementForm;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the aaaAllRiskCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAAllRiskCoverage }
     *     
     */
    public AAAAllRiskCoverage getAAAAllRiskCoverage() {
        return aaaAllRiskCoverage;
    }

    /**
     * Sets the value of the aaaAllRiskCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAAllRiskCoverage }
     *     
     */
    public void setAAAAllRiskCoverage(AAAAllRiskCoverage value) {
        this.aaaAllRiskCoverage = value;
    }

    /**
     * Gets the value of the aaacollCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAACOLLCoverage }
     *     
     */
    public AAACOLLCoverage getAAACOLLCoverage() {
        return aaacollCoverage;
    }

    /**
     * Sets the value of the aaacollCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAACOLLCoverage }
     *     
     */
    public void setAAACOLLCoverage(AAACOLLCoverage value) {
        this.aaacollCoverage = value;
    }

    /**
     * Gets the value of the aaacompCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAACOMPCoverage }
     *     
     */
    public AAACOMPCoverage getAAACOMPCoverage() {
        return aaacompCoverage;
    }

    /**
     * Sets the value of the aaacompCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAACOMPCoverage }
     *     
     */
    public void setAAACOMPCoverage(AAACOMPCoverage value) {
        this.aaacompCoverage = value;
    }

    /**
     * Gets the value of the aaaeteCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAETECoverage }
     *     
     */
    public AAAETECoverage getAAAETECoverage() {
        return aaaeteCoverage;
    }

    /**
     * Sets the value of the aaaeteCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAETECoverage }
     *     
     */
    public void setAAAETECoverage(AAAETECoverage value) {
        this.aaaeteCoverage = value;
    }

    /**
     * Gets the value of the aaaGaragingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AAAGaragingAddress }
     *     
     */
    public AAAGaragingAddress getAAAGaragingAddress() {
        return aaaGaragingAddress;
    }

    /**
     * Sets the value of the aaaGaragingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAGaragingAddress }
     *     
     */
    public void setAAAGaragingAddress(AAAGaragingAddress value) {
        this.aaaGaragingAddress = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageBI property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageBI }
     *     
     */
    public AAAVehicleCoverageBI getAAAVehicleCoverageBI() {
        return aaaVehicleCoverageBI;
    }

    /**
     * Sets the value of the aaaVehicleCoverageBI property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageBI }
     *     
     */
    public void setAAAVehicleCoverageBI(AAAVehicleCoverageBI value) {
        this.aaaVehicleCoverageBI = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageMP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageMP }
     *     
     */
    public AAAVehicleCoverageMP getAAAVehicleCoverageMP() {
        return aaaVehicleCoverageMP;
    }

    /**
     * Sets the value of the aaaVehicleCoverageMP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageMP }
     *     
     */
    public void setAAAVehicleCoverageMP(AAAVehicleCoverageMP value) {
        this.aaaVehicleCoverageMP = value;
    }

    /**
     * Gets the value of the aaaVehicleCoveragePD property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoveragePD }
     *     
     */
    public AAAVehicleCoveragePD getAAAVehicleCoveragePD() {
        return aaaVehicleCoveragePD;
    }

    /**
     * Sets the value of the aaaVehicleCoveragePD property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoveragePD }
     *     
     */
    public void setAAAVehicleCoveragePD(AAAVehicleCoveragePD value) {
        this.aaaVehicleCoveragePD = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageUIMBI property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageUIMBI }
     *     
     */
    public AAAVehicleCoverageUIMBI getAAAVehicleCoverageUIMBI() {
        return aaaVehicleCoverageUIMBI;
    }

    /**
     * Sets the value of the aaaVehicleCoverageUIMBI property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageUIMBI }
     *     
     */
    public void setAAAVehicleCoverageUIMBI(AAAVehicleCoverageUIMBI value) {
        this.aaaVehicleCoverageUIMBI = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageUMBI property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageUMBI }
     *     
     */
    public AAAVehicleCoverageUMBI getAAAVehicleCoverageUMBI() {
        return aaaVehicleCoverageUMBI;
    }

    /**
     * Sets the value of the aaaVehicleCoverageUMBI property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageUMBI }
     *     
     */
    public void setAAAVehicleCoverageUMBI(AAAVehicleCoverageUMBI value) {
        this.aaaVehicleCoverageUMBI = value;
    }

    /**
     * Gets the value of the aaaGlassCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAGlassCoverage }
     *     
     */
    public AAAGlassCoverage getAAAGlassCoverage() {
        return aaaGlassCoverage;
    }

    /**
     * Sets the value of the aaaGlassCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAGlassCoverage }
     *     
     */
    public void setAAAGlassCoverage(AAAGlassCoverage value) {
        this.aaaGlassCoverage = value;
    }

    /**
     * Gets the value of the aaaLoanLeaseCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAALoanLeaseCoverage }
     *     
     */
    public AAALoanLeaseCoverage getAAALoanLeaseCoverage() {
        return aaaLoanLeaseCoverage;
    }

    /**
     * Sets the value of the aaaLoanLeaseCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAALoanLeaseCoverage }
     *     
     */
    public void setAAALoanLeaseCoverage(AAALoanLeaseCoverage value) {
        this.aaaLoanLeaseCoverage = value;
    }

    /**
     * Gets the value of the aaaNewCarCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAANewCarCoverage }
     *     
     */
    public AAANewCarCoverage getAAANewCarCoverage() {
        return aaaNewCarCoverage;
    }

    /**
     * Sets the value of the aaaNewCarCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAANewCarCoverage }
     *     
     */
    public void setAAANewCarCoverage(AAANewCarCoverage value) {
        this.aaaNewCarCoverage = value;
    }

    /**
     * Gets the value of the aaaoemCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOEMCoverage }
     *     
     */
    public AAAOEMCoverage getAAAOEMCoverage() {
        return aaaoemCoverage;
    }

    /**
     * Sets the value of the aaaoemCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOEMCoverage }
     *     
     */
    public void setAAAOEMCoverage(AAAOEMCoverage value) {
        this.aaaoemCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageUMPD property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageUMPD }
     *     
     */
    public AAAVehicleCoverageUMPD getAAAVehicleCoverageUMPD() {
        return aaaVehicleCoverageUMPD;
    }

    /**
     * Sets the value of the aaaVehicleCoverageUMPD property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageUMPD }
     *     
     */
    public void setAAAVehicleCoverageUMPD(AAAVehicleCoverageUMPD value) {
        this.aaaVehicleCoverageUMPD = value;
    }

    /**
     * Gets the value of the aaaRideShareCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAARideShareCoverage }
     *     
     */
    public AAARideShareCoverage getAAARideShareCoverage() {
        return aaaRideShareCoverage;
    }

    /**
     * Sets the value of the aaaRideShareCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAARideShareCoverage }
     *     
     */
    public void setAAARideShareCoverage(AAARideShareCoverage value) {
        this.aaaRideShareCoverage = value;
    }

    /**
     * Gets the value of the aaaCollisionDeductibleWaiverCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAACollisionDeductibleWaiverCoverage }
     *     
     */
    public AAACollisionDeductibleWaiverCoverage getAAACollisionDeductibleWaiverCoverage() {
        return aaaCollisionDeductibleWaiverCoverage;
    }

    /**
     * Sets the value of the aaaCollisionDeductibleWaiverCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAACollisionDeductibleWaiverCoverage }
     *     
     */
    public void setAAACollisionDeductibleWaiverCoverage(AAACollisionDeductibleWaiverCoverage value) {
        this.aaaCollisionDeductibleWaiverCoverage = value;
    }

    /**
     * Gets the value of the aaaSpecialEquipmentCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAASpecialEquipmentCoverage }
     *     
     */
    public AAASpecialEquipmentCoverage getAAASpecialEquipmentCoverage() {
        return aaaSpecialEquipmentCoverage;
    }

    /**
     * Sets the value of the aaaSpecialEquipmentCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAASpecialEquipmentCoverage }
     *     
     */
    public void setAAASpecialEquipmentCoverage(AAASpecialEquipmentCoverage value) {
        this.aaaSpecialEquipmentCoverage = value;
    }

    /**
     * Gets the value of the aaaRentalReimbursementCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAARentalReimbursementCoverage }
     *     
     */
    public AAARentalReimbursementCoverage getAAARentalReimbursementCoverage() {
        return aaaRentalReimbursementCoverage;
    }

    /**
     * Sets the value of the aaaRentalReimbursementCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAARentalReimbursementCoverage }
     *     
     */
    public void setAAARentalReimbursementCoverage(AAARentalReimbursementCoverage value) {
        this.aaaRentalReimbursementCoverage = value;
    }

    /**
     * Gets the value of the aaaTowingAndLaborCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAATowingAndLaborCoverage }
     *     
     */
    public AAATowingAndLaborCoverage getAAATowingAndLaborCoverage() {
        return aaaTowingAndLaborCoverage;
    }

    /**
     * Sets the value of the aaaTowingAndLaborCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAATowingAndLaborCoverage }
     *     
     */
    public void setAAATowingAndLaborCoverage(AAATowingAndLaborCoverage value) {
        this.aaaTowingAndLaborCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleOwnership property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleOwnership }
     *     
     */
    public AAAVehicleOwnership getAAAVehicleOwnership() {
        return aaaVehicleOwnership;
    }

    /**
     * Sets the value of the aaaVehicleOwnership property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleOwnership }
     *     
     */
    public void setAAAVehicleOwnership(AAAVehicleOwnership value) {
        this.aaaVehicleOwnership = value;
    }

    /**
     * Gets the value of the aaaVehicleRatingInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleRatingInfo }
     *     
     */
    public AAAVehicleRatingInfo getAAAVehicleRatingInfo() {
        return aaaVehicleRatingInfo;
    }

    /**
     * Sets the value of the aaaVehicleRatingInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleRatingInfo }
     *     
     */
    public void setAAAVehicleRatingInfo(AAAVehicleRatingInfo value) {
        this.aaaVehicleRatingInfo = value;
    }

    /**
     * Gets the value of the airBagStatusCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirBagStatusCd() {
        return airBagStatusCd;
    }

    /**
     * Sets the value of the airBagStatusCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirBagStatusCd(String value) {
        this.airBagStatusCd = value;
    }

    /**
     * Gets the value of the antitheft property.
     * 
     * @return
     *     possible object is
     *     {@link AntiTheftCode }
     *     
     */
    public AntiTheftCode getAntitheft() {
        return antitheft;
    }

    /**
     * Sets the value of the antitheft property.
     * 
     * @param value
     *     allowed object is
     *     {@link AntiTheftCode }
     *     
     */
    public void setAntitheft(AntiTheftCode value) {
        this.antitheft = value;
    }

    /**
     * Gets the value of the antitheft2 property.
     * 
     * @return
     *     possible object is
     *     {@link AntiTheftCode2 }
     *     
     */
    public AntiTheftCode2 getAntitheft2() {
        return antitheft2;
    }

    /**
     * Sets the value of the antitheft2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link AntiTheftCode2 }
     *     
     */
    public void setAntitheft2(AntiTheftCode2 value) {
        this.antitheft2 = value;
    }

    /**
     * Gets the value of the antiLockBreaks2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAntiLockBreaks2() {
        return antiLockBreaks2;
    }

    /**
     * Sets the value of the antiLockBreaks2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAntiLockBreaks2(String value) {
        this.antiLockBreaks2 = value;
    }

    /**
     * Gets the value of the assignedDriverOID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignedDriverOID() {
        return assignedDriverOID;
    }

    /**
     * Sets the value of the assignedDriverOID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignedDriverOID(String value) {
        this.assignedDriverOID = value;
    }

    /**
     * Gets the value of the geEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link GEEndorsementForm }
     *     
     */
    public GEEndorsementForm getGEEndorsementForm() {
        return geEndorsementForm;
    }

    /**
     * Sets the value of the geEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link GEEndorsementForm }
     *     
     */
    public void setGEEndorsementForm(GEEndorsementForm value) {
        this.geEndorsementForm = value;
    }

    /**
     * Gets the value of the hybrid property.
     * 
     */
    public boolean isHybrid() {
        return hybrid;
    }

    /**
     * Sets the value of the hybrid property.
     * 
     */
    public void setHybrid(boolean value) {
        this.hybrid = value;
    }

    /**
     * Gets the value of the kitCarInd property.
     * 
     */
    public boolean isKitCarInd() {
        return kitCarInd;
    }

    /**
     * Sets the value of the kitCarInd property.
     * 
     */
    public void setKitCarInd(boolean value) {
        this.kitCarInd = value;
    }

    /**
     * Gets the value of the lcEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link LCEndorsementForm }
     *     
     */
    public LCEndorsementForm getLCEndorsementForm() {
        return lcEndorsementForm;
    }

    /**
     * Sets the value of the lcEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link LCEndorsementForm }
     *     
     */
    public void setLCEndorsementForm(LCEndorsementForm value) {
        this.lcEndorsementForm = value;
    }

    /**
     * Gets the value of the lcEndorsementOptForm property.
     * 
     * @return
     *     possible object is
     *     {@link LCEndorsementOptForm }
     *     
     */
    public LCEndorsementOptForm getLCEndorsementOptForm() {
        return lcEndorsementOptForm;
    }

    /**
     * Sets the value of the lcEndorsementOptForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link LCEndorsementOptForm }
     *     
     */
    public void setLCEndorsementOptForm(LCEndorsementOptForm value) {
        this.lcEndorsementOptForm = value;
    }

    /**
     * Gets the value of the lsopcEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link LSOPCEndorsementForm }
     *     
     */
    public LSOPCEndorsementForm getLSOPCEndorsementForm() {
        return lsopcEndorsementForm;
    }

    /**
     * Sets the value of the lsopcEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link LSOPCEndorsementForm }
     *     
     */
    public void setLSOPCEndorsementForm(LSOPCEndorsementForm value) {
        this.lsopcEndorsementForm = value;
    }

    /**
     * Gets the value of the lsopcEndorsementOptForm property.
     * 
     * @return
     *     possible object is
     *     {@link LSOPCEndorsementOptForm }
     *     
     */
    public LSOPCEndorsementOptForm getLSOPCEndorsementOptForm() {
        return lsopcEndorsementOptForm;
    }

    /**
     * Sets the value of the lsopcEndorsementOptForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link LSOPCEndorsementOptForm }
     *     
     */
    public void setLSOPCEndorsementOptForm(LSOPCEndorsementOptForm value) {
        this.lsopcEndorsementOptForm = value;
    }

    /**
     * Gets the value of the manufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the modelYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelYear() {
        return modelYear;
    }

    /**
     * Sets the value of the modelYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelYear(String value) {
        this.modelYear = value;
    }

    /**
     * Gets the value of the otherBodyStyleCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherBodyStyleCd() {
        return otherBodyStyleCd;
    }

    /**
     * Sets the value of the otherBodyStyleCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherBodyStyleCd(String value) {
        this.otherBodyStyleCd = value;
    }

    /**
     * Gets the value of the otherManufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherManufacturer() {
        return otherManufacturer;
    }

    /**
     * Sets the value of the otherManufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherManufacturer(String value) {
        this.otherManufacturer = value;
    }

    /**
     * Gets the value of the otherModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherModel() {
        return otherModel;
    }

    /**
     * Sets the value of the otherModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherModel(String value) {
        this.otherModel = value;
    }

    /**
     * Gets the value of the otherSeries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherSeries() {
        return otherSeries;
    }

    /**
     * Sets the value of the otherSeries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherSeries(String value) {
        this.otherSeries = value;
    }

    /**
     * Gets the value of the ownershipTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAOwnershipType }
     *     
     */
    public AAAOwnershipType getOwnershipTypeCd() {
        return ownershipTypeCd;
    }

    /**
     * Sets the value of the ownershipTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAOwnershipType }
     *     
     */
    public void setOwnershipTypeCd(AAAOwnershipType value) {
        this.ownershipTypeCd = value;
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
     * Gets the value of the principalDriverOID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrincipalDriverOID() {
        return principalDriverOID;
    }

    /**
     * Sets the value of the principalDriverOID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrincipalDriverOID(String value) {
        this.principalDriverOID = value;
    }

    /**
     * Gets the value of the ratedDriverOID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRatedDriverOID() {
        return ratedDriverOID;
    }

    /**
     * Sets the value of the ratedDriverOID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRatedDriverOID(String value) {
        this.ratedDriverOID = value;
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
     * Gets the value of the series property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeries() {
        return series;
    }

    /**
     * Sets the value of the series property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeries(String value) {
        this.series = value;
    }

    /**
     * Gets the value of the showAntiqueQuestionnaire property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowAntiqueQuestionnaire() {
        return showAntiqueQuestionnaire;
    }

    /**
     * Sets the value of the showAntiqueQuestionnaire property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowAntiqueQuestionnaire(Boolean value) {
        this.showAntiqueQuestionnaire = value;
    }

    /**
     * Gets the value of the showBusinessQuest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowBusinessQuest() {
        return showBusinessQuest;
    }

    /**
     * Sets the value of the showBusinessQuest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowBusinessQuest(Boolean value) {
        this.showBusinessQuest = value;
    }

    /**
     * Gets the value of the showFarmQuest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowFarmQuest() {
        return showFarmQuest;
    }

    /**
     * Sets the value of the showFarmQuest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowFarmQuest(Boolean value) {
        this.showFarmQuest = value;
    }

    /**
     * Gets the value of the showKitCarQuestionnaire property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowKitCarQuestionnaire() {
        return showKitCarQuestionnaire;
    }

    /**
     * Sets the value of the showKitCarQuestionnaire property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowKitCarQuestionnaire(Boolean value) {
        this.showKitCarQuestionnaire = value;
    }

    /**
     * Gets the value of the systemRatedDriverOID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemRatedDriverOID() {
        return systemRatedDriverOID;
    }

    /**
     * Sets the value of the systemRatedDriverOID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemRatedDriverOID(String value) {
        this.systemRatedDriverOID = value;
    }

    /**
     * Gets the value of the vehBodyTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehBodyTypeCd() {
        return vehBodyTypeCd;
    }

    /**
     * Sets the value of the vehBodyTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehBodyTypeCd(String value) {
        this.vehBodyTypeCd = value;
    }

    /**
     * Gets the value of the vehIdentificationNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehIdentificationNo() {
        return vehIdentificationNo;
    }

    /**
     * Sets the value of the vehIdentificationNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehIdentificationNo(String value) {
        this.vehIdentificationNo = value;
    }

    /**
     * Gets the value of the vehTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleType }
     *     
     */
    public AAAVehicleType getVehTypeCd() {
        return vehTypeCd;
    }

    /**
     * Sets the value of the vehTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleType }
     *     
     */
    public void setVehTypeCd(AAAVehicleType value) {
        this.vehTypeCd = value;
    }

    /**
     * Gets the value of the vinMatchedInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVinMatchedInd() {
        return vinMatchedInd;
    }

    /**
     * Sets the value of the vinMatchedInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVinMatchedInd(Boolean value) {
        this.vinMatchedInd = value;
    }

    /**
     * Gets the value of the vehicleDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVehicleDiscount() {
        return vehicleDiscount;
    }

    /**
     * Sets the value of the vehicleDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVehicleDiscount(BigDecimal value) {
        this.vehicleDiscount = value;
    }

    /**
     * Gets the value of the aaaAdditionalInterestChoice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaAdditionalInterestChoice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAAdditionalInterestChoice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAAdditionalInterestChoice }
     * 
     * 
     */
    public List<AAAAdditionalInterestChoice> getAAAAdditionalInterestChoice() {
        if (aaaAdditionalInterestChoice == null) {
            aaaAdditionalInterestChoice = new ArrayList<AAAAdditionalInterestChoice>();
        }
        return this.aaaAdditionalInterestChoice;
    }

    /**
     * Gets the value of the choiceAA09CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA09CAEndorsementForm }
     *     
     */
    public ChoiceAA09CAEndorsementForm getChoiceAA09CAEndorsementForm() {
        return choiceAA09CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA09CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA09CAEndorsementForm }
     *     
     */
    public void setChoiceAA09CAEndorsementForm(ChoiceAA09CAEndorsementForm value) {
        this.choiceAA09CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA47CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA47CAEndorsementForm }
     *     
     */
    public ChoiceAA47CAEndorsementForm getChoiceAA47CAEndorsementForm() {
        return choiceAA47CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA47CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA47CAEndorsementForm }
     *     
     */
    public void setChoiceAA47CAEndorsementForm(ChoiceAA47CAEndorsementForm value) {
        this.choiceAA47CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA49CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA49CAEndorsementForm }
     *     
     */
    public ChoiceAA49CAEndorsementForm getChoiceAA49CAEndorsementForm() {
        return choiceAA49CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA49CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA49CAEndorsementForm }
     *     
     */
    public void setChoiceAA49CAEndorsementForm(ChoiceAA49CAEndorsementForm value) {
        this.choiceAA49CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA53CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA53CAEndorsementForm }
     *     
     */
    public ChoiceAA53CAEndorsementForm getChoiceAA53CAEndorsementForm() {
        return choiceAA53CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA53CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA53CAEndorsementForm }
     *     
     */
    public void setChoiceAA53CAEndorsementForm(ChoiceAA53CAEndorsementForm value) {
        this.choiceAA53CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA54CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA54CAEndorsementForm }
     *     
     */
    public ChoiceAA54CAEndorsementForm getChoiceAA54CAEndorsementForm() {
        return choiceAA54CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA54CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA54CAEndorsementForm }
     *     
     */
    public void setChoiceAA54CAEndorsementForm(ChoiceAA54CAEndorsementForm value) {
        this.choiceAA54CAEndorsementForm = value;
    }

    /**
     * Gets the value of the choiceAA59CAEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link ChoiceAA59CAEndorsementForm }
     *     
     */
    public ChoiceAA59CAEndorsementForm getChoiceAA59CAEndorsementForm() {
        return choiceAA59CAEndorsementForm;
    }

    /**
     * Sets the value of the choiceAA59CAEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChoiceAA59CAEndorsementForm }
     *     
     */
    public void setChoiceAA59CAEndorsementForm(ChoiceAA59CAEndorsementForm value) {
        this.choiceAA59CAEndorsementForm = value;
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
