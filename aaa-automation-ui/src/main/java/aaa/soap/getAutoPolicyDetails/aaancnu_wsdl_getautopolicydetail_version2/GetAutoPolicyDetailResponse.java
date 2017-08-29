
package aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2;

import aaa.rest.IModel;
import aaa.soap.getAutoPolicyDetails.aaancnu_common_version2.ApplicationContext;
import aaa.soap.getAutoPolicyDetails.aaancnu_common_version2.RuleResponses;
import aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2.AutoPolicySummary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for getAutoPolicyDetailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAutoPolicyDetailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationContext" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ApplicationContext" minOccurs="0"/>
 *         &lt;element name="autoPolicySummary" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AutoPolicySummary" minOccurs="0"/>
 *         &lt;element name="ruleResponse" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResponses" minOccurs="0"/>
 *         &lt;element name="messageExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "getAutoPolicyDetailResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAutoPolicyDetailResponse", propOrder = {
    "applicationContext",
    "autoPolicySummary",
    "ruleResponse",
    "messageExtension"
})
public class GetAutoPolicyDetailResponse implements IModel {

    protected ApplicationContext applicationContext;
    protected AutoPolicySummary autoPolicySummary;
    protected RuleResponses ruleResponse;
    @XmlElement(nillable = true)
    protected List<Object> messageExtension;

    /**
     * Gets the value of the applicationContext property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationContext }
     *     
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Sets the value of the applicationContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationContext }
     *     
     */
    public void setApplicationContext(ApplicationContext value) {
        this.applicationContext = value;
    }

    /**
     * Gets the value of the autoPolicySummary property.
     * 
     * @return
     *     possible object is
     *     {@link AutoPolicySummary }
     *     
     */
    public AutoPolicySummary getAutoPolicySummary() {
        return autoPolicySummary;
    }

    /**
     * Sets the value of the autoPolicySummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutoPolicySummary }
     *     
     */
    public void setAutoPolicySummary(AutoPolicySummary value) {
        this.autoPolicySummary = value;
    }

    /**
     * Gets the value of the ruleResponse property.
     * 
     * @return
     *     possible object is
     *     {@link RuleResponses }
     *     
     */
    public RuleResponses getRuleResponse() {
        return ruleResponse;
    }

    /**
     * Sets the value of the ruleResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleResponses }
     *     
     */
    public void setRuleResponse(RuleResponses value) {
        this.ruleResponse = value;
    }

    /**
     * Gets the value of the messageExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getMessageExtension() {
        if (messageExtension == null) {
            messageExtension = new ArrayList<Object>();
        }
        return this.messageExtension;
    }

}
