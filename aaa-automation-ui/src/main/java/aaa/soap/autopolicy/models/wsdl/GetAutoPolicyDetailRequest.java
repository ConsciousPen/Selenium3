
package aaa.soap.autopolicy.models.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

import aaa.rest.IModel;
import aaa.soap.autopolicy.models.common.ApplicationContext;
import aaa.soap.autopolicy.models.getautopolicydetail.AgentSummary;


/**
 * <p>Java class for getAutoPolicyDetailRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAutoPolicyDetailRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationContext" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ApplicationContext" minOccurs="0"/>
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceSystem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="asOfDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="agent" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AgentSummary" minOccurs="0"/>
 *         &lt;element name="excludeCoverage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="excludeVehicles" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="excludeDrivers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="returnCurrentWhenNotFnd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="returnConvertedPolicyDetail" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="messageExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "getAutoPolicyDetailRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAutoPolicyDetailRequest", propOrder = {
    "applicationContext",
    "policyNumber",
    "sourceSystem",
    "asOfDate",
    "agent",
    "excludeCoverage",
    "excludeVehicles",
    "excludeDrivers",
    "returnCurrentWhenNotFnd",
    "returnConvertedPolicyDetail",
    "messageExtension"
})
public class GetAutoPolicyDetailRequest implements IModel{

    protected ApplicationContext applicationContext;
    protected String policyNumber;
    protected String sourceSystem;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar asOfDate;
    protected AgentSummary agent;
    @XmlElement(defaultValue = "false")
    protected Boolean excludeCoverage;
    @XmlElement(defaultValue = "false")
    protected Boolean excludeVehicles;
    @XmlElement(defaultValue = "false")
    protected Boolean excludeDrivers;
    @XmlElement(defaultValue = "false")
    protected Boolean returnCurrentWhenNotFnd;
    @XmlElement(defaultValue = "false")
    protected Boolean returnConvertedPolicyDetail;
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
     * Gets the value of the policyNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Sets the value of the policyNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyNumber(String value) {
        this.policyNumber = value;
    }

    /**
     * Gets the value of the sourceSystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Sets the value of the sourceSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceSystem(String value) {
        this.sourceSystem = value;
    }

    /**
     * Gets the value of the asOfDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAsOfDate() {
        return asOfDate;
    }

    /**
     * Sets the value of the asOfDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAsOfDate(XMLGregorianCalendar value) {
        this.asOfDate = value;
    }

    /**
     * Gets the value of the agent property.
     * 
     * @return
     *     possible object is
     *     {@link AgentSummary }
     *     
     */
    public AgentSummary getAgent() {
        return agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentSummary }
     *     
     */
    public void setAgent(AgentSummary value) {
        this.agent = value;
    }

    /**
     * Gets the value of the excludeCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeCoverage() {
        return excludeCoverage;
    }

    /**
     * Sets the value of the excludeCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeCoverage(Boolean value) {
        this.excludeCoverage = value;
    }

    /**
     * Gets the value of the excludeVehicles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeVehicles() {
        return excludeVehicles;
    }

    /**
     * Sets the value of the excludeVehicles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeVehicles(Boolean value) {
        this.excludeVehicles = value;
    }

    /**
     * Gets the value of the excludeDrivers property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeDrivers() {
        return excludeDrivers;
    }

    /**
     * Sets the value of the excludeDrivers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeDrivers(Boolean value) {
        this.excludeDrivers = value;
    }

    /**
     * Gets the value of the returnCurrentWhenNotFnd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturnCurrentWhenNotFnd() {
        return returnCurrentWhenNotFnd;
    }

    /**
     * Sets the value of the returnCurrentWhenNotFnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnCurrentWhenNotFnd(Boolean value) {
        this.returnCurrentWhenNotFnd = value;
    }

    /**
     * Gets the value of the returnConvertedPolicyDetail property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturnConvertedPolicyDetail() {
        return returnConvertedPolicyDetail;
    }

    /**
     * Sets the value of the returnConvertedPolicyDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnConvertedPolicyDetail(Boolean value) {
        this.returnConvertedPolicyDetail = value;
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
