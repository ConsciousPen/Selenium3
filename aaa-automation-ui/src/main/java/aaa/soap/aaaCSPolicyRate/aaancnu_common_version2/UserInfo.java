
package aaa.soap.aaaCSPolicyRate.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for UserInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userRID" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="requestID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="districtOfficeNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="financialLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="repID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="employeeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roles" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="roleNames" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="isPasswordExpired" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isLockedOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="authenticated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="userInfoExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserInfo", propOrder = {
    "userRID",
    "requestID",
    "firstName",
    "lastName",
    "eMailAddress",
    "telephoneNumber",
    "active",
    "districtOfficeNumber",
    "financialLocation",
    "repID",
    "employeeID",
    "roles",
    "roleNames",
    "isPasswordExpired",
    "isLockedOut",
    "authenticated",
    "userInfoExtension"
})
public class UserInfo {

    protected BigInteger userRID;
    protected String requestID;
    protected String firstName;
    protected String lastName;
    protected String eMailAddress;
    protected String telephoneNumber;
    protected Boolean active;
    @XmlElement(required = true)
    protected String districtOfficeNumber;
    protected String financialLocation;
    @XmlElement(required = true)
    protected String repID;
    protected String employeeID;
    @XmlElement(nillable = true)
    protected List<String> roles;
    @XmlElement(nillable = true)
    protected List<String> roleNames;
    @XmlElement(defaultValue = "false")
    protected boolean isPasswordExpired;
    @XmlElement(defaultValue = "false")
    protected boolean isLockedOut;
    @XmlElement(defaultValue = "false")
    protected boolean authenticated;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> userInfoExtension;

    /**
     * Gets the value of the userRID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserRID() {
        return userRID;
    }

    /**
     * Sets the value of the userRID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserRID(BigInteger value) {
        this.userRID = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
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
     * Gets the value of the eMailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMailAddress() {
        return eMailAddress;
    }

    /**
     * Sets the value of the eMailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMailAddress(String value) {
        this.eMailAddress = value;
    }

    /**
     * Gets the value of the telephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * Sets the value of the telephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephoneNumber(String value) {
        this.telephoneNumber = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the districtOfficeNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrictOfficeNumber() {
        return districtOfficeNumber;
    }

    /**
     * Sets the value of the districtOfficeNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrictOfficeNumber(String value) {
        this.districtOfficeNumber = value;
    }

    /**
     * Gets the value of the financialLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialLocation() {
        return financialLocation;
    }

    /**
     * Sets the value of the financialLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialLocation(String value) {
        this.financialLocation = value;
    }

    /**
     * Gets the value of the repID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepID() {
        return repID;
    }

    /**
     * Sets the value of the repID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepID(String value) {
        this.repID = value;
    }

    /**
     * Gets the value of the employeeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeID() {
        return employeeID;
    }

    /**
     * Sets the value of the employeeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeID(String value) {
        this.employeeID = value;
    }

    /**
     * Gets the value of the roles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return this.roles;
    }

    /**
     * Gets the value of the roleNames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roleNames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoleNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRoleNames() {
        if (roleNames == null) {
            roleNames = new ArrayList<String>();
        }
        return this.roleNames;
    }

    /**
     * Gets the value of the isPasswordExpired property.
     * 
     */
    public boolean isIsPasswordExpired() {
        return isPasswordExpired;
    }

    /**
     * Sets the value of the isPasswordExpired property.
     * 
     */
    public void setIsPasswordExpired(boolean value) {
        this.isPasswordExpired = value;
    }

    /**
     * Gets the value of the isLockedOut property.
     * 
     */
    public boolean isIsLockedOut() {
        return isLockedOut;
    }

    /**
     * Sets the value of the isLockedOut property.
     * 
     */
    public void setIsLockedOut(boolean value) {
        this.isLockedOut = value;
    }

    /**
     * Gets the value of the authenticated property.
     * 
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Sets the value of the authenticated property.
     * 
     */
    public void setAuthenticated(boolean value) {
        this.authenticated = value;
    }

    /**
     * Gets the value of the userInfoExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userInfoExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserInfoExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getUserInfoExtension() {
        if (userInfoExtension == null) {
            userInfoExtension = new ArrayList<ExtensionArea>();
        }
        return this.userInfoExtension;
    }

}
