
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for AAAOtherOrPriorPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAOtherOrPriorPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="continuousMonthsInsured" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="insurerCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="insurerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="monthsWithInsurer" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="daysLapsed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="inceptionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="policyExpDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="priorBILimits" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAPriorBILimit" minOccurs="0"/&gt;
 *         &lt;element name="overridePrefilledCarrier" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="agentEnteredCarrier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="enteredMonthsWithInsurer" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="enteredDaysLapsed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="agentEnteredInceptionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="agentEnteredPolicyExpDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="agentEnteredPolicyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="enteredPriorBILimits" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}AAAPriorBILimit" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAOtherOrPriorPolicy", propOrder = {
    "continuousMonthsInsured",
    "insurerCd",
    "insurerName",
    "monthsWithInsurer",
    "daysLapsed",
    "inceptionDate",
    "policyExpDate",
    "policyNumber",
    "priorBILimits",
    "overridePrefilledCarrier",
    "agentEnteredCarrier",
    "enteredMonthsWithInsurer",
    "enteredDaysLapsed",
    "agentEnteredInceptionDate",
    "agentEnteredPolicyExpDate",
    "agentEnteredPolicyNumber",
    "enteredPriorBILimits"
})
public class AAAOtherOrPriorPolicy {

    protected Boolean continuousMonthsInsured;
    @XmlElement(required = true)
    protected String insurerCd;
    protected String insurerName;
    protected BigDecimal monthsWithInsurer;
    protected BigDecimal daysLapsed;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar inceptionDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar policyExpDate;
    protected String policyNumber;
    protected String priorBILimits;
    @XmlElement(defaultValue = "false")
    protected Boolean overridePrefilledCarrier;
    protected String agentEnteredCarrier;
    protected BigDecimal enteredMonthsWithInsurer;
    protected BigDecimal enteredDaysLapsed;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar agentEnteredInceptionDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar agentEnteredPolicyExpDate;
    protected String agentEnteredPolicyNumber;
    protected String enteredPriorBILimits;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the continuousMonthsInsured property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isContinuousMonthsInsured() {
        return continuousMonthsInsured;
    }

    /**
     * Sets the value of the continuousMonthsInsured property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setContinuousMonthsInsured(Boolean value) {
        this.continuousMonthsInsured = value;
    }

    /**
     * Gets the value of the insurerCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerCd() {
        return insurerCd;
    }

    /**
     * Sets the value of the insurerCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerCd(String value) {
        this.insurerCd = value;
    }

    /**
     * Gets the value of the insurerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerName() {
        return insurerName;
    }

    /**
     * Sets the value of the insurerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerName(String value) {
        this.insurerName = value;
    }

    /**
     * Gets the value of the monthsWithInsurer property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMonthsWithInsurer() {
        return monthsWithInsurer;
    }

    /**
     * Sets the value of the monthsWithInsurer property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMonthsWithInsurer(BigDecimal value) {
        this.monthsWithInsurer = value;
    }

    /**
     * Gets the value of the daysLapsed property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDaysLapsed() {
        return daysLapsed;
    }

    /**
     * Sets the value of the daysLapsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDaysLapsed(BigDecimal value) {
        this.daysLapsed = value;
    }

    /**
     * Gets the value of the inceptionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInceptionDate() {
        return inceptionDate;
    }

    /**
     * Sets the value of the inceptionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInceptionDate(XMLGregorianCalendar value) {
        this.inceptionDate = value;
    }

    /**
     * Gets the value of the policyExpDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPolicyExpDate() {
        return policyExpDate;
    }

    /**
     * Sets the value of the policyExpDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPolicyExpDate(XMLGregorianCalendar value) {
        this.policyExpDate = value;
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
     * Gets the value of the priorBILimits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriorBILimits() {
        return priorBILimits;
    }

    /**
     * Sets the value of the priorBILimits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorBILimits(String value) {
        this.priorBILimits = value;
    }

    /**
     * Gets the value of the overridePrefilledCarrier property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOverridePrefilledCarrier() {
        return overridePrefilledCarrier;
    }

    /**
     * Sets the value of the overridePrefilledCarrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOverridePrefilledCarrier(Boolean value) {
        this.overridePrefilledCarrier = value;
    }

    /**
     * Gets the value of the agentEnteredCarrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentEnteredCarrier() {
        return agentEnteredCarrier;
    }

    /**
     * Sets the value of the agentEnteredCarrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentEnteredCarrier(String value) {
        this.agentEnteredCarrier = value;
    }

    /**
     * Gets the value of the enteredMonthsWithInsurer property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEnteredMonthsWithInsurer() {
        return enteredMonthsWithInsurer;
    }

    /**
     * Sets the value of the enteredMonthsWithInsurer property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEnteredMonthsWithInsurer(BigDecimal value) {
        this.enteredMonthsWithInsurer = value;
    }

    /**
     * Gets the value of the enteredDaysLapsed property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEnteredDaysLapsed() {
        return enteredDaysLapsed;
    }

    /**
     * Sets the value of the enteredDaysLapsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEnteredDaysLapsed(BigDecimal value) {
        this.enteredDaysLapsed = value;
    }

    /**
     * Gets the value of the agentEnteredInceptionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAgentEnteredInceptionDate() {
        return agentEnteredInceptionDate;
    }

    /**
     * Sets the value of the agentEnteredInceptionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAgentEnteredInceptionDate(XMLGregorianCalendar value) {
        this.agentEnteredInceptionDate = value;
    }

    /**
     * Gets the value of the agentEnteredPolicyExpDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAgentEnteredPolicyExpDate() {
        return agentEnteredPolicyExpDate;
    }

    /**
     * Sets the value of the agentEnteredPolicyExpDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAgentEnteredPolicyExpDate(XMLGregorianCalendar value) {
        this.agentEnteredPolicyExpDate = value;
    }

    /**
     * Gets the value of the agentEnteredPolicyNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentEnteredPolicyNumber() {
        return agentEnteredPolicyNumber;
    }

    /**
     * Sets the value of the agentEnteredPolicyNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentEnteredPolicyNumber(String value) {
        this.agentEnteredPolicyNumber = value;
    }

    /**
     * Gets the value of the enteredPriorBILimits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnteredPriorBILimits() {
        return enteredPriorBILimits;
    }

    /**
     * Sets the value of the enteredPriorBILimits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnteredPriorBILimits(String value) {
        this.enteredPriorBILimits = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentState }
     *     
     */
    public ComponentState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentState }
     *     
     */
    public void setState(ComponentState value) {
        this.state = value;
    }

}
