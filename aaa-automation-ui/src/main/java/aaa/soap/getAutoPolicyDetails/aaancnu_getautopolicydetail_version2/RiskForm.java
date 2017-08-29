
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

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
 * <p>Java class for RiskForm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiskForm">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="formIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="caseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="originationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalReferenceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endorsementFormPremium" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PremiumEntry" minOccurs="0"/>
 *         &lt;element name="parameters" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}FormParameters" minOccurs="0"/>
 *         &lt;element name="limit" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AvailableLimit" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="riskFormExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiskForm", propOrder = {
    "formIdentifier",
    "sequenceNumber",
    "caseNumber",
    "filingDate",
    "expirationDate",
    "originationCode",
    "externalReferenceNumber",
    "effectiveDate",
    "description",
    "endorsementFormPremium",
    "parameters",
    "limit",
    "riskFormExtension"
})
public class RiskForm {

    protected String formIdentifier;
    protected BigInteger sequenceNumber;
    protected String caseNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar filingDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expirationDate;
    protected String originationCode;
    protected String externalReferenceNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effectiveDate;
    protected String description;
    protected PremiumEntry endorsementFormPremium;
    protected FormParameters parameters;
    @XmlElement(nillable = true)
    protected List<AvailableLimit> limit;
    @XmlElement(nillable = true)
    protected List<Object> riskFormExtension;

    /**
     * Gets the value of the formIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormIdentifier() {
        return formIdentifier;
    }

    /**
     * Sets the value of the formIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormIdentifier(String value) {
        this.formIdentifier = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the caseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * Sets the value of the caseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseNumber(String value) {
        this.caseNumber = value;
    }

    /**
     * Gets the value of the filingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFilingDate() {
        return filingDate;
    }

    /**
     * Sets the value of the filingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFilingDate(XMLGregorianCalendar value) {
        this.filingDate = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpirationDate(XMLGregorianCalendar value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the originationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginationCode() {
        return originationCode;
    }

    /**
     * Sets the value of the originationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginationCode(String value) {
        this.originationCode = value;
    }

    /**
     * Gets the value of the externalReferenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalReferenceNumber() {
        return externalReferenceNumber;
    }

    /**
     * Sets the value of the externalReferenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalReferenceNumber(String value) {
        this.externalReferenceNumber = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveDate(XMLGregorianCalendar value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the endorsementFormPremium property.
     * 
     * @return
     *     possible object is
     *     {@link PremiumEntry }
     *     
     */
    public PremiumEntry getEndorsementFormPremium() {
        return endorsementFormPremium;
    }

    /**
     * Sets the value of the endorsementFormPremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link PremiumEntry }
     *     
     */
    public void setEndorsementFormPremium(PremiumEntry value) {
        this.endorsementFormPremium = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link FormParameters }
     *     
     */
    public FormParameters getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormParameters }
     *     
     */
    public void setParameters(FormParameters value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the limit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLimit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AvailableLimit }
     * 
     * 
     */
    public List<AvailableLimit> getLimit() {
        if (limit == null) {
            limit = new ArrayList<AvailableLimit>();
        }
        return this.limit;
    }

    /**
     * Gets the value of the riskFormExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the riskFormExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRiskFormExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRiskFormExtension() {
        if (riskFormExtension == null) {
            riskFormExtension = new ArrayList<Object>();
        }
        return this.riskFormExtension;
    }

}
