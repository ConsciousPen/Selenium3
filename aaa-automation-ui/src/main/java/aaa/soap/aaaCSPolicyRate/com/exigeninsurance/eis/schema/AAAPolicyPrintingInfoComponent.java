
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.PrintingLanguageCd;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.SuppressPrint;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAAPolicyPrintingInfoComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAPolicyPrintingInfoComponent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="languageCd" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}PrintingLanguageCd" minOccurs="0"/&gt;
 *         &lt;element name="suppressPrint" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}SuppressPrint" minOccurs="0"/&gt;
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
@XmlType(name = "AAAPolicyPrintingInfoComponent", propOrder = {
    "languageCd",
    "suppressPrint"
})
public class AAAPolicyPrintingInfoComponent {

    @XmlSchemaType(name = "string")
    protected PrintingLanguageCd languageCd;
    @XmlSchemaType(name = "string")
    protected SuppressPrint suppressPrint;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the languageCd property.
     * 
     * @return
     *     possible object is
     *     {@link PrintingLanguageCd }
     *     
     */
    public PrintingLanguageCd getLanguageCd() {
        return languageCd;
    }

    /**
     * Sets the value of the languageCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrintingLanguageCd }
     *     
     */
    public void setLanguageCd(PrintingLanguageCd value) {
        this.languageCd = value;
    }

    /**
     * Gets the value of the suppressPrint property.
     * 
     * @return
     *     possible object is
     *     {@link SuppressPrint }
     *     
     */
    public SuppressPrint getSuppressPrint() {
        return suppressPrint;
    }

    /**
     * Sets the value of the suppressPrint property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuppressPrint }
     *     
     */
    public void setSuppressPrint(SuppressPrint value) {
        this.suppressPrint = value;
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
