
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyVehicles complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyVehicles">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vehicle" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}VehicleSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="policyVehiclesExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyVehicles", propOrder = {
    "vehicle",
    "policyVehiclesExtension"
})
public class PolicyVehicles {

    @XmlElement(nillable = true)
    protected List<VehicleSummary> vehicle;
    @XmlElement(nillable = true)
    protected List<Object> policyVehiclesExtension;

    /**
     * Gets the value of the vehicle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VehicleSummary }
     * 
     * 
     */
    public List<VehicleSummary> getVehicle() {
        if (vehicle == null) {
            vehicle = new ArrayList<VehicleSummary>();
        }
        return this.vehicle;
    }

    /**
     * Gets the value of the policyVehiclesExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyVehiclesExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyVehiclesExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicyVehiclesExtension() {
        if (policyVehiclesExtension == null) {
            policyVehiclesExtension = new ArrayList<Object>();
        }
        return this.policyVehiclesExtension;
    }

}
