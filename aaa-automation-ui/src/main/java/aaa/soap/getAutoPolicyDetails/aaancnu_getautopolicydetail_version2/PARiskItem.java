
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.math.BigDecimal;
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
 * <p>Java class for PARiskItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PARiskItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}RiskItem">
 *       &lt;sequence>
 *         &lt;element name="PARiskIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="annualMileage" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="agreedValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="claimsFreeYears" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="confirmedAnnualMileage" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="evaluatedValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="purchasedCost" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="daytimeRunningLights" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="discountAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="distanceOneWay" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="existingDamage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="existingDamageValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodDriverCredit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="marketValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="multiCarCredit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="multiCarDiscount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="multiCar" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="newVehicle" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="numberOfDaysDrivenPerWeek" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="protectedBonus" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="purchaseDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="vehicleSymbolCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleCompSymbolCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleCollSymbolCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yearsNCD" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="namedNonOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="performanceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unitAge" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="ratedSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverAge" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="daysDrivenToWork" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="garaged" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="leased" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ratedClassCode" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ratingTierIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hybrid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="telematics" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="teenTelematics" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="waivedLiabilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="specialEquipmentInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="specialEquipmentDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="antiLockBrakesInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="salvagedInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="antiTheftInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pARiskItemExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARiskItem", propOrder = {
    "paRiskIdentifier",
    "annualMileage",
    "agreedValue",
    "claimsFreeYears",
    "confirmedAnnualMileage",
    "evaluatedValue",
    "purchasedCost",
    "daytimeRunningLights",
    "discountAmount",
    "distanceOneWay",
    "existingDamage",
    "existingDamageValue",
    "goodDriverCredit",
    "marketValue",
    "multiCarCredit",
    "multiCarDiscount",
    "multiCar",
    "newVehicle",
    "numberOfDaysDrivenPerWeek",
    "protectedBonus",
    "purchaseDate",
    "vehicleSymbolCode",
    "vehicleCompSymbolCode",
    "vehicleCollSymbolCode",
    "vehicleClass",
    "yearsNCD",
    "namedNonOwner",
    "performanceCode",
    "unitAge",
    "ratedSymbol",
    "driverAge",
    "daysDrivenToWork",
    "garaged",
    "leased",
    "ratedClassCode",
    "ratingTierIndicator",
    "hybrid",
    "telematics",
    "teenTelematics",
    "waivedLiabilityInd",
    "specialEquipmentInd",
    "specialEquipmentDesc",
    "antiLockBrakesInd",
    "salvagedInd",
    "antiTheftInd",
    "paRiskItemExtension"
})
public class PARiskItem
    extends RiskItem
{

    @XmlElement(name = "PARiskIdentifier")
    protected String paRiskIdentifier;
    protected BigInteger annualMileage;
    protected BigDecimal agreedValue;
    protected BigInteger claimsFreeYears;
    protected BigInteger confirmedAnnualMileage;
    protected BigDecimal evaluatedValue;
    protected BigDecimal purchasedCost;
    protected Boolean daytimeRunningLights;
    protected BigDecimal discountAmount;
    protected BigInteger distanceOneWay;
    protected Boolean existingDamage;
    protected String existingDamageValue;
    protected Boolean goodDriverCredit;
    protected BigDecimal marketValue;
    protected Boolean multiCarCredit;
    protected Boolean multiCarDiscount;
    protected Boolean multiCar;
    protected Boolean newVehicle;
    protected BigInteger numberOfDaysDrivenPerWeek;
    protected Boolean protectedBonus;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar purchaseDate;
    protected String vehicleSymbolCode;
    protected String vehicleCompSymbolCode;
    protected String vehicleCollSymbolCode;
    protected String vehicleClass;
    protected BigInteger yearsNCD;
    protected Boolean namedNonOwner;
    protected String performanceCode;
    protected BigInteger unitAge;
    protected String ratedSymbol;
    protected BigInteger driverAge;
    protected BigInteger daysDrivenToWork;
    protected Boolean garaged;
    protected Boolean leased;
    protected Boolean ratedClassCode;
    protected String ratingTierIndicator;
    protected Boolean hybrid;
    protected Boolean telematics;
    protected Boolean teenTelematics;
    protected Boolean waivedLiabilityInd;
    protected Boolean specialEquipmentInd;
    protected String specialEquipmentDesc;
    protected Boolean antiLockBrakesInd;
    protected Boolean salvagedInd;
    protected Boolean antiTheftInd;
    @XmlElement(name = "pARiskItemExtension")
    protected List<Object> paRiskItemExtension;

    /**
     * Gets the value of the paRiskIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPARiskIdentifier() {
        return paRiskIdentifier;
    }

    /**
     * Sets the value of the paRiskIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPARiskIdentifier(String value) {
        this.paRiskIdentifier = value;
    }

    /**
     * Gets the value of the annualMileage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAnnualMileage() {
        return annualMileage;
    }

    /**
     * Sets the value of the annualMileage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAnnualMileage(BigInteger value) {
        this.annualMileage = value;
    }

    /**
     * Gets the value of the agreedValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAgreedValue() {
        return agreedValue;
    }

    /**
     * Sets the value of the agreedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAgreedValue(BigDecimal value) {
        this.agreedValue = value;
    }

    /**
     * Gets the value of the claimsFreeYears property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getClaimsFreeYears() {
        return claimsFreeYears;
    }

    /**
     * Sets the value of the claimsFreeYears property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setClaimsFreeYears(BigInteger value) {
        this.claimsFreeYears = value;
    }

    /**
     * Gets the value of the confirmedAnnualMileage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getConfirmedAnnualMileage() {
        return confirmedAnnualMileage;
    }

    /**
     * Sets the value of the confirmedAnnualMileage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setConfirmedAnnualMileage(BigInteger value) {
        this.confirmedAnnualMileage = value;
    }

    /**
     * Gets the value of the evaluatedValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEvaluatedValue() {
        return evaluatedValue;
    }

    /**
     * Sets the value of the evaluatedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEvaluatedValue(BigDecimal value) {
        this.evaluatedValue = value;
    }

    /**
     * Gets the value of the purchasedCost property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPurchasedCost() {
        return purchasedCost;
    }

    /**
     * Sets the value of the purchasedCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPurchasedCost(BigDecimal value) {
        this.purchasedCost = value;
    }

    /**
     * Gets the value of the daytimeRunningLights property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDaytimeRunningLights() {
        return daytimeRunningLights;
    }

    /**
     * Sets the value of the daytimeRunningLights property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDaytimeRunningLights(Boolean value) {
        this.daytimeRunningLights = value;
    }

    /**
     * Gets the value of the discountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the value of the discountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDiscountAmount(BigDecimal value) {
        this.discountAmount = value;
    }

    /**
     * Gets the value of the distanceOneWay property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDistanceOneWay() {
        return distanceOneWay;
    }

    /**
     * Sets the value of the distanceOneWay property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDistanceOneWay(BigInteger value) {
        this.distanceOneWay = value;
    }

    /**
     * Gets the value of the existingDamage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExistingDamage() {
        return existingDamage;
    }

    /**
     * Sets the value of the existingDamage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExistingDamage(Boolean value) {
        this.existingDamage = value;
    }

    /**
     * Gets the value of the existingDamageValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExistingDamageValue() {
        return existingDamageValue;
    }

    /**
     * Sets the value of the existingDamageValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExistingDamageValue(String value) {
        this.existingDamageValue = value;
    }

    /**
     * Gets the value of the goodDriverCredit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGoodDriverCredit() {
        return goodDriverCredit;
    }

    /**
     * Sets the value of the goodDriverCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGoodDriverCredit(Boolean value) {
        this.goodDriverCredit = value;
    }

    /**
     * Gets the value of the marketValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMarketValue() {
        return marketValue;
    }

    /**
     * Sets the value of the marketValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMarketValue(BigDecimal value) {
        this.marketValue = value;
    }

    /**
     * Gets the value of the multiCarCredit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiCarCredit() {
        return multiCarCredit;
    }

    /**
     * Sets the value of the multiCarCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiCarCredit(Boolean value) {
        this.multiCarCredit = value;
    }

    /**
     * Gets the value of the multiCarDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiCarDiscount() {
        return multiCarDiscount;
    }

    /**
     * Sets the value of the multiCarDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiCarDiscount(Boolean value) {
        this.multiCarDiscount = value;
    }

    /**
     * Gets the value of the multiCar property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiCar() {
        return multiCar;
    }

    /**
     * Sets the value of the multiCar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiCar(Boolean value) {
        this.multiCar = value;
    }

    /**
     * Gets the value of the newVehicle property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewVehicle() {
        return newVehicle;
    }

    /**
     * Sets the value of the newVehicle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewVehicle(Boolean value) {
        this.newVehicle = value;
    }

    /**
     * Gets the value of the numberOfDaysDrivenPerWeek property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfDaysDrivenPerWeek() {
        return numberOfDaysDrivenPerWeek;
    }

    /**
     * Sets the value of the numberOfDaysDrivenPerWeek property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfDaysDrivenPerWeek(BigInteger value) {
        this.numberOfDaysDrivenPerWeek = value;
    }

    /**
     * Gets the value of the protectedBonus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProtectedBonus() {
        return protectedBonus;
    }

    /**
     * Sets the value of the protectedBonus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProtectedBonus(Boolean value) {
        this.protectedBonus = value;
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
     * Gets the value of the vehicleSymbolCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleSymbolCode() {
        return vehicleSymbolCode;
    }

    /**
     * Sets the value of the vehicleSymbolCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleSymbolCode(String value) {
        this.vehicleSymbolCode = value;
    }

    /**
     * Gets the value of the vehicleCompSymbolCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleCompSymbolCode() {
        return vehicleCompSymbolCode;
    }

    /**
     * Sets the value of the vehicleCompSymbolCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleCompSymbolCode(String value) {
        this.vehicleCompSymbolCode = value;
    }

    /**
     * Gets the value of the vehicleCollSymbolCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleCollSymbolCode() {
        return vehicleCollSymbolCode;
    }

    /**
     * Sets the value of the vehicleCollSymbolCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleCollSymbolCode(String value) {
        this.vehicleCollSymbolCode = value;
    }

    /**
     * Gets the value of the vehicleClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleClass() {
        return vehicleClass;
    }

    /**
     * Sets the value of the vehicleClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleClass(String value) {
        this.vehicleClass = value;
    }

    /**
     * Gets the value of the yearsNCD property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getYearsNCD() {
        return yearsNCD;
    }

    /**
     * Sets the value of the yearsNCD property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setYearsNCD(BigInteger value) {
        this.yearsNCD = value;
    }

    /**
     * Gets the value of the namedNonOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNamedNonOwner() {
        return namedNonOwner;
    }

    /**
     * Sets the value of the namedNonOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNamedNonOwner(Boolean value) {
        this.namedNonOwner = value;
    }

    /**
     * Gets the value of the performanceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerformanceCode() {
        return performanceCode;
    }

    /**
     * Sets the value of the performanceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerformanceCode(String value) {
        this.performanceCode = value;
    }

    /**
     * Gets the value of the unitAge property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUnitAge() {
        return unitAge;
    }

    /**
     * Sets the value of the unitAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUnitAge(BigInteger value) {
        this.unitAge = value;
    }

    /**
     * Gets the value of the ratedSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRatedSymbol() {
        return ratedSymbol;
    }

    /**
     * Sets the value of the ratedSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRatedSymbol(String value) {
        this.ratedSymbol = value;
    }

    /**
     * Gets the value of the driverAge property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDriverAge() {
        return driverAge;
    }

    /**
     * Sets the value of the driverAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDriverAge(BigInteger value) {
        this.driverAge = value;
    }

    /**
     * Gets the value of the daysDrivenToWork property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDaysDrivenToWork() {
        return daysDrivenToWork;
    }

    /**
     * Sets the value of the daysDrivenToWork property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDaysDrivenToWork(BigInteger value) {
        this.daysDrivenToWork = value;
    }

    /**
     * Gets the value of the garaged property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGaraged() {
        return garaged;
    }

    /**
     * Sets the value of the garaged property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGaraged(Boolean value) {
        this.garaged = value;
    }

    /**
     * Gets the value of the leased property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLeased() {
        return leased;
    }

    /**
     * Sets the value of the leased property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLeased(Boolean value) {
        this.leased = value;
    }

    /**
     * Gets the value of the ratedClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRatedClassCode() {
        return ratedClassCode;
    }

    /**
     * Sets the value of the ratedClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRatedClassCode(Boolean value) {
        this.ratedClassCode = value;
    }

    /**
     * Gets the value of the ratingTierIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRatingTierIndicator() {
        return ratingTierIndicator;
    }

    /**
     * Sets the value of the ratingTierIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRatingTierIndicator(String value) {
        this.ratingTierIndicator = value;
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
     * Gets the value of the telematics property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTelematics() {
        return telematics;
    }

    /**
     * Sets the value of the telematics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTelematics(Boolean value) {
        this.telematics = value;
    }

    /**
     * Gets the value of the teenTelematics property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTeenTelematics() {
        return teenTelematics;
    }

    /**
     * Sets the value of the teenTelematics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTeenTelematics(Boolean value) {
        this.teenTelematics = value;
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
     * Gets the value of the specialEquipmentInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSpecialEquipmentInd() {
        return specialEquipmentInd;
    }

    /**
     * Sets the value of the specialEquipmentInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSpecialEquipmentInd(Boolean value) {
        this.specialEquipmentInd = value;
    }

    /**
     * Gets the value of the specialEquipmentDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialEquipmentDesc() {
        return specialEquipmentDesc;
    }

    /**
     * Sets the value of the specialEquipmentDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialEquipmentDesc(String value) {
        this.specialEquipmentDesc = value;
    }

    /**
     * Gets the value of the antiLockBrakesInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAntiLockBrakesInd() {
        return antiLockBrakesInd;
    }

    /**
     * Sets the value of the antiLockBrakesInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAntiLockBrakesInd(Boolean value) {
        this.antiLockBrakesInd = value;
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
     * Gets the value of the antiTheftInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAntiTheftInd() {
        return antiTheftInd;
    }

    /**
     * Sets the value of the antiTheftInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAntiTheftInd(Boolean value) {
        this.antiTheftInd = value;
    }

    /**
     * Gets the value of the paRiskItemExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paRiskItemExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPARiskItemExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPARiskItemExtension() {
        if (paRiskItemExtension == null) {
            paRiskItemExtension = new ArrayList<Object>();
        }
        return this.paRiskItemExtension;
    }

}
