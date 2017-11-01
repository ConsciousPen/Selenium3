
package aaa.soap.autopolicy.models.aaancnu_common_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RuleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ruleDecision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruleScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="service" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruleScorecard" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ruleResults" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResults" minOccurs="0"/>
 *         &lt;element name="ruleResponseExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    protected String ruleDecision;
    protected String ruleScore;
    protected String service;
    @XmlElement(nillable = true)
    protected List<String> ruleScorecard;
    protected RuleResults ruleResults;
    @XmlElement(nillable = true)
    protected List<Object> ruleResponseExtension;

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
     * @return
     *     possible object is
     *     {@link RuleResults }
     *     
     */
    public RuleResults getRuleResults() {
        return ruleResults;
    }

    /**
     * Sets the value of the ruleResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleResults }
     *     
     */
    public void setRuleResults(RuleResults value) {
        this.ruleResults = value;
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
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRuleResponseExtension() {
        if (ruleResponseExtension == null) {
            ruleResponseExtension = new ArrayList<Object>();
        }
        return this.ruleResponseExtension;
    }

}
