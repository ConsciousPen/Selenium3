
package aaa.soap.autopolicy.models.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RuleResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ruleId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruleDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleSeverity" minOccurs="0"/>
 *         &lt;element name="isOverridable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="overrideAuthorityLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="sourceSystem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleCategory" minOccurs="0"/>
 *         &lt;element name="correlationId" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="ruleAttributes" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleAttributes" minOccurs="0"/>
 *         &lt;element name="ruleResultExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleResult", propOrder = {
    "ruleId",
    "ruleDescription",
    "action",
    "severity",
    "isOverridable",
    "overrideAuthorityLevel",
    "instanceId",
    "sourceSystem",
    "timestamp",
    "category",
    "correlationId",
    "ruleAttributes",
    "ruleResultExtension"
})
public class RuleResult {

    protected String ruleId;
    protected String ruleDescription;
    protected String action;
    @XmlSchemaType(name = "string")
    protected RuleSeverity severity;
    protected Boolean isOverridable;
    protected String overrideAuthorityLevel;
    protected BigInteger instanceId;
    protected String sourceSystem;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlSchemaType(name = "string")
    protected RuleCategory category;
    protected BigInteger correlationId;
    protected RuleAttributes ruleAttributes;
    @XmlElement(nillable = true)
    protected List<Object> ruleResultExtension;

    /**
     * Gets the value of the ruleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the value of the ruleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuleId(String value) {
        this.ruleId = value;
    }

    /**
     * Gets the value of the ruleDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuleDescription() {
        return ruleDescription;
    }

    /**
     * Sets the value of the ruleDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuleDescription(String value) {
        this.ruleDescription = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link RuleSeverity }
     *     
     */
    public RuleSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleSeverity }
     *     
     */
    public void setSeverity(RuleSeverity value) {
        this.severity = value;
    }

    /**
     * Gets the value of the isOverridable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOverridable() {
        return isOverridable;
    }

    /**
     * Sets the value of the isOverridable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOverridable(Boolean value) {
        this.isOverridable = value;
    }

    /**
     * Gets the value of the overrideAuthorityLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOverrideAuthorityLevel() {
        return overrideAuthorityLevel;
    }

    /**
     * Sets the value of the overrideAuthorityLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverrideAuthorityLevel(String value) {
        this.overrideAuthorityLevel = value;
    }

    /**
     * Gets the value of the instanceId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the value of the instanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInstanceId(BigInteger value) {
        this.instanceId = value;
    }

    /**
     * Gets the value of the sourceSystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Sets the value of the sourceSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceSystem(String value) {
        this.sourceSystem = value;
    }

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
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link RuleCategory }
     *     
     */
    public RuleCategory getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleCategory }
     *     
     */
    public void setCategory(RuleCategory value) {
        this.category = value;
    }

    /**
     * Gets the value of the correlationId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the correlationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCorrelationId(BigInteger value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the ruleAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link RuleAttributes }
     *     
     */
    public RuleAttributes getRuleAttributes() {
        return ruleAttributes;
    }

    /**
     * Sets the value of the ruleAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleAttributes }
     *     
     */
    public void setRuleAttributes(RuleAttributes value) {
        this.ruleAttributes = value;
    }

    /**
     * Gets the value of the ruleResultExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResultExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResultExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRuleResultExtension() {
        if (ruleResultExtension == null) {
            ruleResultExtension = new ArrayList<Object>();
        }
        return this.ruleResultExtension;
    }

}
