
package aaa.soap.getAutoPolicyDetails.aaancnu_common_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleResults complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ruleResult" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResult" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ruleResultsExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleResults", propOrder = {
    "ruleResult",
    "ruleResultsExtension"
})
public class RuleResults {

    @XmlElement(nillable = true)
    protected List<RuleResult> ruleResult;
    @XmlElement(nillable = true)
    protected List<Object> ruleResultsExtension;

    /**
     * Gets the value of the ruleResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleResult }
     * 
     * 
     */
    public List<RuleResult> getRuleResult() {
        if (ruleResult == null) {
            ruleResult = new ArrayList<RuleResult>();
        }
        return this.ruleResult;
    }

    /**
     * Gets the value of the ruleResultsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResultsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResultsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRuleResultsExtension() {
        if (ruleResultsExtension == null) {
            ruleResultsExtension = new ArrayList<Object>();
        }
        return this.ruleResultsExtension;
    }

}
