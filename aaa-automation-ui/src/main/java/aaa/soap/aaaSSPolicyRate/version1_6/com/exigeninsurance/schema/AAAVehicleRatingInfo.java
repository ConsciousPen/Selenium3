
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAAVehicleRatingInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAVehicleRatingInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="additionalEquipmentInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="additionalEquipmentValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="calculatedAnnualMiles" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="compCollSymbol" type="{http://www.exigeninsurance.com/data/AAA_SS_DB/1.0}PhysicalDamageCollision" minOccurs="0"/&gt;
 *         &lt;element name="collSymbol" type="{http://www.exigeninsurance.com/data/AAA_SS_DB/1.0}PhysicalDamageCollision" minOccurs="0"/&gt;
 *         &lt;element name="compSymbol" type="{http://www.exigeninsurance.com/data/AAA_SS_DB/1.0}PhysicalDamageCollision" minOccurs="0"/&gt;
 *         &lt;element name="costPurchase" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="declaredAnnualmiles" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="distanceOneWay" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="distanceOneWayToWork" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="estimatedValueOfCustomization" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="numDaysDrivenPerWeek" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="odometerReading" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="odometerReadingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="existingDamage" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAExistingDamageCd" minOccurs="0"/&gt;
 *         &lt;element name="stat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="biSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pdSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="umSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mpSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vehicleUsageCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAVehicleUseCd"/&gt;
 *         &lt;element name="salvagedInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="vehSymbolCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="waivedLiabilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="fullGlassCoverageInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="newCarProtectionInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="purchaseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="existingDamageDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rejectionOfWorkLossBenefitInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="fullTortInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="firstPartyBenefit" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}FirstPartyBenefits" minOccurs="0"/&gt;
 *         &lt;element name="uimbiStacked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="umbiStacked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="lolInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="daytimeRunningLight" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}daytimeRunningLight" minOccurs="0"/&gt;
 *         &lt;element name="uimConversion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="pipDedAppTo" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}PIPDEDAPPTO" minOccurs="0"/&gt;
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
@XmlType(name = "AAAVehicleRatingInfo", propOrder = {
    "additionalEquipmentInd",
    "additionalEquipmentValue",
    "calculatedAnnualMiles",
    "compCollSymbol",
    "collSymbol",
    "compSymbol",
    "costPurchase",
    "declaredAnnualmiles",
    "distanceOneWay",
    "distanceOneWayToWork",
    "estimatedValueOfCustomization",
    "numDaysDrivenPerWeek",
    "odometerReading",
    "odometerReadingDate",
    "existingDamage",
    "stat",
    "biSymbol",
    "pdSymbol",
    "umSymbol",
    "mpSymbol",
    "vehicleUsageCd",
    "salvagedInd",
    "vehSymbolCd",
    "waivedLiabilityInd",
    "fullGlassCoverageInd",
    "newCarProtectionInd",
    "purchaseDate",
    "existingDamageDescription",
    "rejectionOfWorkLossBenefitInd",
    "fullTortInd",
    "firstPartyBenefit",
    "uimbiStacked",
    "umbiStacked",
    "lolInd",
    "daytimeRunningLight",
    "uimConversion",
    "pipDedAppTo"
})
public class AAAVehicleRatingInfo {

    protected Boolean additionalEquipmentInd;
    protected BigDecimal additionalEquipmentValue;
    protected BigDecimal calculatedAnnualMiles;
    @XmlSchemaType(name = "integer")
    protected Integer compCollSymbol;
    @XmlSchemaType(name = "integer")
    protected Integer collSymbol;
    @XmlSchemaType(name = "integer")
    protected Integer compSymbol;
    protected BigDecimal costPurchase;
    protected BigDecimal declaredAnnualmiles;
    protected BigDecimal distanceOneWay;
    protected BigDecimal distanceOneWayToWork;
    protected BigDecimal estimatedValueOfCustomization;
    protected BigDecimal numDaysDrivenPerWeek;
    protected BigDecimal odometerReading;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar odometerReadingDate;
    @XmlSchemaType(name = "string")
    protected AAAExistingDamageCd existingDamage;
    protected String stat;
    protected String biSymbol;
    protected String pdSymbol;
    protected String umSymbol;
    protected String mpSymbol;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AAAVehicleUseCd vehicleUsageCd;
    protected Boolean salvagedInd;
    protected String vehSymbolCd;
    protected Boolean waivedLiabilityInd;
    protected Boolean fullGlassCoverageInd;
    protected Boolean newCarProtectionInd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar purchaseDate;
    protected String existingDamageDescription;
    protected Boolean rejectionOfWorkLossBenefitInd;
    protected Boolean fullTortInd;
    @XmlSchemaType(name = "string")
    protected FirstPartyBenefits firstPartyBenefit;
    protected Boolean uimbiStacked;
    protected Boolean umbiStacked;
    protected Boolean lolInd;
    @XmlSchemaType(name = "string")
    protected DaytimeRunningLight daytimeRunningLight;
    protected Boolean uimConversion;
    @XmlSchemaType(name = "string")
    protected PIPDEDAPPTO pipDedAppTo;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the additionalEquipmentInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdditionalEquipmentInd() {
        return additionalEquipmentInd;
    }

    /**
     * Sets the value of the additionalEquipmentInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdditionalEquipmentInd(Boolean value) {
        this.additionalEquipmentInd = value;
    }

    /**
     * Gets the value of the additionalEquipmentValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAdditionalEquipmentValue() {
        return additionalEquipmentValue;
    }

    /**
     * Sets the value of the additionalEquipmentValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAdditionalEquipmentValue(BigDecimal value) {
        this.additionalEquipmentValue = value;
    }

    /**
     * Gets the value of the calculatedAnnualMiles property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCalculatedAnnualMiles() {
        return calculatedAnnualMiles;
    }

    /**
     * Sets the value of the calculatedAnnualMiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCalculatedAnnualMiles(BigDecimal value) {
        this.calculatedAnnualMiles = value;
    }

    /**
     * Gets the value of the compCollSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCompCollSymbol() {
        return compCollSymbol;
    }

    /**
     * Sets the value of the compCollSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCompCollSymbol(Integer value) {
        this.compCollSymbol = value;
    }

    /**
     * Gets the value of the collSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCollSymbol() {
        return collSymbol;
    }

    /**
     * Sets the value of the collSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCollSymbol(Integer value) {
        this.collSymbol = value;
    }

    /**
     * Gets the value of the compSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCompSymbol() {
        return compSymbol;
    }

    /**
     * Sets the value of the compSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCompSymbol(Integer value) {
        this.compSymbol = value;
    }

    /**
     * Gets the value of the costPurchase property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCostPurchase() {
        return costPurchase;
    }

    /**
     * Sets the value of the costPurchase property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCostPurchase(BigDecimal value) {
        this.costPurchase = value;
    }

    /**
     * Gets the value of the declaredAnnualmiles property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDeclaredAnnualmiles() {
        return declaredAnnualmiles;
    }

    /**
     * Sets the value of the declaredAnnualmiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDeclaredAnnualmiles(BigDecimal value) {
        this.declaredAnnualmiles = value;
    }

    /**
     * Gets the value of the distanceOneWay property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDistanceOneWay() {
        return distanceOneWay;
    }

    /**
     * Sets the value of the distanceOneWay property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDistanceOneWay(BigDecimal value) {
        this.distanceOneWay = value;
    }

    /**
     * Gets the value of the distanceOneWayToWork property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDistanceOneWayToWork() {
        return distanceOneWayToWork;
    }

    /**
     * Sets the value of the distanceOneWayToWork property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDistanceOneWayToWork(BigDecimal value) {
        this.distanceOneWayToWork = value;
    }

    /**
     * Gets the value of the estimatedValueOfCustomization property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEstimatedValueOfCustomization() {
        return estimatedValueOfCustomization;
    }

    /**
     * Sets the value of the estimatedValueOfCustomization property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEstimatedValueOfCustomization(BigDecimal value) {
        this.estimatedValueOfCustomization = value;
    }

    /**
     * Gets the value of the numDaysDrivenPerWeek property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumDaysDrivenPerWeek() {
        return numDaysDrivenPerWeek;
    }

    /**
     * Sets the value of the numDaysDrivenPerWeek property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumDaysDrivenPerWeek(BigDecimal value) {
        this.numDaysDrivenPerWeek = value;
    }

    /**
     * Gets the value of the odometerReading property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOdometerReading() {
        return odometerReading;
    }

    /**
     * Sets the value of the odometerReading property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOdometerReading(BigDecimal value) {
        this.odometerReading = value;
    }

    /**
     * Gets the value of the odometerReadingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOdometerReadingDate() {
        return odometerReadingDate;
    }

    /**
     * Sets the value of the odometerReadingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOdometerReadingDate(XMLGregorianCalendar value) {
        this.odometerReadingDate = value;
    }

    /**
     * Gets the value of the existingDamage property.
     * 
     * @return
     *     possible object is
     *     {@link AAAExistingDamageCd }
     *     
     */
    public AAAExistingDamageCd getExistingDamage() {
        return existingDamage;
    }

    /**
     * Sets the value of the existingDamage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAExistingDamageCd }
     *     
     */
    public void setExistingDamage(AAAExistingDamageCd value) {
        this.existingDamage = value;
    }

    /**
     * Gets the value of the stat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStat() {
        return stat;
    }

    /**
     * Sets the value of the stat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStat(String value) {
        this.stat = value;
    }

    /**
     * Gets the value of the biSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBiSymbol() {
        return biSymbol;
    }

    /**
     * Sets the value of the biSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBiSymbol(String value) {
        this.biSymbol = value;
    }

    /**
     * Gets the value of the pdSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdSymbol() {
        return pdSymbol;
    }

    /**
     * Sets the value of the pdSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdSymbol(String value) {
        this.pdSymbol = value;
    }

    /**
     * Gets the value of the umSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUmSymbol() {
        return umSymbol;
    }

    /**
     * Sets the value of the umSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUmSymbol(String value) {
        this.umSymbol = value;
    }

    /**
     * Gets the value of the mpSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMpSymbol() {
        return mpSymbol;
    }

    /**
     * Sets the value of the mpSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMpSymbol(String value) {
        this.mpSymbol = value;
    }

    /**
     * Gets the value of the vehicleUsageCd property.
     * 
     * @return
     *     possible object is
     *     {@link AAAVehicleUseCd }
     *     
     */
    public AAAVehicleUseCd getVehicleUsageCd() {
        return vehicleUsageCd;
    }

    /**
     * Sets the value of the vehicleUsageCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAVehicleUseCd }
     *     
     */
    public void setVehicleUsageCd(AAAVehicleUseCd value) {
        this.vehicleUsageCd = value;
    }

    /**
     * Gets the value of the salvagedInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSalvagedInd() {
        return salvagedInd;
    }

    /**
     * Sets the value of the salvagedInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSalvagedInd(Boolean value) {
        this.salvagedInd = value;
    }

    /**
     * Gets the value of the vehSymbolCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehSymbolCd() {
        return vehSymbolCd;
    }

    /**
     * Sets the value of the vehSymbolCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehSymbolCd(String value) {
        this.vehSymbolCd = value;
    }

    /**
     * Gets the value of the waivedLiabilityInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWaivedLiabilityInd() {
        return waivedLiabilityInd;
    }

    /**
     * Sets the value of the waivedLiabilityInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWaivedLiabilityInd(Boolean value) {
        this.waivedLiabilityInd = value;
    }

    /**
     * Gets the value of the fullGlassCoverageInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFullGlassCoverageInd() {
        return fullGlassCoverageInd;
    }

    /**
     * Sets the value of the fullGlassCoverageInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFullGlassCoverageInd(Boolean value) {
        this.fullGlassCoverageInd = value;
    }

    /**
     * Gets the value of the newCarProtectionInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewCarProtectionInd() {
        return newCarProtectionInd;
    }

    /**
     * Sets the value of the newCarProtectionInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewCarProtectionInd(Boolean value) {
        this.newCarProtectionInd = value;
    }

    /**
     * Gets the value of the purchaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the value of the purchaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPurchaseDate(XMLGregorianCalendar value) {
        this.purchaseDate = value;
    }

    /**
     * Gets the value of the existingDamageDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExistingDamageDescription() {
        return existingDamageDescription;
    }

    /**
     * Sets the value of the existingDamageDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExistingDamageDescription(String value) {
        this.existingDamageDescription = value;
    }

    /**
     * Gets the value of the rejectionOfWorkLossBenefitInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRejectionOfWorkLossBenefitInd() {
        return rejectionOfWorkLossBenefitInd;
    }

    /**
     * Sets the value of the rejectionOfWorkLossBenefitInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRejectionOfWorkLossBenefitInd(Boolean value) {
        this.rejectionOfWorkLossBenefitInd = value;
    }

    /**
     * Gets the value of the fullTortInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFullTortInd() {
        return fullTortInd;
    }

    /**
     * Sets the value of the fullTortInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFullTortInd(Boolean value) {
        this.fullTortInd = value;
    }

    /**
     * Gets the value of the firstPartyBenefit property.
     * 
     * @return
     *     possible object is
     *     {@link FirstPartyBenefits }
     *     
     */
    public FirstPartyBenefits getFirstPartyBenefit() {
        return firstPartyBenefit;
    }

    /**
     * Sets the value of the firstPartyBenefit property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirstPartyBenefits }
     *     
     */
    public void setFirstPartyBenefit(FirstPartyBenefits value) {
        this.firstPartyBenefit = value;
    }

    /**
     * Gets the value of the uimbiStacked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUimbiStacked() {
        return uimbiStacked;
    }

    /**
     * Sets the value of the uimbiStacked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUimbiStacked(Boolean value) {
        this.uimbiStacked = value;
    }

    /**
     * Gets the value of the umbiStacked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUmbiStacked() {
        return umbiStacked;
    }

    /**
     * Sets the value of the umbiStacked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUmbiStacked(Boolean value) {
        this.umbiStacked = value;
    }

    /**
     * Gets the value of the lolInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLolInd() {
        return lolInd;
    }

    /**
     * Sets the value of the lolInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLolInd(Boolean value) {
        this.lolInd = value;
    }

    /**
     * Gets the value of the daytimeRunningLight property.
     * 
     * @return
     *     possible object is
     *     {@link DaytimeRunningLight }
     *     
     */
    public DaytimeRunningLight getDaytimeRunningLight() {
        return daytimeRunningLight;
    }

    /**
     * Sets the value of the daytimeRunningLight property.
     * 
     * @param value
     *     allowed object is
     *     {@link DaytimeRunningLight }
     *     
     */
    public void setDaytimeRunningLight(DaytimeRunningLight value) {
        this.daytimeRunningLight = value;
    }

    /**
     * Gets the value of the uimConversion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUimConversion() {
        return uimConversion;
    }

    /**
     * Sets the value of the uimConversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUimConversion(Boolean value) {
        this.uimConversion = value;
    }

    /**
     * Gets the value of the pipDedAppTo property.
     * 
     * @return
     *     possible object is
     *     {@link PIPDEDAPPTO }
     *     
     */
    public PIPDEDAPPTO getPipDedAppTo() {
        return pipDedAppTo;
    }

    /**
     * Sets the value of the pipDedAppTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PIPDEDAPPTO }
     *     
     */
    public void setPipDedAppTo(PIPDEDAPPTO value) {
        this.pipDedAppTo = value;
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
