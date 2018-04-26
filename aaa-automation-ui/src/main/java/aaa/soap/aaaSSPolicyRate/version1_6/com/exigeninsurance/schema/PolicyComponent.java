
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for PolicyComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyComponent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AAACSAAutoPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAASSAutoPolicy"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="entitySubtype" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="rootEntityType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyComponent", propOrder = {
    "aaacsaAutoPolicy"
})
public class PolicyComponent {

    @XmlElement(name = "AAACSAAutoPolicy", required = true)
    protected AAASSAutoPolicy aaacsaAutoPolicy;
    @XmlAttribute(name = "entitySubtype")
    protected String entitySubtype;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "rootEntityType")
    protected String rootEntityType;
    @XmlAttribute(name = "version")
    protected BigDecimal version;

    /**
     * Gets the value of the aaacsaAutoPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link AAASSAutoPolicy }
     *     
     */
    public AAASSAutoPolicy getAAACSAAutoPolicy() {
        return aaacsaAutoPolicy;
    }

    /**
     * Sets the value of the aaacsaAutoPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAASSAutoPolicy }
     *     
     */
    public void setAAACSAAutoPolicy(AAASSAutoPolicy value) {
        this.aaacsaAutoPolicy = value;
    }

    /**
     * Gets the value of the entitySubtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntitySubtype() {
        return entitySubtype;
    }

    /**
     * Sets the value of the entitySubtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntitySubtype(String value) {
        this.entitySubtype = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the rootEntityType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootEntityType() {
        return rootEntityType;
    }

    /**
     * Sets the value of the rootEntityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootEntityType(String value) {
        this.rootEntityType = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

}
