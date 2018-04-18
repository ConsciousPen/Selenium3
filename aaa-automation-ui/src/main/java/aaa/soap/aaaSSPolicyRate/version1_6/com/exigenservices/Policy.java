
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema.PolicyComponent;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for Policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Policy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MsgID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResponseToMsgID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MessageStatus" type="{http://exigenservices.com/ipb/policy/integration}MessageStatusType" minOccurs="0"/&gt;
 *         &lt;element name="Policy" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}PolicyComponent"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="policyImageType" type="{http://exigenservices.com/ipb/policy/integration}PolicyImageTypeEnum" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Policy", propOrder = {
    "msgID",
    "responseToMsgID",
    "messageStatus",
    "policy"
})
public class Policy {

    @XmlElement(name = "MsgID")
    protected String msgID;
    @XmlElement(name = "ResponseToMsgID")
    protected String responseToMsgID;
    @XmlElement(name = "MessageStatus")
    protected MessageStatusType messageStatus;
    @XmlElement(name = "Policy", required = true)
    protected PolicyComponent policy;
    @XmlAttribute(name = "policyImageType")
    protected PolicyImageTypeEnum policyImageType;

    /**
     * Gets the value of the msgID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgID() {
        return msgID;
    }

    /**
     * Sets the value of the msgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgID(String value) {
        this.msgID = value;
    }

    /**
     * Gets the value of the responseToMsgID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseToMsgID() {
        return responseToMsgID;
    }

    /**
     * Sets the value of the responseToMsgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseToMsgID(String value) {
        this.responseToMsgID = value;
    }

    /**
     * Gets the value of the messageStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MessageStatusType }
     *     
     */
    public MessageStatusType getMessageStatus() {
        return messageStatus;
    }

    /**
     * Sets the value of the messageStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatusType }
     *     
     */
    public void setMessageStatus(MessageStatusType value) {
        this.messageStatus = value;
    }

    /**
     * Gets the value of the policy property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyComponent }
     *     
     */
    public PolicyComponent getPolicy() {
        return policy;
    }

    /**
     * Sets the value of the policy property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyComponent }
     *     
     */
    public void setPolicy(PolicyComponent value) {
        this.policy = value;
    }

    /**
     * Gets the value of the policyImageType property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyImageTypeEnum }
     *     
     */
    public PolicyImageTypeEnum getPolicyImageType() {
        return policyImageType;
    }

    /**
     * Sets the value of the policyImageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyImageTypeEnum }
     *     
     */
    public void setPolicyImageType(PolicyImageTypeEnum value) {
        this.policyImageType = value;
    }

}
