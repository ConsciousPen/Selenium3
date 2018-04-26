
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Choice_AA43CAEndorsementForm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Choice_AA43CAEndorsementForm"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PremiumEntry" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "Choice_AA43CAEndorsementForm", propOrder = {
    "premiumEntry"
})
public class ChoiceAA43CAEndorsementForm {

    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

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
