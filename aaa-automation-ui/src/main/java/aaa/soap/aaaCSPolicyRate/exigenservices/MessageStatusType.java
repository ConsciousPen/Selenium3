
package aaa.soap.aaaCSPolicyRate.exigenservices;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for MessageStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgStatus" type="{http://exigenservices.com/ipb/policy/integration}MessageStatusEnum"/>
 *         &lt;element name="ExtendedStatus" type="{http://exigenservices.com/ipb/policy/integration}ExtendedStatusType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageStatusType", propOrder = {
    "msgStatus",
    "extendedStatus"
})
public class MessageStatusType {

    @XmlElement(name = "MsgStatus", required = true)
    @XmlSchemaType(name = "string")
    protected MessageStatusEnum msgStatus;
    @XmlElement(name = "ExtendedStatus")
    protected List<ExtendedStatusType> extendedStatus;

    /**
     * Gets the value of the msgStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MessageStatusEnum }
     *     
     */
    public MessageStatusEnum getMsgStatus() {
        return msgStatus;
    }

    /**
     * Sets the value of the msgStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatusEnum }
     *     
     */
    public void setMsgStatus(MessageStatusEnum value) {
        this.msgStatus = value;
    }

    /**
     * Gets the value of the extendedStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtendedStatusType }
     * 
     * 
     */
    public List<ExtendedStatusType> getExtendedStatus() {
        if (extendedStatus == null) {
            extendedStatus = new ArrayList<ExtendedStatusType>();
        }
        return this.extendedStatus;
    }

}
