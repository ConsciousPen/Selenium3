
package aaa.soap.autopolicy.models.getautopolicydetail;


import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PolicyHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policyIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lineOfBusiness" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="policyPrefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="policyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riskState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="termEffectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="termExpirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="termAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusReasonDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusReasonDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="writingCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="renewalFlag" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="convertedRenewalOffer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="inceptionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="dataSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="months" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="quoteId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PostalAddressHeader" minOccurs="0"/>
 *         &lt;element name="insurerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agent" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyAgent" minOccurs="0"/>
 *         &lt;element name="insureds" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyInsuredSummaries" minOccurs="0"/>
 *         &lt;element name="policyHeaderExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyHeader", propOrder = {
        "address",
        "agent",
        "convertedRenewalOffer",
        "customerNumber",
        "dataSource",
        "fullTermAmount",
        "inceptionDate",
        "insureds",
        "insurerName",
        "lastUpdated",
        "lineOfBusiness",
        "months",
        "policyHeaderExtension",
        "policyIdentifier",
        "policyNumber",
        "policyPrefix",
        "productCode",
        "quoteId",
        "renewalFlag",
        "riskState",
        "status",
        "statusDescription",
        "statusReason",
        "statusReasonDate",
        "statusReasonDescription",
        "termAmount",
        "termEffectiveDate",
        "termExpirationDate",
        "type",
        "writingCompany"
})
@XmlSeeAlso({
    PolicySummary.class
})
public class PolicyHeader {

    protected String policyIdentifier;
    protected String lineOfBusiness;
    protected String productCode;
    protected String policyPrefix;
    protected String policyNumber;
    protected String riskState;
    protected String type;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar termEffectiveDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar termExpirationDate;
    protected BigDecimal termAmount;
    protected BigDecimal fullTermAmount;
    protected String status;
    protected String statusDescription;
    protected String statusReason;
    protected String statusReasonDescription;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar statusReasonDate;
    protected String writingCompany;
    protected Boolean renewalFlag;
    @XmlElement(defaultValue = "false")
    protected Boolean convertedRenewalOffer;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar inceptionDate;
    protected String dataSource;
    protected BigInteger months;
    protected String quoteId;
    protected String customerNumber;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    protected PostalAddressHeader address;
    protected String insurerName;
    protected PolicyAgent agent;
    protected PolicyInsuredSummaries insureds;
    @XmlElement(nillable = true)
    protected List<Object> policyHeaderExtension;

    /**
     * Gets the value of the policyIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyIdentifier() {
        return policyIdentifier;
    }

    /**
     * Sets the value of the policyIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyIdentifier(String value) {
        this.policyIdentifier = value;
    }

    /**
     * Gets the value of the lineOfBusiness property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    /**
     * Sets the value of the lineOfBusiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineOfBusiness(String value) {
        this.lineOfBusiness = value;
    }

    /**
     * Gets the value of the productCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * Sets the value of the productCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCode(String value) {
        this.productCode = value;
    }

    /**
     * Gets the value of the policyPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyPrefix() {
        return policyPrefix;
    }

    /**
     * Sets the value of the policyPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyPrefix(String value) {
        this.policyPrefix = value;
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
     * Gets the value of the riskState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskState() {
        return riskState;
    }

    /**
     * Sets the value of the riskState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskState(String value) {
        this.riskState = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the termEffectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTermEffectiveDate() {
        return termEffectiveDate;
    }

    /**
     * Sets the value of the termEffectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTermEffectiveDate(XMLGregorianCalendar value) {
        this.termEffectiveDate = value;
    }

    /**
     * Gets the value of the termExpirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTermExpirationDate() {
        return termExpirationDate;
    }

    /**
     * Sets the value of the termExpirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTermExpirationDate(XMLGregorianCalendar value) {
        this.termExpirationDate = value;
    }

    /**
     * Gets the value of the termAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTermAmount() {
        return termAmount;
    }

    /**
     * Sets the value of the termAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTermAmount(BigDecimal value) {
        this.termAmount = value;
    }
    /**
     * Gets the value of the fullTermAmount property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getfullTermAmount() {
        return fullTermAmount;
    }
    /**
     * Sets the value of the fullTermAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setfullTermAmount(BigDecimal value) {
        this.fullTermAmount = value;
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
     * Gets the value of the statusDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * Sets the value of the statusDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusDescription(String value) {
        this.statusDescription = value;
    }

    /**
     * Gets the value of the statusReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusReason() {
        return statusReason;
    }

    /**
     * Sets the value of the statusReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusReason(String value) {
        this.statusReason = value;
    }

    /**
     * Gets the value of the statusReasonDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusReasonDescription() {
        return statusReasonDescription;
    }

    /**
     * Sets the value of the statusReasonDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusReasonDescription(String value) {
        this.statusReasonDescription = value;
    }

    /**
     * Gets the value of the statusReasonDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStatusReasonDate() {
        return statusReasonDate;
    }

    /**
     * Sets the value of the statusReasonDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStatusReasonDate(XMLGregorianCalendar value) {
        this.statusReasonDate = value;
    }

    /**
     * Gets the value of the writingCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWritingCompany() {
        return writingCompany;
    }

    /**
     * Sets the value of the writingCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWritingCompany(String value) {
        this.writingCompany = value;
    }

    /**
     * Gets the value of the renewalFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRenewalFlag() {
        return renewalFlag;
    }

    /**
     * Sets the value of the renewalFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRenewalFlag(Boolean value) {
        this.renewalFlag = value;
    }

    /**
     * Gets the value of the convertedRenewalOffer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConvertedRenewalOffer() {
        return convertedRenewalOffer;
    }

    /**
     * Sets the value of the convertedRenewalOffer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConvertedRenewalOffer(Boolean value) {
        this.convertedRenewalOffer = value;
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
     * Gets the value of the dataSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of the dataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataSource(String value) {
        this.dataSource = value;
    }

    /**
     * Gets the value of the months property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMonths() {
        return months;
    }

    /**
     * Sets the value of the months property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMonths(BigInteger value) {
        this.months = value;
    }

    /**
     * Gets the value of the quoteId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * Sets the value of the quoteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteId(String value) {
        this.quoteId = value;
    }

    /**
     * Gets the value of the customerNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the value of the customerNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerNumber(String value) {
        this.customerNumber = value;
    }

    /**
     * Gets the value of the lastUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the value of the lastUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastUpdated(XMLGregorianCalendar value) {
        this.lastUpdated = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressHeader }
     *     
     */
    public PostalAddressHeader getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressHeader }
     *     
     */
    public void setAddress(PostalAddressHeader value) {
        this.address = value;
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
     * Gets the value of the agent property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyAgent }
     *     
     */
    public PolicyAgent getAgent() {
        return agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyAgent }
     *     
     */
    public void setAgent(PolicyAgent value) {
        this.agent = value;
    }

    /**
     * Gets the value of the insureds property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyInsuredSummaries }
     *     
     */
    public PolicyInsuredSummaries getInsureds() {
        return insureds;
    }

    /**
     * Sets the value of the insureds property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyInsuredSummaries }
     *     
     */
    public void setInsureds(PolicyInsuredSummaries value) {
        this.insureds = value;
    }

    /**
     * Gets the value of the policyHeaderExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyHeaderExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyHeaderExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicyHeaderExtension() {
        if (policyHeaderExtension == null) {
            policyHeaderExtension = new ArrayList<Object>();
        }
        return this.policyHeaderExtension;
    }

}
