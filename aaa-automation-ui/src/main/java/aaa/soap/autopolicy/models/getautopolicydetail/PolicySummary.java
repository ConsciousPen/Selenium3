
package aaa.soap.autopolicy.models.getautopolicydetail;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PolicySummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicySummary">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyHeader">
 *       &lt;sequence>
 *         &lt;element name="districtOffice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conversionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="namedNonOwnerPolicy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="manualDebt" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="manualRenew" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="tier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="baseDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="policyDueDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="commisionIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="specialHandling" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="currentRevision" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="versionNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="revisionNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="pendingRevisionNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="referencedMembership" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="reinstatedDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="multiPolicy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="loyaltyDiscount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paymentDiscount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="advanceShoppingDiscount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="eValueInfo" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EValueHeader" minOccurs="0"/>
 *         &lt;element name="discounts" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Discounts" minOccurs="0"/>
 *         &lt;element name="baseYear" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="extn" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cityTax" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TaxFee" minOccurs="0"/>
 *         &lt;element name="countyTax" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TaxFee" minOccurs="0"/>
 *         &lt;element name="stateTax" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}TaxFee" minOccurs="0"/>
 *         &lt;element name="additionalInterests" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AdditionalInterests" minOccurs="0"/>
 *         &lt;element name="UBIDoctermsAcceptance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="coverages" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Coverages" minOccurs="0"/>
 *         &lt;element name="endorsementForms" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}EndorsementForms" minOccurs="0"/>
 *         &lt;element name="referencedPolicies" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}ReferencedPolicies" minOccurs="0"/>
 *         &lt;element name="payers" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PolicyPayers" minOccurs="0"/>
 *         &lt;element name="otherFee" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}SurchargeAndFee" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="convertedPolicy" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}ConvertedPolicy" minOccurs="0"/>
 *         &lt;element name="policySummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicySummary", propOrder = {
    "districtOffice",
    "productType",
    "countryCode",
    "conversionDate",
    "namedNonOwnerPolicy",
    "manualDebt",
    "manualRenew",
    "tier",
    "baseDate",
    "policyDueDate",
    "commisionIndicator",
    "specialHandling",
    "currentRevision",
    "versionNumber",
    "revisionNumber",
    "pendingRevisionNumber",
    "referencedMembership",
    "statusDate",
    "reinstatedDate",
    "multiPolicy",
    "loyaltyDiscount",
    "paymentDiscount",
    "advanceShoppingDiscount",
    "eValueInfo",
    "discounts",
    "baseYear",
    "extn",
    "cityTax",
    "countyTax",
    "stateTax",
    "additionalInterests",
    "ubiDoctermsAcceptance",
    "coverages",
    "endorsementForms",
    "referencedPolicies",
    "payers",
    "otherFee",
    "convertedPolicy",
    "policySummaryExtension"
})
public class PolicySummary
    extends PolicyHeader
{

    protected String districtOffice;
    protected String productType;
    protected String countryCode;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar conversionDate;
    protected Boolean namedNonOwnerPolicy;
    protected Boolean manualDebt;
    protected Boolean manualRenew;
    protected String tier;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar baseDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar policyDueDate;
    protected String commisionIndicator;
    protected Boolean specialHandling;
    protected Boolean currentRevision;
    protected BigInteger versionNumber;
    protected BigInteger revisionNumber;
    protected BigInteger pendingRevisionNumber;
    protected String referencedMembership;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar statusDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reinstatedDate;
    protected Boolean multiPolicy;
    protected Boolean loyaltyDiscount;
    protected Boolean paymentDiscount;
    protected Boolean advanceShoppingDiscount;
    protected EValueHeader eValueInfo;
    protected Discounts discounts;
    protected BigInteger baseYear;
    @XmlElement(nillable = true)
    protected List<Extn> extn;
    protected aaa.soap.autopolicy.models.getautopolicydetail.TaxFee cityTax;
    protected aaa.soap.autopolicy.models.getautopolicydetail.TaxFee countyTax;
    protected aaa.soap.autopolicy.models.getautopolicydetail.TaxFee stateTax;
    protected AdditionalInterests additionalInterests;
    @XmlElement(name = "UBIDoctermsAcceptance")
    protected String ubiDoctermsAcceptance;
    protected Coverages coverages;
    protected EndorsementForms endorsementForms;
    protected aaa.soap.autopolicy.models.getautopolicydetail.ReferencedPolicies referencedPolicies;
    protected PolicyPayers payers;
    @XmlElement(nillable = true)
    protected List<aaa.soap.autopolicy.models.getautopolicydetail.SurchargeAndFee> otherFee;
    protected ConvertedPolicy convertedPolicy;
    @XmlElement(nillable = true)
    protected List<Object> policySummaryExtension;

    /**
     * Gets the value of the districtOffice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrictOffice() {
        return districtOffice;
    }

    /**
     * Sets the value of the districtOffice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrictOffice(String value) {
        this.districtOffice = value;
    }

    /**
     * Gets the value of the productType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the value of the productType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductType(String value) {
        this.productType = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the conversionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getConversionDate() {
        return conversionDate;
    }

    /**
     * Sets the value of the conversionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setConversionDate(XMLGregorianCalendar value) {
        this.conversionDate = value;
    }

    /**
     * Gets the value of the namedNonOwnerPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNamedNonOwnerPolicy() {
        return namedNonOwnerPolicy;
    }

    /**
     * Sets the value of the namedNonOwnerPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNamedNonOwnerPolicy(Boolean value) {
        this.namedNonOwnerPolicy = value;
    }

    /**
     * Gets the value of the manualDebt property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isManualDebt() {
        return manualDebt;
    }

    /**
     * Sets the value of the manualDebt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setManualDebt(Boolean value) {
        this.manualDebt = value;
    }

    /**
     * Gets the value of the manualRenew property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isManualRenew() {
        return manualRenew;
    }

    /**
     * Sets the value of the manualRenew property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setManualRenew(Boolean value) {
        this.manualRenew = value;
    }

    /**
     * Gets the value of the tier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTier() {
        return tier;
    }

    /**
     * Sets the value of the tier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTier(String value) {
        this.tier = value;
    }

    /**
     * Gets the value of the baseDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBaseDate() {
        return baseDate;
    }

    /**
     * Sets the value of the baseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBaseDate(XMLGregorianCalendar value) {
        this.baseDate = value;
    }

    /**
     * Gets the value of the policyDueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPolicyDueDate() {
        return policyDueDate;
    }

    /**
     * Sets the value of the policyDueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPolicyDueDate(XMLGregorianCalendar value) {
        this.policyDueDate = value;
    }

    /**
     * Gets the value of the commisionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommisionIndicator() {
        return commisionIndicator;
    }

    /**
     * Sets the value of the commisionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommisionIndicator(String value) {
        this.commisionIndicator = value;
    }

    /**
     * Gets the value of the specialHandling property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSpecialHandling() {
        return specialHandling;
    }

    /**
     * Sets the value of the specialHandling property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSpecialHandling(Boolean value) {
        this.specialHandling = value;
    }

    /**
     * Gets the value of the currentRevision property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCurrentRevision() {
        return currentRevision;
    }

    /**
     * Sets the value of the currentRevision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCurrentRevision(Boolean value) {
        this.currentRevision = value;
    }

    /**
     * Gets the value of the versionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the value of the versionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersionNumber(BigInteger value) {
        this.versionNumber = value;
    }

    /**
     * Gets the value of the revisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Sets the value of the revisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRevisionNumber(BigInteger value) {
        this.revisionNumber = value;
    }

    /**
     * Gets the value of the pendingRevisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPendingRevisionNumber() {
        return pendingRevisionNumber;
    }

    /**
     * Sets the value of the pendingRevisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPendingRevisionNumber(BigInteger value) {
        this.pendingRevisionNumber = value;
    }

    /**
     * Gets the value of the referencedMembership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedMembership() {
        return referencedMembership;
    }

    /**
     * Sets the value of the referencedMembership property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedMembership(String value) {
        this.referencedMembership = value;
    }

    /**
     * Gets the value of the statusDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the value of the statusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStatusDate(XMLGregorianCalendar value) {
        this.statusDate = value;
    }

    /**
     * Gets the value of the reinstatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReinstatedDate() {
        return reinstatedDate;
    }

    /**
     * Sets the value of the reinstatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReinstatedDate(XMLGregorianCalendar value) {
        this.reinstatedDate = value;
    }

    /**
     * Gets the value of the multiPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiPolicy() {
        return multiPolicy;
    }

    /**
     * Sets the value of the multiPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiPolicy(Boolean value) {
        this.multiPolicy = value;
    }

    /**
     * Gets the value of the loyaltyDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    /**
     * Sets the value of the loyaltyDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLoyaltyDiscount(Boolean value) {
        this.loyaltyDiscount = value;
    }

    /**
     * Gets the value of the paymentDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaymentDiscount() {
        return paymentDiscount;
    }

    /**
     * Sets the value of the paymentDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaymentDiscount(Boolean value) {
        this.paymentDiscount = value;
    }

    /**
     * Gets the value of the advanceShoppingDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdvanceShoppingDiscount() {
        return advanceShoppingDiscount;
    }

    /**
     * Sets the value of the advanceShoppingDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdvanceShoppingDiscount(Boolean value) {
        this.advanceShoppingDiscount = value;
    }

    /**
     * Gets the value of the eValueInfo property.
     * 
     * @return
     *     possible object is
     *     {@link EValueHeader }
     *     
     */
    public aaa.soap.autopolicy.models.getautopolicydetail.EValueHeader getEValueInfo() {
        return eValueInfo;
    }

    /**
     * Sets the value of the eValueInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link EValueHeader }
     *     
     */
    public void setEValueInfo(aaa.soap.autopolicy.models.getautopolicydetail.EValueHeader value) {
        this.eValueInfo = value;
    }

    /**
     * Gets the value of the discounts property.
     * 
     * @return
     *     possible object is
     *     {@link Discounts }
     *     
     */
    public Discounts getDiscounts() {
        return discounts;
    }

    /**
     * Sets the value of the discounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Discounts }
     *     
     */
    public void setDiscounts(Discounts value) {
        this.discounts = value;
    }

    /**
     * Gets the value of the baseYear property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBaseYear() {
        return baseYear;
    }

    /**
     * Sets the value of the baseYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBaseYear(BigInteger value) {
        this.baseYear = value;
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
    public List<aaa.soap.autopolicy.models.getautopolicydetail.Extn> getExtn() {
        if (extn == null) {
            extn = new ArrayList<aaa.soap.autopolicy.models.getautopolicydetail.Extn>();
        }
        return this.extn;
    }

    /**
     * Gets the value of the cityTax property.
     * 
     * @return
     *     possible object is
     *     {@link TaxFee }
     *     
     */
    public aaa.soap.autopolicy.models.getautopolicydetail.TaxFee getCityTax() {
        return cityTax;
    }

    /**
     * Sets the value of the cityTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxFee }
     *     
     */
    public void setCityTax(aaa.soap.autopolicy.models.getautopolicydetail.TaxFee value) {
        this.cityTax = value;
    }

    /**
     * Gets the value of the countyTax property.
     * 
     * @return
     *     possible object is
     *     {@link TaxFee }
     *     
     */
    public aaa.soap.autopolicy.models.getautopolicydetail.TaxFee getCountyTax() {
        return countyTax;
    }

    /**
     * Sets the value of the countyTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxFee }
     *     
     */
    public void setCountyTax(aaa.soap.autopolicy.models.getautopolicydetail.TaxFee value) {
        this.countyTax = value;
    }

    /**
     * Gets the value of the stateTax property.
     * 
     * @return
     *     possible object is
     *     {@link TaxFee }
     *     
     */
    public aaa.soap.autopolicy.models.getautopolicydetail.TaxFee getStateTax() {
        return stateTax;
    }

    /**
     * Sets the value of the stateTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxFee }
     *     
     */
    public void setStateTax(aaa.soap.autopolicy.models.getautopolicydetail.TaxFee value) {
        this.stateTax = value;
    }

    /**
     * Gets the value of the additionalInterests property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalInterests }
     *     
     */
    public AdditionalInterests getAdditionalInterests() {
        return additionalInterests;
    }

    /**
     * Sets the value of the additionalInterests property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalInterests }
     *     
     */
    public void setAdditionalInterests(AdditionalInterests value) {
        this.additionalInterests = value;
    }

    /**
     * Gets the value of the ubiDoctermsAcceptance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUBIDoctermsAcceptance() {
        return ubiDoctermsAcceptance;
    }

    /**
     * Sets the value of the ubiDoctermsAcceptance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUBIDoctermsAcceptance(String value) {
        this.ubiDoctermsAcceptance = value;
    }

    /**
     * Gets the value of the coverages property.
     * 
     * @return
     *     possible object is
     *     {@link Coverages }
     *     
     */
    public Coverages getCoverages() {
        return coverages;
    }

    /**
     * Sets the value of the coverages property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coverages }
     *     
     */
    public void setCoverages(Coverages value) {
        this.coverages = value;
    }

    /**
     * Gets the value of the endorsementForms property.
     * 
     * @return
     *     possible object is
     *     {@link EndorsementForms }
     *     
     */
    public EndorsementForms getEndorsementForms() {
        return endorsementForms;
    }

    /**
     * Sets the value of the endorsementForms property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndorsementForms }
     *     
     */
    public void setEndorsementForms(EndorsementForms value) {
        this.endorsementForms = value;
    }

    /**
     * Gets the value of the referencedPolicies property.
     * 
     * @return
     *     possible object is
     *     {@link ReferencedPolicies }
     *     
     */
    public aaa.soap.autopolicy.models.getautopolicydetail.ReferencedPolicies getReferencedPolicies() {
        return referencedPolicies;
    }

    /**
     * Sets the value of the referencedPolicies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferencedPolicies }
     *     
     */
    public void setReferencedPolicies(aaa.soap.autopolicy.models.getautopolicydetail.ReferencedPolicies value) {
        this.referencedPolicies = value;
    }

    /**
     * Gets the value of the payers property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyPayers }
     *     
     */
    public PolicyPayers getPayers() {
        return payers;
    }

    /**
     * Sets the value of the payers property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyPayers }
     *     
     */
    public void setPayers(PolicyPayers value) {
        this.payers = value;
    }

    /**
     * Gets the value of the otherFee property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherFee property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherFee().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SurchargeAndFee }
     * 
     * 
     */
    public List<aaa.soap.autopolicy.models.getautopolicydetail.SurchargeAndFee> getOtherFee() {
        if (otherFee == null) {
            otherFee = new ArrayList<aaa.soap.autopolicy.models.getautopolicydetail.SurchargeAndFee>();
        }
        return this.otherFee;
    }

    /**
     * Gets the value of the convertedPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link ConvertedPolicy }
     *     
     */
    public ConvertedPolicy getConvertedPolicy() {
        return convertedPolicy;
    }

    /**
     * Sets the value of the convertedPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConvertedPolicy }
     *     
     */
    public void setConvertedPolicy(ConvertedPolicy value) {
        this.convertedPolicy = value;
    }

    /**
     * Gets the value of the policySummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policySummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicySummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPolicySummaryExtension() {
        if (policySummaryExtension == null) {
            policySummaryExtension = new ArrayList<Object>();
        }
        return this.policySummaryExtension;
    }

}
