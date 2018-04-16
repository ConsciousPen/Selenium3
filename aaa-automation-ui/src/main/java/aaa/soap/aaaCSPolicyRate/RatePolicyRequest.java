
package aaa.soap.aaaCSPolicyRate;

import aaa.rest.IModel;
import aaa.soap.aaaCSPolicyRate.aaancnu_common_version2.ApplicationContext;
import aaa.soap.aaaCSPolicyRate.exigenservices.Policy;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ApplicationContext" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ApplicationContext" minOccurs="0"/>
 *         &lt;element name="Policy" type="{http://exigenservices.com/ipb/policy/integration}Policy"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlRootElement(name = "RatePolicyRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "applicationContext",
    "policy"
})

public class RatePolicyRequest implements IModel {

    @XmlElement(name = "ApplicationContext")
    protected ApplicationContext applicationContext;
    @XmlElement(name = "Policy", required = true)
    protected Policy policy;

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
     * Gets the value of the policy property.
     * 
     * @return
     *     possible object is
     *     {@link Policy }
     *     
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * Sets the value of the policy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Policy }
     *     
     */
    public void setPolicy(Policy value) {
        this.policy = value;
    }

}
