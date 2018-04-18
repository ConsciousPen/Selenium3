
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.DefensiveDriverIndLookup;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.MostRecentGPA;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAADriverRatingInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAADriverRatingInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="drvrRejLimRightToSue" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="certificateNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defensiveDrCrsCertifNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defensiveDriverDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="defensiveDriverIndLookup" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}defensiveDriverIndLookup" minOccurs="0"/&gt;
 *         &lt;element name="distantStudentInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="goodDriverStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="matureDrCrsComplDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="matureDriverInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="newDriverInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="recentBPA" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}MostRecentGPA" minOccurs="0"/&gt;
 *         &lt;element name="smartDrCrsCertifNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="smartDrCrsComplDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="smartDrCrsInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="totalAccidentPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="totalConvictionPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="bgpaInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="totalPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="yaf" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="unverifiableDrRecSurchargeInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="foreignLicenseSurchargeInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="travelinkDiscount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="refresherCourseInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
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
@XmlType(name = "AAADriverRatingInfo", propOrder = {
    "drvrRejLimRightToSue",
    "certificateNum",
    "defensiveDrCrsCertifNum",
    "defensiveDriverDt",
    "defensiveDriverIndLookup",
    "distantStudentInd",
    "goodDriverStatus",
    "matureDrCrsComplDt",
    "matureDriverInd",
    "newDriverInd",
    "recentBPA",
    "smartDrCrsCertifNum",
    "smartDrCrsComplDt",
    "smartDrCrsInd",
    "totalAccidentPointsNum",
    "totalConvictionPointsNum",
    "bgpaInd",
    "totalPointsNum",
    "yaf",
    "unverifiableDrRecSurchargeInd",
    "foreignLicenseSurchargeInd",
    "travelinkDiscount",
    "refresherCourseInd"
})
public class AAADriverRatingInfo {

    protected Boolean drvrRejLimRightToSue;
    protected String certificateNum;
    protected String defensiveDrCrsCertifNum;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar defensiveDriverDt;
    @XmlSchemaType(name = "string")
    protected DefensiveDriverIndLookup defensiveDriverIndLookup;
    protected Boolean distantStudentInd;
    protected Boolean goodDriverStatus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar matureDrCrsComplDt;
    protected Boolean matureDriverInd;
    protected Boolean newDriverInd;
    @XmlSchemaType(name = "string")
    protected MostRecentGPA recentBPA;
    protected String smartDrCrsCertifNum;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar smartDrCrsComplDt;
    protected Boolean smartDrCrsInd;
    protected BigDecimal totalAccidentPointsNum;
    protected BigDecimal totalConvictionPointsNum;
    protected Boolean bgpaInd;
    protected BigDecimal totalPointsNum;
    protected BigDecimal yaf;
    protected Boolean unverifiableDrRecSurchargeInd;
    protected Boolean foreignLicenseSurchargeInd;
    protected Boolean travelinkDiscount;
    protected Boolean refresherCourseInd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the drvrRejLimRightToSue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDrvrRejLimRightToSue() {
        return drvrRejLimRightToSue;
    }

    /**
     * Sets the value of the drvrRejLimRightToSue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDrvrRejLimRightToSue(Boolean value) {
        this.drvrRejLimRightToSue = value;
    }

    /**
     * Gets the value of the certificateNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificateNum() {
        return certificateNum;
    }

    /**
     * Sets the value of the certificateNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateNum(String value) {
        this.certificateNum = value;
    }

    /**
     * Gets the value of the defensiveDrCrsCertifNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefensiveDrCrsCertifNum() {
        return defensiveDrCrsCertifNum;
    }

    /**
     * Sets the value of the defensiveDrCrsCertifNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefensiveDrCrsCertifNum(String value) {
        this.defensiveDrCrsCertifNum = value;
    }

    /**
     * Gets the value of the defensiveDriverDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDefensiveDriverDt() {
        return defensiveDriverDt;
    }

    /**
     * Sets the value of the defensiveDriverDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDefensiveDriverDt(XMLGregorianCalendar value) {
        this.defensiveDriverDt = value;
    }

    /**
     * Gets the value of the defensiveDriverIndLookup property.
     * 
     * @return
     *     possible object is
     *     {@link DefensiveDriverIndLookup }
     *     
     */
    public DefensiveDriverIndLookup getDefensiveDriverIndLookup() {
        return defensiveDriverIndLookup;
    }

    /**
     * Sets the value of the defensiveDriverIndLookup property.
     * 
     * @param value
     *     allowed object is
     *     {@link DefensiveDriverIndLookup }
     *     
     */
    public void setDefensiveDriverIndLookup(DefensiveDriverIndLookup value) {
        this.defensiveDriverIndLookup = value;
    }

    /**
     * Gets the value of the distantStudentInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDistantStudentInd() {
        return distantStudentInd;
    }

    /**
     * Sets the value of the distantStudentInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDistantStudentInd(Boolean value) {
        this.distantStudentInd = value;
    }

    /**
     * Gets the value of the goodDriverStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGoodDriverStatus() {
        return goodDriverStatus;
    }

    /**
     * Sets the value of the goodDriverStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGoodDriverStatus(Boolean value) {
        this.goodDriverStatus = value;
    }

    /**
     * Gets the value of the matureDrCrsComplDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMatureDrCrsComplDt() {
        return matureDrCrsComplDt;
    }

    /**
     * Sets the value of the matureDrCrsComplDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMatureDrCrsComplDt(XMLGregorianCalendar value) {
        this.matureDrCrsComplDt = value;
    }

    /**
     * Gets the value of the matureDriverInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMatureDriverInd() {
        return matureDriverInd;
    }

    /**
     * Sets the value of the matureDriverInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMatureDriverInd(Boolean value) {
        this.matureDriverInd = value;
    }

    /**
     * Gets the value of the newDriverInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewDriverInd() {
        return newDriverInd;
    }

    /**
     * Sets the value of the newDriverInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewDriverInd(Boolean value) {
        this.newDriverInd = value;
    }

    /**
     * Gets the value of the recentBPA property.
     * 
     * @return
     *     possible object is
     *     {@link MostRecentGPA }
     *     
     */
    public MostRecentGPA getRecentBPA() {
        return recentBPA;
    }

    /**
     * Sets the value of the recentBPA property.
     * 
     * @param value
     *     allowed object is
     *     {@link MostRecentGPA }
     *     
     */
    public void setRecentBPA(MostRecentGPA value) {
        this.recentBPA = value;
    }

    /**
     * Gets the value of the smartDrCrsCertifNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmartDrCrsCertifNum() {
        return smartDrCrsCertifNum;
    }

    /**
     * Sets the value of the smartDrCrsCertifNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmartDrCrsCertifNum(String value) {
        this.smartDrCrsCertifNum = value;
    }

    /**
     * Gets the value of the smartDrCrsComplDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmartDrCrsComplDt() {
        return smartDrCrsComplDt;
    }

    /**
     * Sets the value of the smartDrCrsComplDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmartDrCrsComplDt(XMLGregorianCalendar value) {
        this.smartDrCrsComplDt = value;
    }

    /**
     * Gets the value of the smartDrCrsInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmartDrCrsInd() {
        return smartDrCrsInd;
    }

    /**
     * Sets the value of the smartDrCrsInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmartDrCrsInd(Boolean value) {
        this.smartDrCrsInd = value;
    }

    /**
     * Gets the value of the totalAccidentPointsNum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalAccidentPointsNum() {
        return totalAccidentPointsNum;
    }

    /**
     * Sets the value of the totalAccidentPointsNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalAccidentPointsNum(BigDecimal value) {
        this.totalAccidentPointsNum = value;
    }

    /**
     * Gets the value of the totalConvictionPointsNum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalConvictionPointsNum() {
        return totalConvictionPointsNum;
    }

    /**
     * Sets the value of the totalConvictionPointsNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalConvictionPointsNum(BigDecimal value) {
        this.totalConvictionPointsNum = value;
    }

    /**
     * Gets the value of the bgpaInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBgpaInd() {
        return bgpaInd;
    }

    /**
     * Sets the value of the bgpaInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBgpaInd(Boolean value) {
        this.bgpaInd = value;
    }

    /**
     * Gets the value of the totalPointsNum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalPointsNum() {
        return totalPointsNum;
    }

    /**
     * Sets the value of the totalPointsNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalPointsNum(BigDecimal value) {
        this.totalPointsNum = value;
    }

    /**
     * Gets the value of the yaf property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getYaf() {
        return yaf;
    }

    /**
     * Sets the value of the yaf property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setYaf(BigDecimal value) {
        this.yaf = value;
    }

    /**
     * Gets the value of the unverifiableDrRecSurchargeInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnverifiableDrRecSurchargeInd() {
        return unverifiableDrRecSurchargeInd;
    }

    /**
     * Sets the value of the unverifiableDrRecSurchargeInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnverifiableDrRecSurchargeInd(Boolean value) {
        this.unverifiableDrRecSurchargeInd = value;
    }

    /**
     * Gets the value of the foreignLicenseSurchargeInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isForeignLicenseSurchargeInd() {
        return foreignLicenseSurchargeInd;
    }

    /**
     * Sets the value of the foreignLicenseSurchargeInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setForeignLicenseSurchargeInd(Boolean value) {
        this.foreignLicenseSurchargeInd = value;
    }

    /**
     * Gets the value of the travelinkDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTravelinkDiscount() {
        return travelinkDiscount;
    }

    /**
     * Sets the value of the travelinkDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTravelinkDiscount(Boolean value) {
        this.travelinkDiscount = value;
    }

    /**
     * Gets the value of the refresherCourseInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRefresherCourseInd() {
        return refresherCourseInd;
    }

    /**
     * Sets the value of the refresherCourseInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRefresherCourseInd(Boolean value) {
        this.refresherCourseInd = value;
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
