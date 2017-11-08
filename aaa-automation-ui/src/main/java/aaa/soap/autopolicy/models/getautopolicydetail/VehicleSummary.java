
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
 * <p>Java class for VehicleSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehicleSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleIdentificationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="make" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bodyType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="licensePlateNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="odometerReading" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="engineType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="offRoadVehicle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownershipTypeCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discounts" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Discounts" minOccurs="0"/>
 *         &lt;element name="UBIDetail" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}UBIDetail" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherFee" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}SurchargeAndFee" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extns" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extns" minOccurs="0"/>
 *         &lt;element name="coverages" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Coverages" minOccurs="0"/>
 *         &lt;element name="waivedLiabilityInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="additionalInterests" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AdditionalInterests" minOccurs="0"/>
 *         &lt;element name="garagingAddress" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="drivers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}VehicleDrivers" minOccurs="0"/>
 *         &lt;element name="vehiclePremium" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PremiumEntry" minOccurs="0"/>
 *         &lt;element name="riskFactors" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PARiskItem" minOccurs="0"/>
 *         &lt;element name="endorsementForms" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EndorsementForms" minOccurs="0"/>
 *         &lt;element name="vehicleOwnership" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}VehicleOwnership" minOccurs="0"/>
 *         &lt;element name="vehicleSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehicleSummary", propOrder = {
    "model",
    "vehicleIdentifier",
    "vehicleIdentificationNumber",
    "make",
    "modelYear",
    "bodyType",
    "color",
    "licensePlateNumber",
    "odometerReading",
    "engineType",
    "offRoadVehicle",
    "sequenceNumber",
    "effectiveDate",
    "expirationDate",
    "usage",
    "ownershipTypeCd",
    "discounts",
    "ubiDetail",
    "status",
    "otherFee",
    "extns",
    "coverages",
    "waivedLiabilityInd",
    "additionalInterests",
    "garagingAddress",
    "drivers",
    "vehiclePremium",
    "riskFactors",
    "endorsementForms",
    "vehicleOwnership",
    "vehicleSummaryExtension"
})
public class VehicleSummary {

    protected String model;
    protected String vehicleIdentifier;
    protected String vehicleIdentificationNumber;
    protected String make;
    protected String modelYear;
    protected String bodyType;
    protected String color;
    protected String licensePlateNumber;
    protected BigInteger odometerReading;
    protected String engineType;
    protected String offRoadVehicle;
    protected BigInteger sequenceNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effectiveDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expirationDate;
    protected String usage;
    protected String ownershipTypeCd;
    protected Discounts discounts;
    @XmlElement(name = "UBIDetail")
    protected UBIDetail ubiDetail;
    protected String status;
    @XmlElement(nillable = true)
    protected List<SurchargeAndFee> otherFee;
    protected Extns extns;
    protected Coverages coverages;
    protected Boolean waivedLiabilityInd;
    protected AdditionalInterests additionalInterests;
    protected PostalAddressSummary garagingAddress;
    protected VehicleDrivers drivers;
    protected PremiumEntry vehiclePremium;
    protected PARiskItem riskFactors;
    protected EndorsementForms endorsementForms;
    protected VehicleOwnership vehicleOwnership;
    @XmlElement(nillable = true)
    protected List<Object> vehicleSummaryExtension;

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
     * Gets the value of the vehicleIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleIdentifier() {
        return vehicleIdentifier;
    }

    /**
     * Sets the value of the vehicleIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleIdentifier(String value) {
        this.vehicleIdentifier = value;
    }

    /**
     * Gets the value of the vehicleIdentificationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    /**
     * Sets the value of the vehicleIdentificationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleIdentificationNumber(String value) {
        this.vehicleIdentificationNumber = value;
    }

    /**
     * Gets the value of the make property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the value of the make property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMake(String value) {
        this.make = value;
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
     * Gets the value of the bodyType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodyType() {
        return bodyType;
    }

    /**
     * Sets the value of the bodyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodyType(String value) {
        this.bodyType = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the licensePlateNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    /**
     * Sets the value of the licensePlateNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicensePlateNumber(String value) {
        this.licensePlateNumber = value;
    }

    /**
     * Gets the value of the odometerReading property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOdometerReading() {
        return odometerReading;
    }

    /**
     * Sets the value of the odometerReading property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOdometerReading(BigInteger value) {
        this.odometerReading = value;
    }

    /**
     * Gets the value of the engineType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineType() {
        return engineType;
    }

    /**
     * Sets the value of the engineType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineType(String value) {
        this.engineType = value;
    }

    /**
     * Gets the value of the offRoadVehicle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOffRoadVehicle() {
        return offRoadVehicle;
    }

    /**
     * Sets the value of the offRoadVehicle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffRoadVehicle(String value) {
        this.offRoadVehicle = value;
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
     * Gets the value of the usage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the value of the usage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsage(String value) {
        this.usage = value;
    }

    /**
     * Gets the value of the ownershipTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnershipTypeCd() {
        return ownershipTypeCd;
    }

    /**
     * Sets the value of the ownershipTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnershipTypeCd(String value) {
        this.ownershipTypeCd = value;
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
     * Gets the value of the ubiDetail property.
     * 
     * @return
     *     possible object is
     *     {@link UBIDetail }
     *     
     */
    public UBIDetail getUBIDetail() {
        return ubiDetail;
    }

    /**
     * Sets the value of the ubiDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBIDetail }
     *     
     */
    public void setUBIDetail(UBIDetail value) {
        this.ubiDetail = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
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
     * Gets the value of the extns property.
     * 
     * @return
     *     possible object is
     *     {@link Extns }
     *     
     */
    public Extns getExtns() {
        return extns;
    }

    /**
     * Sets the value of the extns property.
     * 
     * @param value
     *     allowed object is
     *     {@link Extns }
     *     
     */
    public void setExtns(Extns value) {
        this.extns = value;
    }

    /**
     * Gets the value of the coverages property.
     * 
     * @return
     *     possible object is
     *     {@link Coverages }
     *     
     */
    public Coverages getCoverages() {
        return coverages;
    }

    /**
     * Sets the value of the coverages property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coverages }
     *     
     */
    public void setCoverages(Coverages value) {
        this.coverages = value;
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
     * Gets the value of the additionalInterests property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalInterests }
     *     
     */
    public AdditionalInterests getAdditionalInterests() {
        return additionalInterests;
    }

    /**
     * Sets the value of the additionalInterests property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalInterests }
     *     
     */
    public void setAdditionalInterests(AdditionalInterests value) {
        this.additionalInterests = value;
    }

    /**
     * Gets the value of the garagingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSummary }
     *     
     */
    public PostalAddressSummary getGaragingAddress() {
        return garagingAddress;
    }

    /**
     * Sets the value of the garagingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSummary }
     *     
     */
    public void setGaragingAddress(PostalAddressSummary value) {
        this.garagingAddress = value;
    }

    /**
     * Gets the value of the drivers property.
     * 
     * @return
     *     possible object is
     *     {@link VehicleDrivers }
     *     
     */
    public VehicleDrivers getDrivers() {
        return drivers;
    }

    /**
     * Sets the value of the drivers property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehicleDrivers }
     *     
     */
    public void setDrivers(VehicleDrivers value) {
        this.drivers = value;
    }

    /**
     * Gets the value of the vehiclePremium property.
     * 
     * @return
     *     possible object is
     *     {@link PremiumEntry }
     *     
     */
    public PremiumEntry getVehiclePremium() {
        return vehiclePremium;
    }

    /**
     * Sets the value of the vehiclePremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link PremiumEntry }
     *     
     */
    public void setVehiclePremium(PremiumEntry value) {
        this.vehiclePremium = value;
    }

    /**
     * Gets the value of the riskFactors property.
     * 
     * @return
     *     possible object is
     *     {@link PARiskItem }
     *     
     */
    public PARiskItem getRiskFactors() {
        return riskFactors;
    }

    /**
     * Sets the value of the riskFactors property.
     * 
     * @param value
     *     allowed object is
     *     {@link PARiskItem }
     *     
     */
    public void setRiskFactors(PARiskItem value) {
        this.riskFactors = value;
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
     * Gets the value of the vehicleOwnership property.
     * 
     * @return
     *     possible object is
     *     {@link VehicleOwnership }
     *     
     */
    public VehicleOwnership getVehicleOwnership() {
        return vehicleOwnership;
    }

    /**
     * Sets the value of the vehicleOwnership property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehicleOwnership }
     *     
     */
    public void setVehicleOwnership(VehicleOwnership value) {
        this.vehicleOwnership = value;
    }

    /**
     * Gets the value of the vehicleSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicleSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicleSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getVehicleSummaryExtension() {
        if (vehicleSummaryExtension == null) {
            vehicleSummaryExtension = new ArrayList<Object>();
        }
        return this.vehicleSummaryExtension;
    }

}
