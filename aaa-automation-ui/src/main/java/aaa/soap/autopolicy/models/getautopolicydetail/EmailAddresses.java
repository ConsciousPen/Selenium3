
package aaa.soap.autopolicy.models.getautopolicydetail;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EmailAddresses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EmailAddresses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="emailAddress" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceEmail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="emailAddressesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmailAddresses", propOrder = {
    "emailAddress",
    "emailAddressesExtension"
})
public class EmailAddresses {

    @XmlElement(nillable = true)
    protected List<PreferenceEmail> emailAddress;
    @XmlElement(nillable = true)
    protected List<Object> emailAddressesExtension;

    /**
     * Gets the value of the emailAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the emailAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmailAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreferenceEmail }
     * 
     * 
     */
    public List<PreferenceEmail> getEmailAddress() {
        if (emailAddress == null) {
            emailAddress = new ArrayList<PreferenceEmail>();
        }
        return this.emailAddress;
    }

    /**
     * Gets the value of the emailAddressesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the emailAddressesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmailAddressesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getEmailAddressesExtension() {
        if (emailAddressesExtension == null) {
            emailAddressesExtension = new ArrayList<Object>();
        }
        return this.emailAddressesExtension;
    }

}
