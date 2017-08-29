
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DriverViolations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriverViolations">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="violation" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverViolation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="driverViolationsExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriverViolations", propOrder = {
    "violation",
    "driverViolationsExtension"
})
public class DriverViolations {

    @XmlElement(nillable = true)
    protected List<DriverViolation> violation;
    @XmlElement(nillable = true)
    protected List<Object> driverViolationsExtension;

    /**
     * Gets the value of the violation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the violation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViolation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DriverViolation }
     * 
     * 
     */
    public List<DriverViolation> getViolation() {
        if (violation == null) {
            violation = new ArrayList<DriverViolation>();
        }
        return this.violation;
    }

    /**
     * Gets the value of the driverViolationsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driverViolationsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriverViolationsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDriverViolationsExtension() {
        if (driverViolationsExtension == null) {
            driverViolationsExtension = new ArrayList<Object>();
        }
        return this.driverViolationsExtension;
    }

}
