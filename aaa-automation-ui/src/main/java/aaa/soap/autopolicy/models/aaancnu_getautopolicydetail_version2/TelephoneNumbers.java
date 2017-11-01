
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TelephoneNumbers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TelephoneNumbers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="telephoneNumber" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceTelephone" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="telephoneNumbersExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelephoneNumbers", propOrder = {
    "telephoneNumber",
    "telephoneNumbersExtension"
})
public class TelephoneNumbers {

    @XmlElement(nillable = true)
    protected List<PreferenceTelephone> telephoneNumber;
    @XmlElement(nillable = true)
    protected List<Object> telephoneNumbersExtension;

    /**
     * Gets the value of the telephoneNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telephoneNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelephoneNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreferenceTelephone }
     * 
     * 
     */
    public List<PreferenceTelephone> getTelephoneNumber() {
        if (telephoneNumber == null) {
            telephoneNumber = new ArrayList<PreferenceTelephone>();
        }
        return this.telephoneNumber;
    }

    /**
     * Gets the value of the telephoneNumbersExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telephoneNumbersExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelephoneNumbersExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getTelephoneNumbersExtension() {
        if (telephoneNumbersExtension == null) {
            telephoneNumbersExtension = new ArrayList<Object>();
        }
        return this.telephoneNumbersExtension;
    }

}
