
package aaa.soap.aaaCSPolicyRate.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for StatusWithCommonReason complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusWithCommonReason">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.aaancnuit.com.AAANCNU_Common_version2}Status">
 *       &lt;sequence>
 *         &lt;element name="statusWithCommonReasonExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusWithCommonReason", propOrder = {
    "statusWithCommonReasonExtension"
})
public class StatusWithCommonReason
    extends Status
{

    @XmlElement(nillable = true)
    protected List<ExtensionArea> statusWithCommonReasonExtension;

    /**
     * Gets the value of the statusWithCommonReasonExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statusWithCommonReasonExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatusWithCommonReasonExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getStatusWithCommonReasonExtension() {
        if (statusWithCommonReasonExtension == null) {
            statusWithCommonReasonExtension = new ArrayList<ExtensionArea>();
        }
        return this.statusWithCommonReasonExtension;
    }

}
