
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

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
 * <p>Java class for UBIDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBIDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vehicleIdentificationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enrollmentStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceStatusDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="voucherNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="safetyScore" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="safetyScoreDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="scoreLockDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="scoreLockIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="manualScoreLockIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="exceptionIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extns" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extns" minOccurs="0"/>
 *         &lt;element name="removeTelematicDiscountIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="uBIDetailExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBIDetail", propOrder = {
    "vehicleIdentificationNumber",
    "enrollmentStatus",
    "deviceStatus",
    "deviceStatusDate",
    "voucherNumber",
    "safetyScore",
    "safetyScoreDate",
    "scoreLockDate",
    "scoreLockIndicator",
    "manualScoreLockIndicator",
    "exceptionIndicator",
    "extns",
    "removeTelematicDiscountIndicator",
    "ubiDetailExtension"
})
public class UBIDetail {

    protected String vehicleIdentificationNumber;
    protected String enrollmentStatus;
    protected String deviceStatus;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deviceStatusDate;
    protected String voucherNumber;
    protected BigInteger safetyScore;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar safetyScoreDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar scoreLockDate;
    protected Boolean scoreLockIndicator;
    protected Boolean manualScoreLockIndicator;
    protected Boolean exceptionIndicator;
    protected Extns extns;
    protected Boolean removeTelematicDiscountIndicator;
    @XmlElement(name = "uBIDetailExtension")
    protected List<Object> ubiDetailExtension;

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
     * Gets the value of the enrollmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    /**
     * Sets the value of the enrollmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrollmentStatus(String value) {
        this.enrollmentStatus = value;
    }

    /**
     * Gets the value of the deviceStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceStatus() {
        return deviceStatus;
    }

    /**
     * Sets the value of the deviceStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceStatus(String value) {
        this.deviceStatus = value;
    }

    /**
     * Gets the value of the deviceStatusDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeviceStatusDate() {
        return deviceStatusDate;
    }

    /**
     * Sets the value of the deviceStatusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeviceStatusDate(XMLGregorianCalendar value) {
        this.deviceStatusDate = value;
    }

    /**
     * Gets the value of the voucherNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * Sets the value of the voucherNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherNumber(String value) {
        this.voucherNumber = value;
    }

    /**
     * Gets the value of the safetyScore property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSafetyScore() {
        return safetyScore;
    }

    /**
     * Sets the value of the safetyScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSafetyScore(BigInteger value) {
        this.safetyScore = value;
    }

    /**
     * Gets the value of the safetyScoreDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSafetyScoreDate() {
        return safetyScoreDate;
    }

    /**
     * Sets the value of the safetyScoreDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSafetyScoreDate(XMLGregorianCalendar value) {
        this.safetyScoreDate = value;
    }

    /**
     * Gets the value of the scoreLockDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScoreLockDate() {
        return scoreLockDate;
    }

    /**
     * Sets the value of the scoreLockDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScoreLockDate(XMLGregorianCalendar value) {
        this.scoreLockDate = value;
    }

    /**
     * Gets the value of the scoreLockIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScoreLockIndicator() {
        return scoreLockIndicator;
    }

    /**
     * Sets the value of the scoreLockIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScoreLockIndicator(Boolean value) {
        this.scoreLockIndicator = value;
    }

    /**
     * Gets the value of the manualScoreLockIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isManualScoreLockIndicator() {
        return manualScoreLockIndicator;
    }

    /**
     * Sets the value of the manualScoreLockIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setManualScoreLockIndicator(Boolean value) {
        this.manualScoreLockIndicator = value;
    }

    /**
     * Gets the value of the exceptionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExceptionIndicator() {
        return exceptionIndicator;
    }

    /**
     * Sets the value of the exceptionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExceptionIndicator(Boolean value) {
        this.exceptionIndicator = value;
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
     * Gets the value of the removeTelematicDiscountIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveTelematicDiscountIndicator() {
        return removeTelematicDiscountIndicator;
    }

    /**
     * Sets the value of the removeTelematicDiscountIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveTelematicDiscountIndicator(Boolean value) {
        this.removeTelematicDiscountIndicator = value;
    }

    /**
     * Gets the value of the ubiDetailExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ubiDetailExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUBIDetailExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getUBIDetailExtension() {
        if (ubiDetailExtension == null) {
            ubiDetailExtension = new ArrayList<Object>();
        }
        return this.ubiDetailExtension;
    }

}
