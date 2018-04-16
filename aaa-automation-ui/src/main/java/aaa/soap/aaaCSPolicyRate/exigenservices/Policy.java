
package aaa.soap.aaaCSPolicyRate.exigenservices;

import aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product.PolicyComponent;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for Policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Policy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResponseToMsgID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MessageStatus" type="{http://exigenservices.com/ipb/policy/integration}MessageStatusType" minOccurs="0"/>
 *         &lt;element name="Policy" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PolicyComponent"/>
 *       &lt;/sequence>
 *       &lt;attribute name="policyImageType" type="{http://exigenservices.com/ipb/policy/integration}PolicyImageTypeEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
