
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferencedPolicies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferencedPolicies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policy" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyHeader" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="referencedPoliciesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferencedPolicies", propOrder = {
    "policy",
    "referencedPoliciesExtension"
})
public class ReferencedPolicies {

    @XmlElement(nillable = true)
    protected List<PolicyHeader> policy;
    @XmlElement(nillable = true)
    protected List<Object> referencedPoliciesExtension;

    /**
     * Gets the value of the policy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolicyHeader }
     * 
     * 
     */
    public List<PolicyHeader> getPolicy() {
        if (policy == null) {
            policy = new ArrayList<PolicyHeader>();
        }
        return this.policy;
    }

    /**
     * Gets the value of the referencedPoliciesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referencedPoliciesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencedPoliciesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getReferencedPoliciesExtension() {
        if (referencedPoliciesExtension == null) {
            referencedPoliciesExtension = new ArrayList<Object>();
        }
        return this.referencedPoliciesExtension;
    }

}
