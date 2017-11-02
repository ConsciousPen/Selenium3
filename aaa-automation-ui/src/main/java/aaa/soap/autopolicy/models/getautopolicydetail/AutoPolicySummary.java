
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AutoPolicySummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AutoPolicySummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policySummary" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicySummary" minOccurs="0"/>
 *         &lt;element name="vehicles" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyVehicles" minOccurs="0"/>
 *         &lt;element name="drivers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyDrivers" minOccurs="0"/>
 *         &lt;element name="autoPolicySummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutoPolicySummary", propOrder = {
    "policySummary",
    "vehicles",
    "drivers",
    "autoPolicySummaryExtension"
})
public class AutoPolicySummary {

    protected PolicySummary policySummary;
    protected PolicyVehicles vehicles;
    protected PolicyDrivers drivers;
    @XmlElement(nillable = true)
    protected List<Object> autoPolicySummaryExtension;

    /**
     * Gets the value of the policySummary property.
     * 
     * @return
     *     possible object is
     *     {@link PolicySummary }
     *     
     */
    public PolicySummary getPolicySummary() {
        return policySummary;
    }

    /**
     * Sets the value of the policySummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicySummary }
     *     
     */
    public void setPolicySummary(PolicySummary value) {
        this.policySummary = value;
    }

    /**
     * Gets the value of the vehicles property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyVehicles }
     *     
     */
    public PolicyVehicles getVehicles() {
        return vehicles;
    }

    /**
     * Sets the value of the vehicles property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyVehicles }
     *     
     */
    public void setVehicles(PolicyVehicles value) {
        this.vehicles = value;
    }

    /**
     * Gets the value of the drivers property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyDrivers }
     *     
     */
    public PolicyDrivers getDrivers() {
        return drivers;
    }

    /**
     * Sets the value of the drivers property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyDrivers }
     *     
     */
    public void setDrivers(PolicyDrivers value) {
        this.drivers = value;
    }

    /**
     * Gets the value of the autoPolicySummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the autoPolicySummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAutoPolicySummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAutoPolicySummaryExtension() {
        if (autoPolicySummaryExtension == null) {
            autoPolicySummaryExtension = new ArrayList<Object>();
        }
        return this.autoPolicySummaryExtension;
    }

}
