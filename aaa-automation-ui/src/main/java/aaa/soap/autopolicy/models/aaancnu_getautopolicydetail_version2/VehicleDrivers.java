
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VehicleDrivers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehicleDrivers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="driver" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}VehicleDriverSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vehicleDriversExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehicleDrivers", propOrder = {
    "driver",
    "vehicleDriversExtension"
})
public class VehicleDrivers {

    @XmlElement(nillable = true)
    protected List<VehicleDriverSummary> driver;
    @XmlElement(nillable = true)
    protected List<Object> vehicleDriversExtension;

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
     * {@link VehicleDriverSummary }
     * 
     * 
     */
    public List<VehicleDriverSummary> getDriver() {
        if (driver == null) {
            driver = new ArrayList<VehicleDriverSummary>();
        }
        return this.driver;
    }

    /**
     * Gets the value of the vehicleDriversExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicleDriversExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicleDriversExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getVehicleDriversExtension() {
        if (vehicleDriversExtension == null) {
            vehicleDriversExtension = new ArrayList<Object>();
        }
        return this.vehicleDriversExtension;
    }

}
