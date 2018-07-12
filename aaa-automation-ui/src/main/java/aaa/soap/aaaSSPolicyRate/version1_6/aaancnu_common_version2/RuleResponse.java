
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for RuleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="ruleDecision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ruleScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="service" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ruleScorecard" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ruleResults" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResult" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ruleResponseExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleResponse", propOrder = {
    "timestamp",
    "ruleDecision",
    "ruleScore",
    "service",
    "ruleScorecard",
    "ruleResults",
    "ruleResponseExtension"
})
public class RuleResponse {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    protected String ruleDecision;
    protected String ruleScore;
    @XmlElement(required = true)
    protected String service;
    @XmlElement(nillable = true)
    protected List<String> ruleScorecard;
    @XmlElement(nillable = true)
    protected List<RuleResult> ruleResults;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> ruleResponseExtension;

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the ruleDecision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuleDecision() {
        return ruleDecision;
    }

    /**
     * Sets the value of the ruleDecision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuleDecision(String value) {
        this.ruleDecision = value;
    }

    /**
     * Gets the value of the ruleScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuleScore() {
        return ruleScore;
    }

    /**
     * Sets the value of the ruleScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuleScore(String value) {
        this.ruleScore = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the ruleScorecard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleScorecard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleScorecard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRuleScorecard() {
        if (ruleScorecard == null) {
            ruleScorecard = new ArrayList<String>();
        }
        return this.ruleScorecard;
    }

    /**
     * Gets the value of the ruleResults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleResult }
     * 
     * 
     */
    public List<RuleResult> getRuleResults() {
        if (ruleResults == null) {
            ruleResults = new ArrayList<RuleResult>();
        }
        return this.ruleResults;
    }

    /**
     * Gets the value of the ruleResponseExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResponseExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResponseExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getRuleResponseExtension() {
        if (ruleResponseExtension == null) {
            ruleResponseExtension = new ArrayList<ExtensionArea>();
        }
        return this.ruleResponseExtension;
    }

}
