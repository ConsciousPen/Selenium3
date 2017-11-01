
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AvailableLimit complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvailableLimit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="combinedSingleLimitAmount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="individualLimitAmount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="occurrenceLimitAmount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="availableLimitExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailableLimit", propOrder = {
    "description",
    "displayValue",
    "combinedSingleLimitAmount",
    "individualLimitAmount",
    "occurrenceLimitAmount",
    "availableLimitExtension"
})
public class AvailableLimit {

    protected String description;
    protected String displayValue;
    protected BigInteger combinedSingleLimitAmount;
    protected BigInteger individualLimitAmount;
    protected BigInteger occurrenceLimitAmount;
    @XmlElement(nillable = true)
    protected List<Object> availableLimitExtension;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the displayValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Sets the value of the displayValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayValue(String value) {
        this.displayValue = value;
    }

    /**
     * Gets the value of the combinedSingleLimitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCombinedSingleLimitAmount() {
        return combinedSingleLimitAmount;
    }

    /**
     * Sets the value of the combinedSingleLimitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCombinedSingleLimitAmount(BigInteger value) {
        this.combinedSingleLimitAmount = value;
    }

    /**
     * Gets the value of the individualLimitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIndividualLimitAmount() {
        return individualLimitAmount;
    }

    /**
     * Sets the value of the individualLimitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIndividualLimitAmount(BigInteger value) {
        this.individualLimitAmount = value;
    }

    /**
     * Gets the value of the occurrenceLimitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOccurrenceLimitAmount() {
        return occurrenceLimitAmount;
    }

    /**
     * Sets the value of the occurrenceLimitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOccurrenceLimitAmount(BigInteger value) {
        this.occurrenceLimitAmount = value;
    }

    /**
     * Gets the value of the availableLimitExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the availableLimitExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvailableLimitExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAvailableLimitExtension() {
        if (availableLimitExtension == null) {
            availableLimitExtension = new ArrayList<Object>();
        }
        return this.availableLimitExtension;
    }

}
