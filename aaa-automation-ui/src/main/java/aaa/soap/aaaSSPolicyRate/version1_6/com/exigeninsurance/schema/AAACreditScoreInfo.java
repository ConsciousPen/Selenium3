
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.AAAReasonForOverrideCd;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AAACreditScoreInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAACreditScoreInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="creditScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="creditScoreDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="creditScoreProvider" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="creditScoreTypeCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="overrideDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="reasonForOverride" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAReasonForOverrideCd" minOccurs="0"/&gt;
 *         &lt;element name="reportOrderOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "AAACreditScoreInfo", propOrder = {
    "creditScore",
    "creditScoreDt",
    "creditScoreProvider",
    "creditScoreTypeCd",
    "otherReason",
    "overrideDate",
    "reasonForOverride",
    "reportOrderOid",
    "userId"
})
public class AAACreditScoreInfo {

    protected String creditScore;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creditScoreDt;
    protected String creditScoreProvider;
    protected String creditScoreTypeCd;
    protected String otherReason;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar overrideDate;
    @XmlSchemaType(name = "string")
    protected AAAReasonForOverrideCd reasonForOverride;
    protected String reportOrderOid;
    protected String userId;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the creditScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditScore() {
        return creditScore;
    }

    /**
     * Sets the value of the creditScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditScore(String value) {
        this.creditScore = value;
    }

    /**
     * Gets the value of the creditScoreDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreditScoreDt() {
        return creditScoreDt;
    }

    /**
     * Sets the value of the creditScoreDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreditScoreDt(XMLGregorianCalendar value) {
        this.creditScoreDt = value;
    }

    /**
     * Gets the value of the creditScoreProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditScoreProvider() {
        return creditScoreProvider;
    }

    /**
     * Sets the value of the creditScoreProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditScoreProvider(String value) {
        this.creditScoreProvider = value;
    }

    /**
     * Gets the value of the creditScoreTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditScoreTypeCd() {
        return creditScoreTypeCd;
    }

    /**
     * Sets the value of the creditScoreTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditScoreTypeCd(String value) {
        this.creditScoreTypeCd = value;
    }

    /**
     * Gets the value of the otherReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherReason() {
        return otherReason;
    }

    /**
     * Sets the value of the otherReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherReason(String value) {
        this.otherReason = value;
    }

    /**
     * Gets the value of the overrideDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOverrideDate() {
        return overrideDate;
    }

    /**
     * Sets the value of the overrideDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOverrideDate(XMLGregorianCalendar value) {
        this.overrideDate = value;
    }

    /**
     * Gets the value of the reasonForOverride property.
     * 
     * @return
     *     possible object is
     *     {@link AAAReasonForOverrideCd }
     *     
     */
    public AAAReasonForOverrideCd getReasonForOverride() {
        return reasonForOverride;
    }

    /**
     * Sets the value of the reasonForOverride property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAReasonForOverrideCd }
     *     
     */
    public void setReasonForOverride(AAAReasonForOverrideCd value) {
        this.reasonForOverride = value;
    }

    /**
     * Gets the value of the reportOrderOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportOrderOid() {
        return reportOrderOid;
    }

    /**
     * Sets the value of the reportOrderOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportOrderOid(String value) {
        this.reportOrderOid = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
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
