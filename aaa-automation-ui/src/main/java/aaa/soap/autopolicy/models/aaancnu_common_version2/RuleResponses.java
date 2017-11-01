
package aaa.soap.autopolicy.models.aaancnu_common_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleResponses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleResponses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ruleResponse" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResponse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ruleResponsesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleResponses", propOrder = {
    "ruleResponse",
    "ruleResponsesExtension"
})
public class RuleResponses {

    @XmlElement(nillable = true)
    protected List<RuleResponse> ruleResponse;
    @XmlElement(nillable = true)
    protected List<Object> ruleResponsesExtension;

    /**
     * Gets the value of the ruleResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleResponse }
     * 
     * 
     */
    public List<RuleResponse> getRuleResponse() {
        if (ruleResponse == null) {
            ruleResponse = new ArrayList<RuleResponse>();
        }
        return this.ruleResponse;
    }

    /**
     * Gets the value of the ruleResponsesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResponsesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResponsesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRuleResponsesExtension() {
        if (ruleResponsesExtension == null) {
            ruleResponsesExtension = new ArrayList<Object>();
        }
        return this.ruleResponsesExtension;
    }

}
