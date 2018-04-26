
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.CommissionType;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for Commissions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Commissions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="commissionPct" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="commissionTypeCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}CommissionType"/&gt;
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
@XmlType(name = "Commissions", propOrder = {
    "commissionPct",
    "commissionTypeCd"
})
public class Commissions {

    protected BigDecimal commissionPct;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CommissionType commissionTypeCd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the commissionPct property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCommissionPct() {
        return commissionPct;
    }

    /**
     * Sets the value of the commissionPct property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCommissionPct(BigDecimal value) {
        this.commissionPct = value;
    }

    /**
     * Gets the value of the commissionTypeCd property.
     * 
     * @return
     *     possible object is
     *     {@link CommissionType }
     *     
     */
    public CommissionType getCommissionTypeCd() {
        return commissionTypeCd;
    }

    /**
     * Sets the value of the commissionTypeCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommissionType }
     *     
     */
    public void setCommissionTypeCd(CommissionType value) {
        this.commissionTypeCd = value;
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
