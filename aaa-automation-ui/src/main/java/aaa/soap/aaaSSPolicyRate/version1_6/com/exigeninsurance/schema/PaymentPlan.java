
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * <p>Java class for PaymentPlan complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentPlan"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paymentPlanCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="premium" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="minDownPayment" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="installmentAmountWithOutFees" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="remainingInstallments" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="restrictedInd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentPlan", propOrder = {
    "paymentPlanCd",
    "premium",
    "minDownPayment",
    "installmentAmountWithOutFees",
    "remainingInstallments",
    "restrictedInd"
})
public class PaymentPlan {

    protected String paymentPlanCd;
    protected BigDecimal premium;
    protected BigDecimal minDownPayment;
    protected BigDecimal installmentAmountWithOutFees;
    protected BigInteger remainingInstallments;
    protected Boolean restrictedInd;

    /**
     * Gets the value of the paymentPlanCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentPlanCd() {
        return paymentPlanCd;
    }

    /**
     * Sets the value of the paymentPlanCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentPlanCd(String value) {
        this.paymentPlanCd = value;
    }

    /**
     * Gets the value of the premium property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPremium() {
        return premium;
    }

    /**
     * Sets the value of the premium property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPremium(BigDecimal value) {
        this.premium = value;
    }

    /**
     * Gets the value of the minDownPayment property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMinDownPayment() {
        return minDownPayment;
    }

    /**
     * Sets the value of the minDownPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMinDownPayment(BigDecimal value) {
        this.minDownPayment = value;
    }

    /**
     * Gets the value of the installmentAmountWithOutFees property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInstallmentAmountWithOutFees() {
        return installmentAmountWithOutFees;
    }

    /**
     * Sets the value of the installmentAmountWithOutFees property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInstallmentAmountWithOutFees(BigDecimal value) {
        this.installmentAmountWithOutFees = value;
    }

    /**
     * Gets the value of the remainingInstallments property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRemainingInstallments() {
        return remainingInstallments;
    }

    /**
     * Sets the value of the remainingInstallments property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRemainingInstallments(BigInteger value) {
        this.remainingInstallments = value;
    }

    /**
     * Gets the value of the restrictedInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRestrictedInd() {
        return restrictedInd;
    }

    /**
     * Sets the value of the restrictedInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRestrictedInd(Boolean value) {
        this.restrictedInd = value;
    }

}
