
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup.AAAActivitySource;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup.AAANonRatingReason;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup.LiabilityCode;
import aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAADrivingRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAADrivingRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accidentViolationDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="activitySource" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAAActivitySource" minOccurs="0"/>
 *         &lt;element name="activityType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="convictionDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="incidentDescription" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}ActivityDescription" minOccurs="0"/>
 *         &lt;element name="includeToRatingInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="liabilityCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}LiabilityCode" minOccurs="0"/>
 *         &lt;element name="lossPaymentAmt" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="numAccidentPoints" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="numConvictionPoints" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="otherReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="penaltyQuestion1" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="penaltyQuestion2" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="penaltyQuestion3" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="penaltyQuestion4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reason" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}AAANonRatingReason" minOccurs="0"/>
 *         &lt;element name="reinstatementDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="suspensionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="svcDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numViolationPoints" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "AAADrivingRecord", propOrder = {
    "accidentViolationDt",
    "activitySource",
    "activityType",
    "convictionDt",
    "incidentDescription",
    "includeToRatingInd",
    "liabilityCd",
    "lossPaymentAmt",
    "numAccidentPoints",
    "numConvictionPoints",
    "otherReason",
    "penaltyQuestion1",
    "penaltyQuestion2",
    "penaltyQuestion3",
    "penaltyQuestion4",
    "reason",
    "reinstatementDate",
    "suspensionDate",
    "svcDescription",
    "numViolationPoints"
})
public class AAADrivingRecord {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar accidentViolationDt;
    @XmlSchemaType(name = "string")
    protected AAAActivitySource activitySource;
    protected String activityType;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar convictionDt;
    protected String incidentDescription;
    @XmlElement(defaultValue = "true")
    protected boolean includeToRatingInd;
    @XmlSchemaType(name = "string")
    protected LiabilityCode liabilityCd;
    protected BigDecimal lossPaymentAmt;
    protected BigDecimal numAccidentPoints;
    protected BigDecimal numConvictionPoints;
    protected String otherReason;
    protected Boolean penaltyQuestion1;
    protected Boolean penaltyQuestion2;
    protected Boolean penaltyQuestion3;
    protected String penaltyQuestion4;
    @XmlSchemaType(name = "string")
    protected AAANonRatingReason reason;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reinstatementDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar suspensionDate;
    protected String svcDescription;
    protected String numViolationPoints;
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
     * Gets the value of the activityType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * Sets the value of the activityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityType(String value) {
        this.activityType = value;
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
     * Gets the value of the incidentDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncidentDescription() {
        return incidentDescription;
    }

    /**
     * Sets the value of the incidentDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncidentDescription(String value) {
        this.incidentDescription = value;
    }

    /**
     * Gets the value of the includeToRatingInd property.
     * 
     */
    public boolean isIncludeToRatingInd() {
        return includeToRatingInd;
    }

    /**
     * Sets the value of the includeToRatingInd property.
     * 
     */
    public void setIncludeToRatingInd(boolean value) {
        this.includeToRatingInd = value;
    }

    /**
     * Gets the value of the liabilityCd property.
     * 
     * @return
     *     possible object is
     *     {@link LiabilityCode }
     *     
     */
    public LiabilityCode getLiabilityCd() {
        return liabilityCd;
    }

    /**
     * Sets the value of the liabilityCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiabilityCode }
     *     
     */
    public void setLiabilityCd(LiabilityCode value) {
        this.liabilityCd = value;
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
     * Gets the value of the penaltyQuestion1 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPenaltyQuestion1() {
        return penaltyQuestion1;
    }

    /**
     * Sets the value of the penaltyQuestion1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPenaltyQuestion1(Boolean value) {
        this.penaltyQuestion1 = value;
    }

    /**
     * Gets the value of the penaltyQuestion2 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPenaltyQuestion2() {
        return penaltyQuestion2;
    }

    /**
     * Sets the value of the penaltyQuestion2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPenaltyQuestion2(Boolean value) {
        this.penaltyQuestion2 = value;
    }

    /**
     * Gets the value of the penaltyQuestion3 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPenaltyQuestion3() {
        return penaltyQuestion3;
    }

    /**
     * Sets the value of the penaltyQuestion3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPenaltyQuestion3(Boolean value) {
        this.penaltyQuestion3 = value;
    }

    /**
     * Gets the value of the penaltyQuestion4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPenaltyQuestion4() {
        return penaltyQuestion4;
    }

    /**
     * Sets the value of the penaltyQuestion4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPenaltyQuestion4(String value) {
        this.penaltyQuestion4 = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link AAANonRatingReason }
     *     
     */
    public AAANonRatingReason getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAANonRatingReason }
     *     
     */
    public void setReason(AAANonRatingReason value) {
        this.reason = value;
    }

    /**
     * Gets the value of the reinstatementDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReinstatementDate() {
        return reinstatementDate;
    }

    /**
     * Sets the value of the reinstatementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReinstatementDate(XMLGregorianCalendar value) {
        this.reinstatementDate = value;
    }

    /**
     * Gets the value of the suspensionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSuspensionDate() {
        return suspensionDate;
    }

    /**
     * Sets the value of the suspensionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSuspensionDate(XMLGregorianCalendar value) {
        this.suspensionDate = value;
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
     * Gets the value of the numViolationPoints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumViolationPoints() {
        return numViolationPoints;
    }

    /**
     * Sets the value of the numViolationPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumViolationPoints(String value) {
        this.numViolationPoints = value;
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
