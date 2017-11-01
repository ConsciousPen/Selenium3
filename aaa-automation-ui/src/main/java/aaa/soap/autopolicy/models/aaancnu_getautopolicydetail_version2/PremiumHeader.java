
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PremiumHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PremiumHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualPremium" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="annualPremium" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="premiumHeaderExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PremiumHeader", propOrder = {
    "actualPremium",
    "annualPremium",
    "premiumHeaderExtension"
})
@XmlSeeAlso({
    PremiumEntry.class
})
public class PremiumHeader {

    protected BigDecimal actualPremium;
    protected BigDecimal annualPremium;
    @XmlElement(nillable = true)
    protected List<Object> premiumHeaderExtension;

    /**
     * Gets the value of the actualPremium property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getActualPremium() {
        return actualPremium;
    }

    /**
     * Sets the value of the actualPremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setActualPremium(BigDecimal value) {
        this.actualPremium = value;
    }

    /**
     * Gets the value of the annualPremium property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAnnualPremium() {
        return annualPremium;
    }

    /**
     * Sets the value of the annualPremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAnnualPremium(BigDecimal value) {
        this.annualPremium = value;
    }

    /**
     * Gets the value of the premiumHeaderExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiumHeaderExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiumHeaderExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPremiumHeaderExtension() {
        if (premiumHeaderExtension == null) {
            premiumHeaderExtension = new ArrayList<Object>();
        }
        return this.premiumHeaderExtension;
    }

}
