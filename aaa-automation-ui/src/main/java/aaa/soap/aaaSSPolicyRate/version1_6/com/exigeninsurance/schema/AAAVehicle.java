
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

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
 * &lt;complexType name="AAAVehicle"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAAADBCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAADBCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAATDCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAATDCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAAdditionalInterest" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAAdditionalInterest" maxOccurs="3" minOccurs="0"/&gt;
 *         &lt;element name="AAACOLLCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAACOLLCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAACOMPCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAACOMPCoverage"/&gt;
 *         &lt;element name="AAAGaragingAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAGaragingAddress" minOccurs="0"/&gt;
 *         &lt;element name="AAALoanLeaseCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAALoanLeaseCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAARentalReimbursementCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAARentalReimbursementCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAASpecialEquipmentCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAASpecialEquipmentCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAATowingAndLaborCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAATowingAndLaborCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleADBPolicyVCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleADBPolicyVCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAUMPDDedCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAUMPDDedCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoveragePIP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoveragePIP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageBI"/&gt;
 *         &lt;element name="AAAVehicleCoverageMP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageMP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoveragePD" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoveragePD"/&gt;
 *         &lt;element name="AAAVehicleCoverageUIMBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageUIMBI" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageUIMPD" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageUIMPD" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageUMBI" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageUMBI" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageUMPD" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageUMPD" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageUMSUM" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageUMSUM" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageVehLimitUMPD" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageVehLimitUMPD" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleEMBCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleEMBCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleFuneralCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleFuneralCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleOBELCoverage" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleOBELCoverage" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoveragePIPDed" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoveragePIPDed" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoveragePIPMEDICAL" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoveragePIPMEDICAL" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoveragePIPWORKLOSS" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoveragePIPWORKLOSS" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageAPIP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageAPIP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageBasicPIP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageBasicPIP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageAdditionalPIP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageAdditionalPIP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageGPIP" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageGPIP" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleCoverageIL" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleCoverageIL" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleOwnership" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleOwnership" minOccurs="0"/&gt;
 *         &lt;element name="AAAVehicleRatingInfo" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAVehicleRatingInfo"/&gt;
 *         &lt;element name="airBagStatusCd" type="{http://www.exigeninsurance.com/data/AAA_SS_DB/1.0}RestraintsCode" minOccurs="0"/&gt;
 *         &lt;element name="antitheft" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAVehicleAntiTheft" minOccurs="0"/&gt;
 *         &lt;element name="ExistingDamageEndorsement" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}ExistingDamageEndorsement" minOccurs="0"/&gt;
 *         &lt;element name="GolfCartEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}GolfCartEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="hybrid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="LCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}LCEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="LCEndorsementOptForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}LCEndorsementOptForm" minOccurs="0"/&gt;
 *         &lt;element name="LSOPCEndorsementForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}LSOPCEndorsementForm" minOccurs="0"/&gt;
 *         &lt;element name="LSOPCEndorsementOptForm" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}LSOPCEndorsementOptForm" minOccurs="0"/&gt;
 *         &lt;element name="manufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="modelYear" type="{http://www.exigeninsurance.com/data/EIS/1.0}ModelYear" minOccurs="0"/&gt;
 *         &lt;element name="otherBodyStyleCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAVehOtherBodyType" minOccurs="0"/&gt;
 *         &lt;element name="otherManufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherSeries" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ownershipTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAOwnershipType" minOccurs="0"/&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}PremiumEntry" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="principalDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ratedDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="series" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="showBusinessQuest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="systemRatedDriverOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="customBusinessUseDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vehBodyTypeCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vehIdentificationNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vehTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAVehicleType"/&gt;
 *         &lt;element name="trailerTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAATrailerType" minOccurs="0"/&gt;
 *         &lt;element name="motorHomeTypeCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAMotorHomeType" minOccurs="0"/&gt;
 *         &lt;element name="primaryOperatorOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vinMatchedInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="vehicleBusinessUse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="vehicleDiscount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="AAADriverAssignment" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAADriverAssignment" maxOccurs="7" minOccurs="0"/&gt;
 *         &lt;element name="lessThan1000Miles" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="antiLockBreaks" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="AAATelematicDevice" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAATelematicDevice" minOccurs="0"/&gt;
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
@XmlType(name = "AAAVehicle", propOrder = {
    "aaaadbCoverage",
    "aaatdCoverage",
    "aaaAdditionalInterest",
    "aaacollCoverage",
    "aaacompCoverage",
    "aaaGaragingAddress",
    "aaaLoanLeaseCoverage",
    "aaaRentalReimbursementCoverage",
    "aaaSpecialEquipmentCoverage",
    "aaaTowingAndLaborCoverage",
    "aaaVehicleADBPolicyVCoverage",
    "aaaumpdDedCoverage",
    "aaaVehicleCoveragePIP",
    "aaaVehicleCoverageBI",
    "aaaVehicleCoverageMP",
    "aaaVehicleCoveragePD",
    "aaaVehicleCoverageUIMBI",
    "aaaVehicleCoverageUIMPD",
    "aaaVehicleCoverageUMBI",
    "aaaVehicleCoverageUMPD",
    "aaaVehicleCoverageUMSUM",
    "aaaVehicleCoverageVehLimitUMPD",
    "aaaVehicleEMBCoverage",
    "aaaVehicleFuneralCoverage",
    "aaaVehicleOBELCoverage",
    "aaaVehicleCoveragePIPDed",
    "aaaVehicleCoveragePIPMEDICAL",
    "aaaVehicleCoveragePIPWORKLOSS",
    "aaaVehicleCoverageAPIP",
    "aaaVehicleCoverageBasicPIP",
    "aaaVehicleCoverageAdditionalPIP",
    "aaaVehicleCoverageGPIP",
    "aaaVehicleCoverageIL",
    "aaaVehicleOwnership",
    "aaaVehicleRatingInfo",
    "airBagStatusCd",
    "antitheft",
    "existingDamageEndorsement",
    "golfCartEndorsementForm",
    "hybrid",
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
    "showBusinessQuest",
    "systemRatedDriverOID",
    "customBusinessUseDescription",
    "vehBodyTypeCd",
    "vehIdentificationNo",
    "vehTypeCd",
    "trailerTypeCd",
    "motorHomeTypeCd",
    "primaryOperatorOid",
    "vinMatchedInd",
    "vehicleBusinessUse",
    "vehicleDiscount",
    "aaaDriverAssignment",
    "lessThan1000Miles",
    "antiLockBreaks",
    "aaaTelematicDevice"
})
public class AAAVehicle {

    @XmlElement(name = "AAAADBCoverage")
    protected AAAADBCoverage aaaadbCoverage;
    @XmlElement(name = "AAATDCoverage")
    protected AAATDCoverage aaatdCoverage;
    @XmlElement(name = "AAAAdditionalInterest")
    protected List<AAAAdditionalInterest> aaaAdditionalInterest;
    @XmlElement(name = "AAACOLLCoverage")
    protected AAACOLLCoverage aaacollCoverage;
    @XmlElement(name = "AAACOMPCoverage", required = true)
    protected AAACOMPCoverage aaacompCoverage;
    @XmlElement(name = "AAAGaragingAddress")
    protected AAAGaragingAddress aaaGaragingAddress;
    @XmlElement(name = "AAALoanLeaseCoverage")
    protected AAALoanLeaseCoverage aaaLoanLeaseCoverage;
    @XmlElement(name = "AAARentalReimbursementCoverage")
    protected AAARentalReimbursementCoverage aaaRentalReimbursementCoverage;
    @XmlElement(name = "AAASpecialEquipmentCoverage")
    protected AAASpecialEquipmentCoverage aaaSpecialEquipmentCoverage;
    @XmlElement(name = "AAATowingAndLaborCoverage")
    protected AAATowingAndLaborCoverage aaaTowingAndLaborCoverage;
    @XmlElement(name = "AAAVehicleADBPolicyVCoverage")
    protected AAAVehicleADBPolicyVCoverage aaaVehicleADBPolicyVCoverage;
    @XmlElement(name = "AAAUMPDDedCoverage")
    protected AAAUMPDDedCoverage aaaumpdDedCoverage;
    @XmlElement(name = "AAAVehicleCoveragePIP")
    protected AAAVehicleCoveragePIP aaaVehicleCoveragePIP;
    @XmlElement(name = "AAAVehicleCoverageBI", required = true)
    protected AAAVehicleCoverageBI aaaVehicleCoverageBI;
    @XmlElement(name = "AAAVehicleCoverageMP")
    protected AAAVehicleCoverageMP aaaVehicleCoverageMP;
    @XmlElement(name = "AAAVehicleCoveragePD", required = true)
    protected AAAVehicleCoveragePD aaaVehicleCoveragePD;
    @XmlElement(name = "AAAVehicleCoverageUIMBI")
    protected AAAVehicleCoverageUIMBI aaaVehicleCoverageUIMBI;
    @XmlElement(name = "AAAVehicleCoverageUIMPD")
    protected AAAVehicleCoverageUIMPD aaaVehicleCoverageUIMPD;
    @XmlElement(name = "AAAVehicleCoverageUMBI")
    protected AAAVehicleCoverageUMBI aaaVehicleCoverageUMBI;
    @XmlElement(name = "AAAVehicleCoverageUMPD")
    protected AAAVehicleCoverageUMPD aaaVehicleCoverageUMPD;
    @XmlElement(name = "AAAVehicleCoverageUMSUM")
    protected AAAVehicleCoverageUMSUM aaaVehicleCoverageUMSUM;
    @XmlElement(name = "AAAVehicleCoverageVehLimitUMPD")
    protected AAAVehicleCoverageVehLimitUMPD aaaVehicleCoverageVehLimitUMPD;
    @XmlElement(name = "AAAVehicleEMBCoverage")
    protected AAAVehicleEMBCoverage aaaVehicleEMBCoverage;
    @XmlElement(name = "AAAVehicleFuneralCoverage")
    protected AAAVehicleFuneralCoverage aaaVehicleFuneralCoverage;
    @XmlElement(name = "AAAVehicleOBELCoverage")
    protected AAAVehicleOBELCoverage aaaVehicleOBELCoverage;
    @XmlElement(name = "AAAVehicleCoveragePIPDed")
    protected AAAVehicleCoveragePIPDed aaaVehicleCoveragePIPDed;
    @XmlElement(name = "AAAVehicleCoveragePIPMEDICAL")
    protected AAAVehicleCoveragePIPMEDICAL aaaVehicleCoveragePIPMEDICAL;
    @XmlElement(name = "AAAVehicleCoveragePIPWORKLOSS")
    protected AAAVehicleCoveragePIPWORKLOSS aaaVehicleCoveragePIPWORKLOSS;
    @XmlElement(name = "AAAVehicleCoverageAPIP")
    protected AAAVehicleCoverageAPIP aaaVehicleCoverageAPIP;
    @XmlElement(name = "AAAVehicleCoverageBasicPIP")
    protected AAAVehicleCoverageBasicPIP aaaVehicleCoverageBasicPIP;
    @XmlElement(name = "AAAVehicleCoverageAdditionalPIP")
    protected AAAVehicleCoverageAdditionalPIP aaaVehicleCoverageAdditionalPIP;
    @XmlElement(name = "AAAVehicleCoverageGPIP")
    protected AAAVehicleCoverageGPIP aaaVehicleCoverageGPIP;
    @XmlElement(name = "AAAVehicleCoverageIL")
    protected AAAVehicleCoverageIL aaaVehicleCoverageIL;
    @XmlElement(name = "AAAVehicleOwnership")
    protected AAAVehicleOwnership aaaVehicleOwnership;
    @XmlElement(name = "AAAVehicleRatingInfo", required = true)
    protected AAAVehicleRatingInfo aaaVehicleRatingInfo;
    @XmlElement(defaultValue = "None")
    protected String airBagStatusCd;
    protected String antitheft;
    @XmlElement(name = "ExistingDamageEndorsement")
    protected ExistingDamageEndorsement existingDamageEndorsement;
    @XmlElement(name = "GolfCartEndorsementForm")
    protected GolfCartEndorsementForm golfCartEndorsementForm;
    protected Boolean hybrid;
    @XmlElement(name = "LCEndorsementForm")
    protected LCEndorsementForm lcEndorsementForm;
    @XmlElement(name = "LCEndorsementOptForm")
    protected LCEndorsementOptForm lcEndorsementOptForm;
    @XmlElement(name = "LSOPCEndorsementForm")
    protected LSOPCEndorsementForm lsopcEndorsementForm;
    @XmlElement(name = "LSOPCEndorsementOptForm")
    protected LSOPCEndorsementOptForm lsopcEndorsementOptForm;
    protected String manufacturer;
    protected String model;
    @XmlSchemaType(name = "integer")
    protected Integer modelYear;
    @XmlSchemaType(name = "string")
    protected AAAVehOtherBodyType otherBodyStyleCd;
    protected String otherManufacturer;
    protected String otherModel;
    protected String otherSeries;
    @XmlElement(defaultValue = "OWN")
    @XmlSchemaType(name = "string")
    protected AAAOwnershipType ownershipTypeCd;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    protected String principalDriverOID;
    protected String ratedDriverOID;
    protected BigDecimal seqNo;
    protected String series;
    @XmlElement(defaultValue = "true")
    protected Boolean showBusinessQuest;
    protected String systemRatedDriverOID;
    protected String customBusinessUseDescription;
    @XmlElement(defaultValue = "OTHER")
    protected String vehBodyTypeCd;
    protected String vehIdentificationNo;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAAVehicleType vehTypeCd;
    @XmlSchemaType(name = "string")
    protected AAATrailerType trailerTypeCd;
    @XmlSchemaType(name = "string")
    protected AAAMotorHomeType motorHomeTypeCd;
    protected String primaryOperatorOid;
    protected Boolean vinMatchedInd;
    protected Boolean vehicleBusinessUse;
    protected BigDecimal vehicleDiscount;
    @XmlElement(name = "AAADriverAssignment")
    protected List<AAADriverAssignment> aaaDriverAssignment;
    @XmlElement(defaultValue = "false")
    protected Boolean lessThan1000Miles;
    @XmlElement(defaultValue = "false")
    protected Boolean antiLockBreaks;
    @XmlElement(name = "AAATelematicDevice")
    protected AAATelematicDevice aaaTelematicDevice;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState componentState;

    /**
     * Gets the value of the aaaadbCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAADBCoverage }
     *     
     */
    public AAAADBCoverage getAAAADBCoverage() {
        return aaaadbCoverage;
    }

    /**
     * Sets the value of the aaaadbCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAADBCoverage }
     *     
     */
    public void setAAAADBCoverage(AAAADBCoverage value) {
        this.aaaadbCoverage = value;
    }

    /**
     * Gets the value of the aaatdCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAATDCoverage }
     *     
     */
    public AAATDCoverage getAAATDCoverage() {
        return aaatdCoverage;
    }

    /**
     * Sets the value of the aaatdCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAATDCoverage }
     *     
     */
    public void setAAATDCoverage(AAATDCoverage value) {
        this.aaatdCoverage = value;
    }

    /**
     * Gets the value of the aaaAdditionalInterest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaAdditionalInterest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAAdditionalInterest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAAdditionalInterest }
     * 
     * 
     */
    public List<AAAAdditionalInterest> getAAAAdditionalInterest() {
        if (aaaAdditionalInterest == null) {
            aaaAdditionalInterest = new ArrayList<AAAAdditionalInterest>();
        }
        return this.aaaAdditionalInterest;
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
     * Gets the value of the aaaVehicleADBPolicyVCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleADBPolicyVCoverage }
     *     
     */
    public AAAVehicleADBPolicyVCoverage getAAAVehicleADBPolicyVCoverage() {
        return aaaVehicleADBPolicyVCoverage;
    }

    /**
     * Sets the value of the aaaVehicleADBPolicyVCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleADBPolicyVCoverage }
     *     
     */
    public void setAAAVehicleADBPolicyVCoverage(AAAVehicleADBPolicyVCoverage value) {
        this.aaaVehicleADBPolicyVCoverage = value;
    }

    /**
     * Gets the value of the aaaumpdDedCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAUMPDDedCoverage }
     *     
     */
    public AAAUMPDDedCoverage getAAAUMPDDedCoverage() {
        return aaaumpdDedCoverage;
    }

    /**
     * Sets the value of the aaaumpdDedCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAUMPDDedCoverage }
     *     
     */
    public void setAAAUMPDDedCoverage(AAAUMPDDedCoverage value) {
        this.aaaumpdDedCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleCoveragePIP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoveragePIP }
     *     
     */
    public AAAVehicleCoveragePIP getAAAVehicleCoveragePIP() {
        return aaaVehicleCoveragePIP;
    }

    /**
     * Sets the value of the aaaVehicleCoveragePIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoveragePIP }
     *     
     */
    public void setAAAVehicleCoveragePIP(AAAVehicleCoveragePIP value) {
        this.aaaVehicleCoveragePIP = value;
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
     * Gets the value of the aaaVehicleCoverageUIMPD property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageUIMPD }
     *     
     */
    public AAAVehicleCoverageUIMPD getAAAVehicleCoverageUIMPD() {
        return aaaVehicleCoverageUIMPD;
    }

    /**
     * Sets the value of the aaaVehicleCoverageUIMPD property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageUIMPD }
     *     
     */
    public void setAAAVehicleCoverageUIMPD(AAAVehicleCoverageUIMPD value) {
        this.aaaVehicleCoverageUIMPD = value;
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
     * Gets the value of the aaaVehicleCoverageUMSUM property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageUMSUM }
     *     
     */
    public AAAVehicleCoverageUMSUM getAAAVehicleCoverageUMSUM() {
        return aaaVehicleCoverageUMSUM;
    }

    /**
     * Sets the value of the aaaVehicleCoverageUMSUM property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageUMSUM }
     *     
     */
    public void setAAAVehicleCoverageUMSUM(AAAVehicleCoverageUMSUM value) {
        this.aaaVehicleCoverageUMSUM = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageVehLimitUMPD property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageVehLimitUMPD }
     *     
     */
    public AAAVehicleCoverageVehLimitUMPD getAAAVehicleCoverageVehLimitUMPD() {
        return aaaVehicleCoverageVehLimitUMPD;
    }

    /**
     * Sets the value of the aaaVehicleCoverageVehLimitUMPD property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageVehLimitUMPD }
     *     
     */
    public void setAAAVehicleCoverageVehLimitUMPD(AAAVehicleCoverageVehLimitUMPD value) {
        this.aaaVehicleCoverageVehLimitUMPD = value;
    }

    /**
     * Gets the value of the aaaVehicleEMBCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleEMBCoverage }
     *     
     */
    public AAAVehicleEMBCoverage getAAAVehicleEMBCoverage() {
        return aaaVehicleEMBCoverage;
    }

    /**
     * Sets the value of the aaaVehicleEMBCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleEMBCoverage }
     *     
     */
    public void setAAAVehicleEMBCoverage(AAAVehicleEMBCoverage value) {
        this.aaaVehicleEMBCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleFuneralCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleFuneralCoverage }
     *     
     */
    public AAAVehicleFuneralCoverage getAAAVehicleFuneralCoverage() {
        return aaaVehicleFuneralCoverage;
    }

    /**
     * Sets the value of the aaaVehicleFuneralCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleFuneralCoverage }
     *     
     */
    public void setAAAVehicleFuneralCoverage(AAAVehicleFuneralCoverage value) {
        this.aaaVehicleFuneralCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleOBELCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleOBELCoverage }
     *     
     */
    public AAAVehicleOBELCoverage getAAAVehicleOBELCoverage() {
        return aaaVehicleOBELCoverage;
    }

    /**
     * Sets the value of the aaaVehicleOBELCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleOBELCoverage }
     *     
     */
    public void setAAAVehicleOBELCoverage(AAAVehicleOBELCoverage value) {
        this.aaaVehicleOBELCoverage = value;
    }

    /**
     * Gets the value of the aaaVehicleCoveragePIPDed property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoveragePIPDed }
     *     
     */
    public AAAVehicleCoveragePIPDed getAAAVehicleCoveragePIPDed() {
        return aaaVehicleCoveragePIPDed;
    }

    /**
     * Sets the value of the aaaVehicleCoveragePIPDed property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoveragePIPDed }
     *     
     */
    public void setAAAVehicleCoveragePIPDed(AAAVehicleCoveragePIPDed value) {
        this.aaaVehicleCoveragePIPDed = value;
    }

    /**
     * Gets the value of the aaaVehicleCoveragePIPMEDICAL property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoveragePIPMEDICAL }
     *     
     */
    public AAAVehicleCoveragePIPMEDICAL getAAAVehicleCoveragePIPMEDICAL() {
        return aaaVehicleCoveragePIPMEDICAL;
    }

    /**
     * Sets the value of the aaaVehicleCoveragePIPMEDICAL property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoveragePIPMEDICAL }
     *     
     */
    public void setAAAVehicleCoveragePIPMEDICAL(AAAVehicleCoveragePIPMEDICAL value) {
        this.aaaVehicleCoveragePIPMEDICAL = value;
    }

    /**
     * Gets the value of the aaaVehicleCoveragePIPWORKLOSS property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoveragePIPWORKLOSS }
     *     
     */
    public AAAVehicleCoveragePIPWORKLOSS getAAAVehicleCoveragePIPWORKLOSS() {
        return aaaVehicleCoveragePIPWORKLOSS;
    }

    /**
     * Sets the value of the aaaVehicleCoveragePIPWORKLOSS property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoveragePIPWORKLOSS }
     *     
     */
    public void setAAAVehicleCoveragePIPWORKLOSS(AAAVehicleCoveragePIPWORKLOSS value) {
        this.aaaVehicleCoveragePIPWORKLOSS = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageAPIP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageAPIP }
     *     
     */
    public AAAVehicleCoverageAPIP getAAAVehicleCoverageAPIP() {
        return aaaVehicleCoverageAPIP;
    }

    /**
     * Sets the value of the aaaVehicleCoverageAPIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageAPIP }
     *     
     */
    public void setAAAVehicleCoverageAPIP(AAAVehicleCoverageAPIP value) {
        this.aaaVehicleCoverageAPIP = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageBasicPIP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageBasicPIP }
     *     
     */
    public AAAVehicleCoverageBasicPIP getAAAVehicleCoverageBasicPIP() {
        return aaaVehicleCoverageBasicPIP;
    }

    /**
     * Sets the value of the aaaVehicleCoverageBasicPIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageBasicPIP }
     *     
     */
    public void setAAAVehicleCoverageBasicPIP(AAAVehicleCoverageBasicPIP value) {
        this.aaaVehicleCoverageBasicPIP = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageAdditionalPIP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageAdditionalPIP }
     *     
     */
    public AAAVehicleCoverageAdditionalPIP getAAAVehicleCoverageAdditionalPIP() {
        return aaaVehicleCoverageAdditionalPIP;
    }

    /**
     * Sets the value of the aaaVehicleCoverageAdditionalPIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageAdditionalPIP }
     *     
     */
    public void setAAAVehicleCoverageAdditionalPIP(AAAVehicleCoverageAdditionalPIP value) {
        this.aaaVehicleCoverageAdditionalPIP = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageGPIP property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageGPIP }
     *     
     */
    public AAAVehicleCoverageGPIP getAAAVehicleCoverageGPIP() {
        return aaaVehicleCoverageGPIP;
    }

    /**
     * Sets the value of the aaaVehicleCoverageGPIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageGPIP }
     *     
     */
    public void setAAAVehicleCoverageGPIP(AAAVehicleCoverageGPIP value) {
        this.aaaVehicleCoverageGPIP = value;
    }

    /**
     * Gets the value of the aaaVehicleCoverageIL property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleCoverageIL }
     *     
     */
    public AAAVehicleCoverageIL getAAAVehicleCoverageIL() {
        return aaaVehicleCoverageIL;
    }

    /**
     * Sets the value of the aaaVehicleCoverageIL property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleCoverageIL }
     *     
     */
    public void setAAAVehicleCoverageIL(AAAVehicleCoverageIL value) {
        this.aaaVehicleCoverageIL = value;
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
     *     {@link String }
     *     
     */
    public String getAntitheft() {
        return antitheft;
    }

    /**
     * Sets the value of the antitheft property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAntitheft(String value) {
        this.antitheft = value;
    }

    /**
     * Gets the value of the existingDamageEndorsement property.
     * 
     * @return
     *     possible object is
     *     {@link ExistingDamageEndorsement }
     *     
     */
    public ExistingDamageEndorsement getExistingDamageEndorsement() {
        return existingDamageEndorsement;
    }

    /**
     * Sets the value of the existingDamageEndorsement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExistingDamageEndorsement }
     *     
     */
    public void setExistingDamageEndorsement(ExistingDamageEndorsement value) {
        this.existingDamageEndorsement = value;
    }

    /**
     * Gets the value of the golfCartEndorsementForm property.
     * 
     * @return
     *     possible object is
     *     {@link GolfCartEndorsementForm }
     *     
     */
    public GolfCartEndorsementForm getGolfCartEndorsementForm() {
        return golfCartEndorsementForm;
    }

    /**
     * Sets the value of the golfCartEndorsementForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link GolfCartEndorsementForm }
     *     
     */
    public void setGolfCartEndorsementForm(GolfCartEndorsementForm value) {
        this.golfCartEndorsementForm = value;
    }

    /**
     * Gets the value of the hybrid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHybrid() {
        return hybrid;
    }

    /**
     * Sets the value of the hybrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHybrid(Boolean value) {
        this.hybrid = value;
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
     *     {@link Integer }
     *     
     */
    public Integer getModelYear() {
        return modelYear;
    }

    /**
     * Sets the value of the modelYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setModelYear(Integer value) {
        this.modelYear = value;
    }

    /**
     * Gets the value of the otherBodyStyleCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehOtherBodyType }
     *     
     */
    public AAAVehOtherBodyType getOtherBodyStyleCd() {
        return otherBodyStyleCd;
    }

    /**
     * Sets the value of the otherBodyStyleCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehOtherBodyType }
     *     
     */
    public void setOtherBodyStyleCd(AAAVehOtherBodyType value) {
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
     * Gets the value of the customBusinessUseDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomBusinessUseDescription() {
        return customBusinessUseDescription;
    }

    /**
     * Sets the value of the customBusinessUseDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomBusinessUseDescription(String value) {
        this.customBusinessUseDescription = value;
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
     * Gets the value of the trailerTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAATrailerType }
     *     
     */
    public AAATrailerType getTrailerTypeCd() {
        return trailerTypeCd;
    }

    /**
     * Sets the value of the trailerTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAATrailerType }
     *     
     */
    public void setTrailerTypeCd(AAATrailerType value) {
        this.trailerTypeCd = value;
    }

    /**
     * Gets the value of the motorHomeTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAMotorHomeType }
     *     
     */
    public AAAMotorHomeType getMotorHomeTypeCd() {
        return motorHomeTypeCd;
    }

    /**
     * Sets the value of the motorHomeTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAMotorHomeType }
     *     
     */
    public void setMotorHomeTypeCd(AAAMotorHomeType value) {
        this.motorHomeTypeCd = value;
    }

    /**
     * Gets the value of the primaryOperatorOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryOperatorOid() {
        return primaryOperatorOid;
    }

    /**
     * Sets the value of the primaryOperatorOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryOperatorOid(String value) {
        this.primaryOperatorOid = value;
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
     * Gets the value of the vehicleBusinessUse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVehicleBusinessUse() {
        return vehicleBusinessUse;
    }

    /**
     * Sets the value of the vehicleBusinessUse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVehicleBusinessUse(Boolean value) {
        this.vehicleBusinessUse = value;
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
     * Gets the value of the aaaDriverAssignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaDriverAssignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAADriverAssignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAADriverAssignment }
     * 
     * 
     */
    public List<AAADriverAssignment> getAAADriverAssignment() {
        if (aaaDriverAssignment == null) {
            aaaDriverAssignment = new ArrayList<AAADriverAssignment>();
        }
        return this.aaaDriverAssignment;
    }

    /**
     * Gets the value of the lessThan1000Miles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLessThan1000Miles() {
        return lessThan1000Miles;
    }

    /**
     * Sets the value of the lessThan1000Miles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLessThan1000Miles(Boolean value) {
        this.lessThan1000Miles = value;
    }

    /**
     * Gets the value of the antiLockBreaks property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAntiLockBreaks() {
        return antiLockBreaks;
    }

    /**
     * Sets the value of the antiLockBreaks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAntiLockBreaks(Boolean value) {
        this.antiLockBreaks = value;
    }

    /**
     * Gets the value of the aaaTelematicDevice property.
     * 
     * @return
     *     possible object is
     *     {@link AAATelematicDevice }
     *     
     */
    public AAATelematicDevice getAAATelematicDevice() {
        return aaaTelematicDevice;
    }

    /**
     * Sets the value of the aaaTelematicDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAATelematicDevice }
     *     
     */
    public void setAAATelematicDevice(AAATelematicDevice value) {
        this.aaaTelematicDevice = value;
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
        return componentState;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param componentState
     *     allowed object is
     *     {@link ComponentState }
     *     
     */
    public void setComponentState(ComponentState componentState) {
        this.componentState = componentState;
    }

}
