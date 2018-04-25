
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.PremiumCode;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for PremiumEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PremiumEntry"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="actualAmt" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="annualAmt" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="changeAmt" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="factor" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="premiumCd" type="{http://www.exigeninsurance.com/data/EIS/1.0}PremiumCode" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PremiumEntry", propOrder = {
    "actualAmt",
    "annualAmt",
    "changeAmt",
    "factor"
})
public class PremiumEntry {

    @XmlElement(required = true)
    protected BigDecimal actualAmt;
    @XmlElement(required = true)
    protected BigDecimal annualAmt;
    @XmlElement(required = true)
    protected BigDecimal changeAmt;
    @XmlElementRef(name = "factor", namespace = "http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> factor;
    @XmlAttribute(name = "premiumCd")
    protected PremiumCode premiumCd;

    /**
     * Gets the value of the actualAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getActualAmt() {
        return actualAmt;
    }

    /**
     * Sets the value of the actualAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setActualAmt(BigDecimal value) {
        this.actualAmt = value;
    }

    /**
     * Gets the value of the annualAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAnnualAmt() {
        return annualAmt;
    }

    /**
     * Sets the value of the annualAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAnnualAmt(BigDecimal value) {
        this.annualAmt = value;
    }

    /**
     * Gets the value of the changeAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getChangeAmt() {
        return changeAmt;
    }

    /**
     * Sets the value of the changeAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setChangeAmt(BigDecimal value) {
        this.changeAmt = value;
    }

    /**
     * Gets the value of the factor property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getFactor() {
        return factor;
    }

    /**
     * Sets the value of the factor property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setFactor(JAXBElement<BigDecimal> value) {
        this.factor = value;
    }

    /**
     * Gets the value of the premiumCd property.
     * 
     * @return
     *     possible object is
     *     {@link PremiumCode }
     *     
     */
    public PremiumCode getPremiumCd() {
        return premiumCd;
    }

    /**
     * Sets the value of the premiumCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PremiumCode }
     *     
     */
    public void setPremiumCd(PremiumCode value) {
        this.premiumCd = value;
    }

}
