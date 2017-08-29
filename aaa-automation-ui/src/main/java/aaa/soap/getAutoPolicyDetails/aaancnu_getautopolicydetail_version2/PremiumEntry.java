
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PremiumEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PremiumEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PremiumHeader">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="premiumFactor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="changePremium" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="premiumEntryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PremiumEntry", propOrder = {
    "type",
    "premiumFactor",
    "changePremium",
    "premiumEntryExtension"
})
public class PremiumEntry
    extends PremiumHeader
{

    protected String type;
    protected String premiumFactor;
    protected BigDecimal changePremium;
    @XmlElement(nillable = true)
    protected List<Object> premiumEntryExtension;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the premiumFactor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPremiumFactor() {
        return premiumFactor;
    }

    /**
     * Sets the value of the premiumFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPremiumFactor(String value) {
        this.premiumFactor = value;
    }

    /**
     * Gets the value of the changePremium property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getChangePremium() {
        return changePremium;
    }

    /**
     * Sets the value of the changePremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setChangePremium(BigDecimal value) {
        this.changePremium = value;
    }

    /**
     * Gets the value of the premiumEntryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiumEntryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiumEntryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPremiumEntryExtension() {
        if (premiumEntryExtension == null) {
            premiumEntryExtension = new ArrayList<Object>();
        }
        return this.premiumEntryExtension;
    }

}
