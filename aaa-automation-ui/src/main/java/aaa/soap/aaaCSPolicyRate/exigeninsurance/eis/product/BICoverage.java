
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.ComponentState;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for BICoverage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BICoverage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="combinedLimitAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="coverageCd" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}CoverageCode"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
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
@XmlType(name = "BICoverage", propOrder = {
    "combinedLimitAmount",
    "coverageCd",
    "seqNo"
})
public class BICoverage {

    @XmlElement(required = true)
    protected String combinedLimitAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CoverageCode coverageCd;
    protected BigDecimal seqNo;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the combinedLimitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCombinedLimitAmount() {
        return combinedLimitAmount;
    }

    /**
     * Sets the value of the combinedLimitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCombinedLimitAmount(String value) {
        this.combinedLimitAmount = value;
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
