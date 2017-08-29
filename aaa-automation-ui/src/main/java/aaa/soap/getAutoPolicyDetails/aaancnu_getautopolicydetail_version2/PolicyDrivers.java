
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyDrivers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyDrivers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="driver" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="policyDriversExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyDrivers", propOrder = {
    "driver",
    "policyDriversExtension"
})
public class PolicyDrivers {

    @XmlElement(nillable = true)
    protected List<DriverSummary> driver;
    @XmlElement(nillable = true)
    protected List<Object> policyDriversExtension;

    /**
     * Gets the value of the driver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DriverSummary }
     * 
     * 
     */
    public List<DriverSummary> getDriver() {
        if (driver == null) {
            driver = new ArrayList<DriverSummary>();
        }
        return this.driver;
    }

    /**
     * Gets the value of the policyDriversExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyDriversExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyDriversExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicyDriversExtension() {
        if (policyDriversExtension == null) {
            policyDriversExtension = new ArrayList<Object>();
        }
        return this.policyDriversExtension;
    }

}
