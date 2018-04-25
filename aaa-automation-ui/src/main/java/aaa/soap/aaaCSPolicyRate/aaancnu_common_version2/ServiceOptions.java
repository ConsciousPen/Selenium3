
package aaa.soap.aaaCSPolicyRate.aaancnu_common_version2;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ServiceOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="echoBackRequest" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="startPaginationInstance" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="maximumReturnedInstances" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="serviceResponseSort" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ResponseSort" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="serviceOptionsExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceOptions", propOrder = {
    "echoBackRequest",
    "startPaginationInstance",
    "maximumReturnedInstances",
    "serviceResponseSort",
    "serviceOptionsExtension"
})
public class ServiceOptions {

    @XmlElement(defaultValue = "false")
    protected boolean echoBackRequest;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger startPaginationInstance;
    @XmlElement(defaultValue = "50")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumReturnedInstances;
    @XmlElement(nillable = true)
    protected List<ResponseSort> serviceResponseSort;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> serviceOptionsExtension;

    /**
     * Gets the value of the echoBackRequest property.
     * 
     */
    public boolean isEchoBackRequest() {
        return echoBackRequest;
    }

    /**
     * Sets the value of the echoBackRequest property.
     * 
     */
    public void setEchoBackRequest(boolean value) {
        this.echoBackRequest = value;
    }

    /**
     * Gets the value of the startPaginationInstance property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartPaginationInstance() {
        return startPaginationInstance;
    }

    /**
     * Sets the value of the startPaginationInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStartPaginationInstance(BigInteger value) {
        this.startPaginationInstance = value;
    }

    /**
     * Gets the value of the maximumReturnedInstances property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumReturnedInstances() {
        return maximumReturnedInstances;
    }

    /**
     * Sets the value of the maximumReturnedInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumReturnedInstances(BigInteger value) {
        this.maximumReturnedInstances = value;
    }

    /**
     * Gets the value of the serviceResponseSort property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceResponseSort property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceResponseSort().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResponseSort }
     * 
     * 
     */
    public List<ResponseSort> getServiceResponseSort() {
        if (serviceResponseSort == null) {
            serviceResponseSort = new ArrayList<ResponseSort>();
        }
        return this.serviceResponseSort;
    }

    /**
     * Gets the value of the serviceOptionsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceOptionsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceOptionsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getServiceOptionsExtension() {
        if (serviceOptionsExtension == null) {
            serviceOptionsExtension = new ArrayList<ExtensionArea>();
        }
        return this.serviceOptionsExtension;
    }

}
