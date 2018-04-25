
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.CoverageCode;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAAVehicleCoverageVehLimitUMPD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAVehicleCoverageVehLimitUMPD"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="commissionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="coverageCd" type="{http://www.exigeninsurance.com/data/EIS/1.0}CoverageCode" minOccurs="0"/&gt;
 *         &lt;element name="coverLevelCd" type="{http://www.exigeninsurance.com/data/EIS/1.0}CoverageCode" minOccurs="0"/&gt;
 *         &lt;element name="deductibleAmount" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}UMPDDED" minOccurs="0"/&gt;
 *         &lt;element name="deductibleAppliesToCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="deductibleTypeCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="effectiveDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="expirationDt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="limitAmount" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}UMPD" minOccurs="0"/&gt;
 *         &lt;element name="limitAppliesToCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="limitValuationCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}PremiumEntry" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
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
@XmlType(name = "AAAVehicleCoverageVehLimitUMPD", propOrder = {
    "commissionAmount",
    "coverageCd",
    "coverLevelCd",
    "deductibleAmount",
    "deductibleAppliesToCd",
    "deductibleTypeCd",
    "effectiveDt",
    "expirationDt",
    "limitAmount",
    "limitAppliesToCd",
    "limitValuationCd",
    "premiumEntry",
    "seqNo"
})
public class AAAVehicleCoverageVehLimitUMPD {

    protected BigDecimal commissionAmount;
    @XmlSchemaType(name = "string")
    protected CoverageCode coverageCd;
    @XmlSchemaType(name = "string")
    protected CoverageCode coverLevelCd;
    protected BigDecimal deductibleAmount;
    protected String deductibleAppliesToCd;
    protected String deductibleTypeCd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveDt;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expirationDt;
    protected BigDecimal limitAmount;
    protected String limitAppliesToCd;
    protected String limitValuationCd;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    protected BigDecimal seqNo;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the commissionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    /**
     * Sets the value of the commissionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCommissionAmount(BigDecimal value) {
        this.commissionAmount = value;
    }

    /**
     * Gets the value of the coverageCd property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageCode }
     *     
     */
    public CoverageCode getCoverageCd() {
        return coverageCd;
    }

    /**
     * Sets the value of the coverageCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageCode }
     *     
     */
    public void setCoverageCd(CoverageCode value) {
        this.coverageCd = value;
    }

    /**
     * Gets the value of the coverLevelCd property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageCode }
     *     
     */
    public CoverageCode getCoverLevelCd() {
        return coverLevelCd;
    }

    /**
     * Sets the value of the coverLevelCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageCode }
     *     
     */
    public void setCoverLevelCd(CoverageCode value) {
        this.coverLevelCd = value;
    }

    /**
     * Gets the value of the deductibleAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDeductibleAmount() {
        return deductibleAmount;
    }

    /**
     * Sets the value of the deductibleAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDeductibleAmount(BigDecimal value) {
        this.deductibleAmount = value;
    }

    /**
     * Gets the value of the deductibleAppliesToCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeductibleAppliesToCd() {
        return deductibleAppliesToCd;
    }

    /**
     * Sets the value of the deductibleAppliesToCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeductibleAppliesToCd(String value) {
        this.deductibleAppliesToCd = value;
    }

    /**
     * Gets the value of the deductibleTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeductibleTypeCd() {
        return deductibleTypeCd;
    }

    /**
     * Sets the value of the deductibleTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeductibleTypeCd(String value) {
        this.deductibleTypeCd = value;
    }

    /**
     * Gets the value of the effectiveDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveDt() {
        return effectiveDt;
    }

    /**
     * Sets the value of the effectiveDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveDt(XMLGregorianCalendar value) {
        this.effectiveDt = value;
    }

    /**
     * Gets the value of the expirationDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpirationDt() {
        return expirationDt;
    }

    /**
     * Sets the value of the expirationDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpirationDt(XMLGregorianCalendar value) {
        this.expirationDt = value;
    }

    /**
     * Gets the value of the limitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    /**
     * Sets the value of the limitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLimitAmount(BigDecimal value) {
        this.limitAmount = value;
    }

    /**
     * Gets the value of the limitAppliesToCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLimitAppliesToCd() {
        return limitAppliesToCd;
    }

    /**
     * Sets the value of the limitAppliesToCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLimitAppliesToCd(String value) {
        this.limitAppliesToCd = value;
    }

    /**
     * Gets the value of the limitValuationCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLimitValuationCd() {
        return limitValuationCd;
    }

    /**
     * Sets the value of the limitValuationCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLimitValuationCd(String value) {
        this.limitValuationCd = value;
    }

    /**
     * Gets the value of the premiumEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiumEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiumEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiumEntry }
     * 
     * 
     */
    public List<PremiumEntry> getPremiumEntry() {
        if (premiumEntry == null) {
            premiumEntry = new ArrayList<PremiumEntry>();
        }
        return this.premiumEntry;
    }

    /**
     * Gets the value of the seqNo property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSeqNo() {
        return seqNo;
    }

    /**
     * Sets the value of the seqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSeqNo(BigDecimal value) {
        this.seqNo = value;
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
