
package aaa.soap.aaaCSPolicyRate.aaancnu_common_version2;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="ruleId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ruleDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleSeverity"/>
 *         &lt;element name="isOverridable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="overrideAuthorityLevel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="sourceSystem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="category" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleCategory" minOccurs="0"/>
 *         &lt;element name="correlationId" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ruleattribute" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ruleResultExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/>
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
    "ruleattribute",
    "ruleResultExtension"
})
public class RuleResult {

    @XmlElement(required = true)
    protected String ruleId;
    @XmlElement(required = true)
    protected String ruleDescription;
    protected String action;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleSeverity severity;
    protected boolean isOverridable;
    @XmlElement(required = true)
    protected String overrideAuthorityLevel;
    @XmlElement(required = true)
    protected BigInteger instanceId;
    @XmlElement(required = true)
    protected String sourceSystem;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlSchemaType(name = "string")
    protected RuleCategory category;
    @XmlElement(required = true)
    protected BigInteger correlationId;
    @XmlElement(nillable = true)
    protected List<RuleAttribute> ruleattribute;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> ruleResultExtension;

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
     */
    public boolean isIsOverridable() {
        return isOverridable;
    }

    /**
     * Sets the value of the isOverridable property.
     * 
     */
    public void setIsOverridable(boolean value) {
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
     * Gets the value of the ruleattribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleattribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleattribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleAttribute }
     * 
     * 
     */
    public List<RuleAttribute> getRuleattribute() {
        if (ruleattribute == null) {
            ruleattribute = new ArrayList<RuleAttribute>();
        }
        return this.ruleattribute;
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
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getRuleResultExtension() {
        if (ruleResultExtension == null) {
            ruleResultExtension = new ArrayList<ExtensionArea>();
        }
        return this.ruleResultExtension;
    }

}
