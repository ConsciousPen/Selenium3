
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for ExistingDamageEndorsement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExistingDamageEndorsement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="formText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="formCd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "ExistingDamageEndorsement", propOrder = {
    "formText",
    "formCd"
})
public class ExistingDamageEndorsement {

    @XmlElement(defaultValue = "Existing Damage Endorsement")
    protected String formText;
    @XmlElement(defaultValue = "EXDMGE")
    protected String formCd;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the formText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormText() {
        return formText;
    }

    /**
     * Sets the value of the formText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormText(String value) {
        this.formText = value;
    }

    /**
     * Gets the value of the formCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormCd() {
        return formCd;
    }

    /**
     * Sets the value of the formCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormCd(String value) {
        this.formCd = value;
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
