
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AgentSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AgentSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="channelType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isSweepEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="agency" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}OrganizationHeader" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}OrganizationHeader" minOccurs="0"/>
 *         &lt;element name="workArea" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AgentWorkArea" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contactNumber" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PreferenceTelephone" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pendingTraining" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Training" minOccurs="0"/>
 *         &lt;element name="extn" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="agentSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgentSummary", propOrder = {
    "agentIdentifier",
    "firstName",
    "lastName",
    "fullName",
    "emailAddress",
    "channelType",
    "agentType",
    "status",
    "isSweepEnabled",
    "address",
    "agency",
    "location",
    "workArea",
    "contactNumber",
    "pendingTraining",
    "extn",
    "agentSummaryExtension"
})
public class AgentSummary {

    protected String agentIdentifier;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String emailAddress;
    protected String channelType;
    protected String agentType;
    protected String status;
    protected Boolean isSweepEnabled;
    @XmlElement(nillable = true)
    protected List<PostalAddressSummary> address;
    protected OrganizationHeader agency;
    protected OrganizationHeader location;
    @XmlElement(nillable = true)
    protected List<AgentWorkArea> workArea;
    @XmlElement(nillable = true)
    protected List<PreferenceTelephone> contactNumber;
    protected Training pendingTraining;
    @XmlElement(nillable = true)
    protected List<Extn> extn;
    @XmlElement(nillable = true)
    protected List<Object> agentSummaryExtension;

    /**
     * Gets the value of the agentIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentIdentifier() {
        return agentIdentifier;
    }

    /**
     * Sets the value of the agentIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentIdentifier(String value) {
        this.agentIdentifier = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the channelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannelType() {
        return channelType;
    }

    /**
     * Sets the value of the channelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannelType(String value) {
        this.channelType = value;
    }

    /**
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentType(String value) {
        this.agentType = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the isSweepEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSweepEnabled() {
        return isSweepEnabled;
    }

    /**
     * Sets the value of the isSweepEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSweepEnabled(Boolean value) {
        this.isSweepEnabled = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the address property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PostalAddressSummary }
     * 
     * 
     */
    public List<PostalAddressSummary> getAddress() {
        if (address == null) {
            address = new ArrayList<PostalAddressSummary>();
        }
        return this.address;
    }

    /**
     * Gets the value of the agency property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationHeader }
     *     
     */
    public OrganizationHeader getAgency() {
        return agency;
    }

    /**
     * Sets the value of the agency property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationHeader }
     *     
     */
    public void setAgency(OrganizationHeader value) {
        this.agency = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationHeader }
     *     
     */
    public OrganizationHeader getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationHeader }
     *     
     */
    public void setLocation(OrganizationHeader value) {
        this.location = value;
    }

    /**
     * Gets the value of the workArea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the workArea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorkArea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AgentWorkArea }
     * 
     * 
     */
    public List<AgentWorkArea> getWorkArea() {
        if (workArea == null) {
            workArea = new ArrayList<AgentWorkArea>();
        }
        return this.workArea;
    }

    /**
     * Gets the value of the contactNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contactNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContactNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreferenceTelephone }
     * 
     * 
     */
    public List<PreferenceTelephone> getContactNumber() {
        if (contactNumber == null) {
            contactNumber = new ArrayList<PreferenceTelephone>();
        }
        return this.contactNumber;
    }

    /**
     * Gets the value of the pendingTraining property.
     * 
     * @return
     *     possible object is
     *     {@link Training }
     *     
     */
    public Training getPendingTraining() {
        return pendingTraining;
    }

    /**
     * Sets the value of the pendingTraining property.
     * 
     * @param value
     *     allowed object is
     *     {@link Training }
     *     
     */
    public void setPendingTraining(Training value) {
        this.pendingTraining = value;
    }

    /**
     * Gets the value of the extn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extn }
     * 
     * 
     */
    public List<Extn> getExtn() {
        if (extn == null) {
            extn = new ArrayList<Extn>();
        }
        return this.extn;
    }

    /**
     * Gets the value of the agentSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agentSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAgentSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAgentSummaryExtension() {
        if (agentSummaryExtension == null) {
            agentSummaryExtension = new ArrayList<Object>();
        }
        return this.agentSummaryExtension;
    }

}
