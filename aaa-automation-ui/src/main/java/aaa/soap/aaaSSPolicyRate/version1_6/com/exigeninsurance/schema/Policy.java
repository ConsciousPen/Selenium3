
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.PaymentDetails;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.UserNote;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;




/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAACSAAutoPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAASSAutoPolicy"/&gt;
 *         &lt;element name="PaymentDetails" type="{http://www.exigeninsurance.com/data/EIS/1.0}PaymentDetails" minOccurs="0"/&gt;
 *         &lt;element name="UserNote" type="{http://www.exigeninsurance.com/data/EIS/1.0}UserNote" maxOccurs="999" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "aaacsaAutoPolicy",
    "paymentDetails",
    "userNote"
})
@XmlRootElement(name = "Policy")
public class Policy {

    @XmlElement(name = "AAACSAAutoPolicy", required = true)
    protected AAASSAutoPolicy aaacsaAutoPolicy;
    @XmlElement(name = "PaymentDetails")
    protected PaymentDetails paymentDetails;
    @XmlElement(name = "UserNote")
    protected List<UserNote> userNote;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "version")
    protected BigDecimal version;

    /**
     * Gets the value of the aaacsaAutoPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link AAASSAutoPolicy }
     *     
     */
    public AAASSAutoPolicy getAAACSAAutoPolicy() {
        return aaacsaAutoPolicy;
    }

    /**
     * Sets the value of the aaacsaAutoPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAASSAutoPolicy }
     *     
     */
    public void setAAACSAAutoPolicy(AAASSAutoPolicy value) {
        this.aaacsaAutoPolicy = value;
    }

    /**
     * Gets the value of the paymentDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentDetails }
     *     
     */
    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * Sets the value of the paymentDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentDetails }
     *     
     */
    public void setPaymentDetails(PaymentDetails value) {
        this.paymentDetails = value;
    }

    /**
     * Gets the value of the userNote property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userNote property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserNote }
     * 
     * 
     */
    public List<UserNote> getUserNote() {
        if (userNote == null) {
            userNote = new ArrayList<UserNote>();
        }
        return this.userNote;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

}
