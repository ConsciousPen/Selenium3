
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DriverAccidents complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriverAccidents">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accident" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverAccident" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="driverAccidentsExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriverAccidents", propOrder = {
    "accident",
    "driverAccidentsExtension"
})
public class DriverAccidents {

    @XmlElement(nillable = true)
    protected List<DriverAccident> accident;
    @XmlElement(nillable = true)
    protected List<Object> driverAccidentsExtension;

    /**
     * Gets the value of the accident property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accident property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccident().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DriverAccident }
     * 
     * 
     */
    public List<DriverAccident> getAccident() {
        if (accident == null) {
            accident = new ArrayList<DriverAccident>();
        }
        return this.accident;
    }

    /**
     * Gets the value of the driverAccidentsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driverAccidentsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriverAccidentsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDriverAccidentsExtension() {
        if (driverAccidentsExtension == null) {
            driverAccidentsExtension = new ArrayList<Object>();
        }
        return this.driverAccidentsExtension;
    }

}
