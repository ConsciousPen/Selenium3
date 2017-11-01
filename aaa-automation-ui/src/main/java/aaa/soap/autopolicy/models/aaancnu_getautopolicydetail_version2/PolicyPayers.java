
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyPayers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyPayers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payer" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PayerSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="policyPayersExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyPayers", propOrder = {
    "payer",
    "policyPayersExtension"
})
public class PolicyPayers {

    @XmlElement(nillable = true)
    protected List<PayerSummary> payer;
    @XmlElement(nillable = true)
    protected List<Object> policyPayersExtension;

    /**
     * Gets the value of the payer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PayerSummary }
     * 
     * 
     */
    public List<PayerSummary> getPayer() {
        if (payer == null) {
            payer = new ArrayList<PayerSummary>();
        }
        return this.payer;
    }

    /**
     * Gets the value of the policyPayersExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyPayersExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyPayersExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicyPayersExtension() {
        if (policyPayersExtension == null) {
            policyPayersExtension = new ArrayList<Object>();
        }
        return this.policyPayersExtension;
    }

}
