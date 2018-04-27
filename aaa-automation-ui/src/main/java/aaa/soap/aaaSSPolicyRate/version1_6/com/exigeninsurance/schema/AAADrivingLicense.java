
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.LicenseClass;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.StateProvCd;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAADrivingLicense complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAADrivingLicense"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="licensedDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="licensePermitNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="licenseStatusCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}LicenseClass"/&gt;
 *         &lt;element name="stateProvCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd" minOccurs="0"/&gt;
 *         &lt;element name="yearsLicensed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
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
@XmlType(name = "AAADrivingLicense", propOrder = {
    "licensedDt",
    "licensePermitNumber",
    "licenseStatusCd",
    "stateProvCd",
    "yearsLicensed"
})
public class AAADrivingLicense {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar licensedDt;
    protected String licensePermitNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LicenseClass licenseStatusCd;
    @XmlSchemaType(name = "string")
    protected StateProvCd stateProvCd;
    protected BigDecimal yearsLicensed;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the licensedDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLicensedDt() {
        return licensedDt;
    }

    /**
     * Sets the value of the licensedDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLicensedDt(XMLGregorianCalendar value) {
        this.licensedDt = value;
    }

    /**
     * Gets the value of the licensePermitNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicensePermitNumber() {
        return licensePermitNumber;
    }

    /**
     * Sets the value of the licensePermitNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicensePermitNumber(String value) {
        this.licensePermitNumber = value;
    }

    /**
     * Gets the value of the licenseStatusCd property.
     * 
     * @return
     *     possible object is
     *     {@link LicenseClass }
     *     
     */
    public LicenseClass getLicenseStatusCd() {
        return licenseStatusCd;
    }

    /**
     * Sets the value of the licenseStatusCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicenseClass }
     *     
     */
    public void setLicenseStatusCd(LicenseClass value) {
        this.licenseStatusCd = value;
    }

    /**
     * Gets the value of the stateProvCd property.
     * 
     * @return
     *     possible object is
     *     {@link StateProvCd }
     *     
     */
    public StateProvCd getStateProvCd() {
        return stateProvCd;
    }

    /**
     * Sets the value of the stateProvCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateProvCd }
     *     
     */
    public void setStateProvCd(StateProvCd value) {
        this.stateProvCd = value;
    }

    /**
     * Gets the value of the yearsLicensed property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getYearsLicensed() {
        return yearsLicensed;
    }

    /**
     * Sets the value of the yearsLicensed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setYearsLicensed(BigDecimal value) {
        this.yearsLicensed = value;
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
