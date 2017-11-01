
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CoverageSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="coverageIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="coveragePriority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalLimit" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AvailableLimit" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extn" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="limit" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AvailableLimit" minOccurs="0"/>
 *         &lt;element name="deductible" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}AvailableDeductible" minOccurs="0"/>
 *         &lt;element name="coveragePremium" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}PremiumEntry" minOccurs="0"/>
 *         &lt;element name="coverageSummaryExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageSummary", propOrder = {
    "coverageIdentifier",
    "coveragePriority",
    "sequenceNumber",
    "description",
    "code",
    "additionalLimit",
    "extn",
    "limit",
    "deductible",
    "coveragePremium",
    "coverageSummaryExtension"
})
public class CoverageSummary {

    protected String coverageIdentifier;
    protected String coveragePriority;
    protected BigInteger sequenceNumber;
    protected String description;
    protected String code;
    @XmlElement(nillable = true)
    protected List<AvailableLimit> additionalLimit;
    @XmlElement(nillable = true)
    protected List<Extn> extn;
    protected AvailableLimit limit;
    protected AvailableDeductible deductible;
    protected PremiumEntry coveragePremium;
    @XmlElement(nillable = true)
    protected List<Object> coverageSummaryExtension;

    /**
     * Gets the value of the coverageIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageIdentifier() {
        return coverageIdentifier;
    }

    /**
     * Sets the value of the coverageIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageIdentifier(String value) {
        this.coverageIdentifier = value;
    }

    /**
     * Gets the value of the coveragePriority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoveragePriority() {
        return coveragePriority;
    }

    /**
     * Sets the value of the coveragePriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoveragePriority(String value) {
        this.coveragePriority = value;
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
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the additionalLimit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalLimit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalLimit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AvailableLimit }
     * 
     * 
     */
    public List<AvailableLimit> getAdditionalLimit() {
        if (additionalLimit == null) {
            additionalLimit = new ArrayList<AvailableLimit>();
        }
        return this.additionalLimit;
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
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableLimit }
     *     
     */
    public AvailableLimit getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableLimit }
     *     
     */
    public void setLimit(AvailableLimit value) {
        this.limit = value;
    }

    /**
     * Gets the value of the deductible property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableDeductible }
     *     
     */
    public AvailableDeductible getDeductible() {
        return deductible;
    }

    /**
     * Sets the value of the deductible property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableDeductible }
     *     
     */
    public void setDeductible(AvailableDeductible value) {
        this.deductible = value;
    }

    /**
     * Gets the value of the coveragePremium property.
     * 
     * @return
     *     possible object is
     *     {@link PremiumEntry }
     *     
     */
    public PremiumEntry getCoveragePremium() {
        return coveragePremium;
    }

    /**
     * Sets the value of the coveragePremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link PremiumEntry }
     *     
     */
    public void setCoveragePremium(PremiumEntry value) {
        this.coveragePremium = value;
    }

    /**
     * Gets the value of the coverageSummaryExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coverageSummaryExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoverageSummaryExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getCoverageSummaryExtension() {
        if (coverageSummaryExtension == null) {
            coverageSummaryExtension = new ArrayList<Object>();
        }
        return this.coverageSummaryExtension;
    }

}
