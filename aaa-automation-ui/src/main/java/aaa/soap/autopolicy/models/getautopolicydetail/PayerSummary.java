
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PayerSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayerSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PartyName" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceEmail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="telephoneNumbers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TelephoneNumbers" minOccurs="0"/>
 *         &lt;element name="postalAddress" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" minOccurs="0"/>
 *         &lt;element name="payerSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayerSummary", propOrder = {
    "name",
    "email",
    "telephoneNumbers",
    "postalAddress",
    "payerSummaryExtension"
})
public class PayerSummary {

    protected PartyName name;
    @XmlElement(nillable = true)
    protected List<PreferenceEmail> email;
    protected TelephoneNumbers telephoneNumbers;
    protected PostalAddressSummary postalAddress;
    @XmlElement(nillable = true)
    protected List<Object> payerSummaryExtension;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link PartyName }
     *     
     */
    public PartyName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyName }
     *     
     */
    public void setName(PartyName value) {
        this.name = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the email property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreferenceEmail }
     * 
     * 
     */
    public List<PreferenceEmail> getEmail() {
        if (email == null) {
            email = new ArrayList<PreferenceEmail>();
        }
        return this.email;
    }

    /**
     * Gets the value of the telephoneNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link TelephoneNumbers }
     *     
     */
    public TelephoneNumbers getTelephoneNumbers() {
        return telephoneNumbers;
    }

    /**
     * Sets the value of the telephoneNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link TelephoneNumbers }
     *     
     */
    public void setTelephoneNumbers(TelephoneNumbers value) {
        this.telephoneNumbers = value;
    }

    /**
     * Gets the value of the postalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSummary }
     *     
     */
    public PostalAddressSummary getPostalAddress() {
        return postalAddress;
    }

    /**
     * Sets the value of the postalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSummary }
     *     
     */
    public void setPostalAddress(PostalAddressSummary value) {
        this.postalAddress = value;
    }

    /**
     * Gets the value of the payerSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payerSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayerSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPayerSummaryExtension() {
        if (payerSummaryExtension == null) {
            payerSummaryExtension = new ArrayList<Object>();
        }
        return this.payerSummaryExtension;
    }

}
