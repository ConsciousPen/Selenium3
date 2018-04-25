
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.AAAActivitySource;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.AAAActivityType;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.ActivityDescription;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAASSDrivingRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAASSDrivingRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accidentViolationDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="convictionDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="waiverStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="activitySource" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAActivitySource" minOccurs="0"/&gt;
 *         &lt;element name="includeInRatingOverridable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="activityType" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAActivityType" minOccurs="0"/&gt;
 *         &lt;element name="incidentDescription" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}ActivityDescription" minOccurs="0"/&gt;
 *         &lt;element name="removable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="includeToRatingInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="lossPaymentAmt" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="numAccidentPoints" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="numConvictionPoints" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="svcDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "AAASSDrivingRecord", propOrder = {
    "accidentViolationDt",
    "convictionDt",
    "waiverStatus",
    "activitySource",
    "includeInRatingOverridable",
    "activityType",
    "incidentDescription",
    "removable",
    "includeToRatingInd",
    "lossPaymentAmt",
    "numAccidentPoints",
    "numConvictionPoints",
    "svcDescription"
})
public class AAASSDrivingRecord {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar accidentViolationDt;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar convictionDt;
    protected String waiverStatus;
    @XmlSchemaType(name = "string")
    protected AAAActivitySource activitySource;
    protected Boolean includeInRatingOverridable;
    @XmlSchemaType(name = "string")
    protected AAAActivityType activityType;
    @XmlSchemaType(name = "string")
    protected ActivityDescription incidentDescription;
    protected Boolean removable;
    protected Boolean includeToRatingInd;
    protected BigDecimal lossPaymentAmt;
    protected BigDecimal numAccidentPoints;
    protected BigDecimal numConvictionPoints;
    protected String svcDescription;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the accidentViolationDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAccidentViolationDt() {
        return accidentViolationDt;
    }

    /**
     * Sets the value of the accidentViolationDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAccidentViolationDt(XMLGregorianCalendar value) {
        this.accidentViolationDt = value;
    }

    /**
     * Gets the value of the convictionDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getConvictionDt() {
        return convictionDt;
    }

    /**
     * Sets the value of the convictionDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setConvictionDt(XMLGregorianCalendar value) {
        this.convictionDt = value;
    }

    /**
     * Gets the value of the waiverStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaiverStatus() {
        return waiverStatus;
    }

    /**
     * Sets the value of the waiverStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaiverStatus(String value) {
        this.waiverStatus = value;
    }

    /**
     * Gets the value of the activitySource property.
     * 
     * @return
     *     possible object is
     *     {@link AAAActivitySource }
     *     
     */
    public AAAActivitySource getActivitySource() {
        return activitySource;
    }

    /**
     * Sets the value of the activitySource property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAActivitySource }
     *     
     */
    public void setActivitySource(AAAActivitySource value) {
        this.activitySource = value;
    }

    /**
     * Gets the value of the includeInRatingOverridable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeInRatingOverridable() {
        return includeInRatingOverridable;
    }

    /**
     * Sets the value of the includeInRatingOverridable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeInRatingOverridable(Boolean value) {
        this.includeInRatingOverridable = value;
    }

    /**
     * Gets the value of the activityType property.
     * 
     * @return
     *     possible object is
     *     {@link AAAActivityType }
     *     
     */
    public AAAActivityType getActivityType() {
        return activityType;
    }

    /**
     * Sets the value of the activityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAActivityType }
     *     
     */
    public void setActivityType(AAAActivityType value) {
        this.activityType = value;
    }

    /**
     * Gets the value of the incidentDescription property.
     * 
     * @return
     *     possible object is
     *     {@link ActivityDescription }
     *     
     */
    public ActivityDescription getIncidentDescription() {
        return incidentDescription;
    }

    /**
     * Sets the value of the incidentDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivityDescription }
     *     
     */
    public void setIncidentDescription(ActivityDescription value) {
        this.incidentDescription = value;
    }

    /**
     * Gets the value of the removable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemovable() {
        return removable;
    }

    /**
     * Sets the value of the removable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemovable(Boolean value) {
        this.removable = value;
    }

    /**
     * Gets the value of the includeToRatingInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeToRatingInd() {
        return includeToRatingInd;
    }

    /**
     * Sets the value of the includeToRatingInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeToRatingInd(Boolean value) {
        this.includeToRatingInd = value;
    }

    /**
     * Gets the value of the lossPaymentAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLossPaymentAmt() {
        return lossPaymentAmt;
    }

    /**
     * Sets the value of the lossPaymentAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLossPaymentAmt(BigDecimal value) {
        this.lossPaymentAmt = value;
    }

    /**
     * Gets the value of the numAccidentPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumAccidentPoints() {
        return numAccidentPoints;
    }

    /**
     * Sets the value of the numAccidentPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumAccidentPoints(BigDecimal value) {
        this.numAccidentPoints = value;
    }

    /**
     * Gets the value of the numConvictionPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumConvictionPoints() {
        return numConvictionPoints;
    }

    /**
     * Sets the value of the numConvictionPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumConvictionPoints(BigDecimal value) {
        this.numConvictionPoints = value;
    }

    /**
     * Gets the value of the svcDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvcDescription() {
        return svcDescription;
    }

    /**
     * Sets the value of the svcDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvcDescription(String value) {
        this.svcDescription = value;
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
