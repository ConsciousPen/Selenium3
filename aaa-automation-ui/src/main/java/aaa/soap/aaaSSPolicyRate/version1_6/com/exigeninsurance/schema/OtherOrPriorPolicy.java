
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.OtherProductCode;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for OtherOrPriorPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OtherOrPriorPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="insurerCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="insurerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="productCd" type="{http://www.exigeninsurance.com/data/EIS/1.0}OtherProductCode"/&gt;
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OtherOrPriorPolicy", propOrder = {
    "insurerCd",
    "insurerName",
    "productCd",
    "policyNumber"
})
public class OtherOrPriorPolicy {

    protected String insurerCd;
    protected String insurerName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected OtherProductCode productCd;
    protected String policyNumber;
    @XmlAttribute(name = "oid")
    protected String oid;

    /**
     * Gets the value of the insurerCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerCd() {
        return insurerCd;
    }

    /**
     * Sets the value of the insurerCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerCd(String value) {
        this.insurerCd = value;
    }

    /**
     * Gets the value of the insurerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerName() {
        return insurerName;
    }

    /**
     * Sets the value of the insurerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerName(String value) {
        this.insurerName = value;
    }

    /**
     * Gets the value of the productCd property.
     * 
     * @return
     *     possible object is
     *     {@link OtherProductCode }
     *     
     */
    public OtherProductCode getProductCd() {
        return productCd;
    }

    /**
     * Sets the value of the productCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherProductCode }
     *     
     */
    public void setProductCd(OtherProductCode value) {
        this.productCd = value;
    }

    /**
     * Gets the value of the policyNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Sets the value of the policyNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyNumber(String value) {
        this.policyNumber = value;
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

}
