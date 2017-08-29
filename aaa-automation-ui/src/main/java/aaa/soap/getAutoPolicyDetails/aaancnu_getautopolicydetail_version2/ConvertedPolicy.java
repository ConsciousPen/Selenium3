
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ConvertedPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConvertedPolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyProductSource">
 *       &lt;sequence>
 *         &lt;element name="inceptionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="billingAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="renewalEffectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="conversionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conversionEffectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="convertedPolicyExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConvertedPolicy", propOrder = {
    "inceptionDate",
    "billingAccountId",
    "renewalEffectiveDate",
    "conversionDate",
    "status",
    "conversionEffectiveDate",
    "convertedPolicyExtension"
})
public class ConvertedPolicy
    extends PolicyProductSource
{

    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar inceptionDate;
    protected String billingAccountId;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar renewalEffectiveDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar conversionDate;
    protected String status;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar conversionEffectiveDate;
    @XmlElement(nillable = true)
    protected List<Object> convertedPolicyExtension;

    /**
     * Gets the value of the inceptionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInceptionDate() {
        return inceptionDate;
    }

    /**
     * Sets the value of the inceptionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInceptionDate(XMLGregorianCalendar value) {
        this.inceptionDate = value;
    }

    /**
     * Gets the value of the billingAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAccountId() {
        return billingAccountId;
    }

    /**
     * Sets the value of the billingAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAccountId(String value) {
        this.billingAccountId = value;
    }

    /**
     * Gets the value of the renewalEffectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRenewalEffectiveDate() {
        return renewalEffectiveDate;
    }

    /**
     * Sets the value of the renewalEffectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRenewalEffectiveDate(XMLGregorianCalendar value) {
        this.renewalEffectiveDate = value;
    }

    /**
     * Gets the value of the conversionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getConversionDate() {
        return conversionDate;
    }

    /**
     * Sets the value of the conversionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setConversionDate(XMLGregorianCalendar value) {
        this.conversionDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the conversionEffectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getConversionEffectiveDate() {
        return conversionEffectiveDate;
    }

    /**
     * Sets the value of the conversionEffectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setConversionEffectiveDate(XMLGregorianCalendar value) {
        this.conversionEffectiveDate = value;
    }

    /**
     * Gets the value of the convertedPolicyExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the convertedPolicyExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConvertedPolicyExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getConvertedPolicyExtension() {
        if (convertedPolicyExtension == null) {
            convertedPolicyExtension = new ArrayList<Object>();
        }
        return this.convertedPolicyExtension;
    }

}
