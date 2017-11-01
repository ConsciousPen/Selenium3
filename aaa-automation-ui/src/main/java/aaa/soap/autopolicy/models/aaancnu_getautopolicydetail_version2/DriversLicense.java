
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DriversLicense complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriversLicense">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="licenseIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="licenseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuedDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="revocationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="issuedState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="licenseStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mvrReport" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}MVRReport" minOccurs="0"/>
 *         &lt;element name="driversLicenseExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriversLicense", propOrder = {
    "licenseIdentifier",
    "licenseNumber",
    "issuedDate",
    "expirationDate",
    "revocationDate",
    "issuedState",
    "type",
    "licenseStatus",
    "mvrReport",
    "driversLicenseExtension"
})
public class DriversLicense {

    protected String licenseIdentifier;
    protected String licenseNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar issuedDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expirationDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar revocationDate;
    protected String issuedState;
    protected String type;
    protected String licenseStatus;
    protected MVRReport mvrReport;
    @XmlElement(nillable = true)
    protected List<Object> driversLicenseExtension;

    /**
     * Gets the value of the licenseIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseIdentifier() {
        return licenseIdentifier;
    }

    /**
     * Sets the value of the licenseIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseIdentifier(String value) {
        this.licenseIdentifier = value;
    }

    /**
     * Gets the value of the licenseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the value of the licenseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseNumber(String value) {
        this.licenseNumber = value;
    }

    /**
     * Gets the value of the issuedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIssuedDate() {
        return issuedDate;
    }

    /**
     * Sets the value of the issuedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIssuedDate(XMLGregorianCalendar value) {
        this.issuedDate = value;
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
     * Gets the value of the revocationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRevocationDate() {
        return revocationDate;
    }

    /**
     * Sets the value of the revocationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRevocationDate(XMLGregorianCalendar value) {
        this.revocationDate = value;
    }

    /**
     * Gets the value of the issuedState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuedState() {
        return issuedState;
    }

    /**
     * Sets the value of the issuedState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuedState(String value) {
        this.issuedState = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the licenseStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseStatus() {
        return licenseStatus;
    }

    /**
     * Sets the value of the licenseStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseStatus(String value) {
        this.licenseStatus = value;
    }

    /**
     * Gets the value of the mvrReport property.
     * 
     * @return
     *     possible object is
     *     {@link MVRReport }
     *     
     */
    public MVRReport getMvrReport() {
        return mvrReport;
    }

    /**
     * Sets the value of the mvrReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link MVRReport }
     *     
     */
    public void setMvrReport(MVRReport value) {
        this.mvrReport = value;
    }

    /**
     * Gets the value of the driversLicenseExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driversLicenseExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriversLicenseExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDriversLicenseExtension() {
        if (driversLicenseExtension == null) {
            driversLicenseExtension = new ArrayList<Object>();
        }
        return this.driversLicenseExtension;
    }

}
