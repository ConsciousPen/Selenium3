
package aaa.soap.autopolicy.models.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for RuleAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ruleattribute" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ruleAttributesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleAttributes", propOrder = {
    "ruleattribute",
    "ruleAttributesExtension"
})
public class RuleAttributes {

    @XmlElement(nillable = true)
    protected List<RuleAttribute> ruleattribute;
    @XmlElement(nillable = true)
    protected List<Object> ruleAttributesExtension;

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
     * Gets the value of the ruleAttributesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleAttributesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleAttributesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRuleAttributesExtension() {
        if (ruleAttributesExtension == null) {
            ruleAttributesExtension = new ArrayList<Object>();
        }
        return this.ruleAttributesExtension;
    }

}
