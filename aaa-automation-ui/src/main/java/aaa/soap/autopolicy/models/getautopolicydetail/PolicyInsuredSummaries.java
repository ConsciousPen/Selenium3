
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyInsuredSummaries complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInsuredSummaries">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="namedInsuredSummary" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}NamedInsuredSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="policyInsuredSummariesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInsuredSummaries", propOrder = {
    "namedInsuredSummary",
    "policyInsuredSummariesExtension"
})
public class PolicyInsuredSummaries {

    @XmlElement(nillable = true)
    protected List<NamedInsuredSummary> namedInsuredSummary;
    @XmlElement(nillable = true)
    protected List<Object> policyInsuredSummariesExtension;

    /**
     * Gets the value of the namedInsuredSummary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the namedInsuredSummary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamedInsuredSummary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedInsuredSummary }
     * 
     * 
     */
    public List<NamedInsuredSummary> getNamedInsuredSummary() {
        if (namedInsuredSummary == null) {
            namedInsuredSummary = new ArrayList<NamedInsuredSummary>();
        }
        return this.namedInsuredSummary;
    }

    /**
     * Gets the value of the policyInsuredSummariesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyInsuredSummariesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyInsuredSummariesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicyInsuredSummariesExtension() {
        if (policyInsuredSummariesExtension == null) {
            policyInsuredSummariesExtension = new ArrayList<Object>();
        }
        return this.policyInsuredSummariesExtension;
    }

}
