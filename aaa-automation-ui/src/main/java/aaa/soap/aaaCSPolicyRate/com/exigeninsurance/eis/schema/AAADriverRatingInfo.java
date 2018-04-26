
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.MostRecentGPA;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

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
 *         &lt;element name="certificateNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="goodDriverStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="matureDrCrsComplDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="matureDriverInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="newDriverInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="smokerInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="recentBPA" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}MostRecentGPA" minOccurs="0"/&gt;
 *         &lt;element name="totalAccidentPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="totalConvictionPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="totalPointsNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="yaf" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
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
    "certificateNum",
    "goodDriverStatus",
    "matureDrCrsComplDt",
    "matureDriverInd",
    "newDriverInd",
    "smokerInd",
    "recentBPA",
    "totalAccidentPointsNum",
    "totalConvictionPointsNum",
    "totalPointsNum",
    "yaf"
})
public class AAADriverRatingInfo {

    protected String certificateNum;
    protected Boolean goodDriverStatus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar matureDrCrsComplDt;
    protected Boolean matureDriverInd;
    protected Boolean newDriverInd;
    protected Boolean smokerInd;
    @XmlSchemaType(name = "string")
    protected MostRecentGPA recentBPA;
    protected BigDecimal totalAccidentPointsNum;
    protected BigDecimal totalConvictionPointsNum;
    protected BigDecimal totalPointsNum;
    protected BigDecimal yaf;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

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
     * Gets the value of the smokerInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmokerInd() {
        return smokerInd;
    }

    /**
     * Sets the value of the smokerInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmokerInd(Boolean value) {
        this.smokerInd = value;
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
