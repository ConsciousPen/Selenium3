
package aaa.soap.autopolicy.models.aaancnu_getautopolicydetail_version2;

import java.math.BigDecimal;
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
 * <p>Java class for DriverAccident complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DriverAccident">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claimedCoverageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accidentIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accidentDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="atFault" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="claimNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accidentPoints" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="lossAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="ratingIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverAccidentExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DriverAccident", propOrder = {
    "claimedCoverageType",
    "accidentIdentifier",
    "accidentDate",
    "atFault",
    "claimNumber",
    "accidentPoints",
    "sequenceNumber",
    "lossAmount",
    "ratingIndicator",
    "type",
    "source",
    "driverAccidentExtension"
})
public class DriverAccident {

    protected String claimedCoverageType;
    protected String accidentIdentifier;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar accidentDate;
    protected Boolean atFault;
    protected String claimNumber;
    protected BigInteger accidentPoints;
    protected BigInteger sequenceNumber;
    protected BigDecimal lossAmount;
    protected Boolean ratingIndicator;
    protected String type;
    protected String source;
    @XmlElement(nillable = true)
    protected List<Object> driverAccidentExtension;

    /**
     * Gets the value of the claimedCoverageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimedCoverageType() {
        return claimedCoverageType;
    }

    /**
     * Sets the value of the claimedCoverageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimedCoverageType(String value) {
        this.claimedCoverageType = value;
    }

    /**
     * Gets the value of the accidentIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccidentIdentifier() {
        return accidentIdentifier;
    }

    /**
     * Sets the value of the accidentIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccidentIdentifier(String value) {
        this.accidentIdentifier = value;
    }

    /**
     * Gets the value of the accidentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAccidentDate() {
        return accidentDate;
    }

    /**
     * Sets the value of the accidentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAccidentDate(XMLGregorianCalendar value) {
        this.accidentDate = value;
    }

    /**
     * Gets the value of the atFault property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAtFault() {
        return atFault;
    }

    /**
     * Sets the value of the atFault property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAtFault(Boolean value) {
        this.atFault = value;
    }

    /**
     * Gets the value of the claimNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimNumber() {
        return claimNumber;
    }

    /**
     * Sets the value of the claimNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimNumber(String value) {
        this.claimNumber = value;
    }

    /**
     * Gets the value of the accidentPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAccidentPoints() {
        return accidentPoints;
    }

    /**
     * Sets the value of the accidentPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAccidentPoints(BigInteger value) {
        this.accidentPoints = value;
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
     * Gets the value of the lossAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    /**
     * Sets the value of the lossAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLossAmount(BigDecimal value) {
        this.lossAmount = value;
    }

    /**
     * Gets the value of the ratingIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRatingIndicator() {
        return ratingIndicator;
    }

    /**
     * Sets the value of the ratingIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRatingIndicator(Boolean value) {
        this.ratingIndicator = value;
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
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the driverAccidentExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driverAccidentExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriverAccidentExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDriverAccidentExtension() {
        if (driverAccidentExtension == null) {
            driverAccidentExtension = new ArrayList<Object>();
        }
        return this.driverAccidentExtension;
    }

}
