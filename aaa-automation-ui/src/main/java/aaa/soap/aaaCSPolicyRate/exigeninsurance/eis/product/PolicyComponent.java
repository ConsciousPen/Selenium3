
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for PolicyComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AAACSAAutoPolicy" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}AAACSAAutoPolicy"/>
 *       &lt;/sequence>
 *       &lt;attribute name="entitySubtype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rootEntityType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    protected AAACSAAutoPolicy aaacsaAutoPolicy;
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
     *     {@link AAACSAAutoPolicy }
     *     
     */
    public AAACSAAutoPolicy getAAACSAAutoPolicy() {
        return aaacsaAutoPolicy;
    }

    /**
     * Sets the value of the aaacsaAutoPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAACSAAutoPolicy }
     *     
     */
    public void setAAACSAAutoPolicy(AAACSAAutoPolicy value) {
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
