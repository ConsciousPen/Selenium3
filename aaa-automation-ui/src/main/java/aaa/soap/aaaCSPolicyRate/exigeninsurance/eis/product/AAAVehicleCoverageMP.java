
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.ComponentState;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAAVehicleCoverageMP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAVehicleCoverageMP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="coverageCd" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}CoverageCode"/>
 *         &lt;element name="limitAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PremiumEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAVehicleCoverageMP", propOrder = {
    "coverageCd",
    "limitAmount",
    "premiumEntry"
})
public class AAAVehicleCoverageMP {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CoverageCode coverageCd;
    @XmlElement(required = true)
    protected String limitAmount;
    @XmlElement(name = "PremiumEntry")
    protected List<PremiumEntry> premiumEntry;
    @XmlAttribute(name = "oid")
    protected String oid;
    @XmlAttribute(name = "state")
    protected ComponentState state;

    /**
     * Gets the value of the coverageCd property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageCode }
     *     
     */
    public CoverageCode getCoverageCd() {
        return coverageCd;
    }

    /**
     * Sets the value of the coverageCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageCode }
     *     
     */
    public void setCoverageCd(CoverageCode value) {
        this.coverageCd = value;
    }

    /**
     * Gets the value of the limitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLimitAmount() {
        return limitAmount;
    }

    /**
     * Sets the value of the limitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLimitAmount(String value) {
        this.limitAmount = value;
    }

    /**
     * Gets the value of the premiumEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiumEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiumEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiumEntry }
     * 
     * 
     */
    public List<PremiumEntry> getPremiumEntry() {
        if (premiumEntry == null) {
            premiumEntry = new ArrayList<PremiumEntry>();
        }
        return this.premiumEntry;
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
