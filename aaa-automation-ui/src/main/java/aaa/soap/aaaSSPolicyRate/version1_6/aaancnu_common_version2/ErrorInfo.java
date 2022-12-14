
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ErrorInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errorTimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="severity" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ErrorSeverity" minOccurs="0"/&gt;
 *         &lt;element name="errorMessageText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="friendlyErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vendorMessageText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stackTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sqlState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sourceSystem" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RuleResponse" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}RuleResponse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="errorInfoExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorInfo", propOrder = {
    "errorTimeStamp",
    "errorCode",
    "severity",
    "errorMessageText",
    "friendlyErrorMessage",
    "vendorMessageText",
    "stackTrace",
    "sqlState",
    "serviceName",
    "sourceSystem",
    "ruleResponse",
    "errorInfoExtension"
})
public class    ErrorInfo {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar errorTimeStamp;
    protected String errorCode;
    @XmlSchemaType(name = "string")
    protected ErrorSeverity severity;
    protected String errorMessageText;
    protected String friendlyErrorMessage;
    protected String vendorMessageText;
    protected String stackTrace;
    protected String sqlState;
    @XmlElement(required = true)
    protected String serviceName;
    @XmlElement(required = true)
    protected String sourceSystem;
    @XmlElement(name = "RuleResponse")
    protected List<RuleResponse> ruleResponse;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> errorInfoExtension;

    /**
     * Gets the value of the errorTimeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getErrorTimeStamp() {
        return errorTimeStamp;
    }

    /**
     * Sets the value of the errorTimeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setErrorTimeStamp(XMLGregorianCalendar value) {
        this.errorTimeStamp = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorSeverity }
     *     
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorSeverity }
     *     
     */
    public void setSeverity(ErrorSeverity value) {
        this.severity = value;
    }

    /**
     * Gets the value of the errorMessageText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessageText() {
        return errorMessageText;
    }

    /**
     * Sets the value of the errorMessageText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessageText(String value) {
        this.errorMessageText = value;
    }

    /**
     * Gets the value of the friendlyErrorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFriendlyErrorMessage() {
        return friendlyErrorMessage;
    }

    /**
     * Sets the value of the friendlyErrorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFriendlyErrorMessage(String value) {
        this.friendlyErrorMessage = value;
    }

    /**
     * Gets the value of the vendorMessageText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendorMessageText() {
        return vendorMessageText;
    }

    /**
     * Sets the value of the vendorMessageText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendorMessageText(String value) {
        this.vendorMessageText = value;
    }

    /**
     * Gets the value of the stackTrace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the value of the stackTrace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackTrace(String value) {
        this.stackTrace = value;
    }

    /**
     * Gets the value of the sqlState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSqlState() {
        return sqlState;
    }

    /**
     * Sets the value of the sqlState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSqlState(String value) {
        this.sqlState = value;
    }

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
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
     * Gets the value of the ruleResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleResponse }
     * 
     * 
     */
    public List<RuleResponse> getRuleResponse() {
        if (ruleResponse == null) {
            ruleResponse = new ArrayList<RuleResponse>();
        }
        return this.ruleResponse;
    }

    /**
     * Gets the value of the errorInfoExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorInfoExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorInfoExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getErrorInfoExtension() {
        if (errorInfoExtension == null) {
            errorInfoExtension = new ArrayList<ExtensionArea>();
        }
        return this.errorInfoExtension;
    }

}
