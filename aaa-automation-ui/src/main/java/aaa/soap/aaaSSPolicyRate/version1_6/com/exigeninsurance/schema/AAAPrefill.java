
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.StateProvCd;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AAAPrefill complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAPrefill"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="riskStateCd" type="{http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0}StateProvCd"/&gt;
 *         &lt;element name="AAAPrefillVehicle" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPrefillVehicle" maxOccurs="8" minOccurs="0"/&gt;
 *         &lt;element name="AAAPrefillDriver" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPrefillDriver" maxOccurs="7" minOccurs="0"/&gt;
 *         &lt;element name="AAAPrefillAddress" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}AAAPrefillAddress"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAPrefill", propOrder = {
    "firstName",
    "middleName",
    "lastName",
    "birthDate",
    "riskStateCd",
    "aaaPrefillVehicle",
    "aaaPrefillDriver",
    "aaaPrefillAddress"
})
public class AAAPrefill {

    @XmlElement(required = true)
    protected String firstName;
    protected String middleName;
    @XmlElement(required = true)
    protected String lastName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StateProvCd riskStateCd;
    @XmlElement(name = "AAAPrefillVehicle")
    protected List<AAAPrefillVehicle> aaaPrefillVehicle;
    @XmlElement(name = "AAAPrefillDriver")
    protected List<AAAPrefillDriver> aaaPrefillDriver;
    @XmlElement(name = "AAAPrefillAddress", required = true)
    protected AAAPrefillAddress aaaPrefillAddress;

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
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
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
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the riskStateCd property.
     * 
     * @return
     *     possible object is
     *     {@link StateProvCd }
     *     
     */
    public StateProvCd getRiskStateCd() {
        return riskStateCd;
    }

    /**
     * Sets the value of the riskStateCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateProvCd }
     *     
     */
    public void setRiskStateCd(StateProvCd value) {
        this.riskStateCd = value;
    }

    /**
     * Gets the value of the aaaPrefillVehicle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaPrefillVehicle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAPrefillVehicle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAPrefillVehicle }
     * 
     * 
     */
    public List<AAAPrefillVehicle> getAAAPrefillVehicle() {
        if (aaaPrefillVehicle == null) {
            aaaPrefillVehicle = new ArrayList<AAAPrefillVehicle>();
        }
        return this.aaaPrefillVehicle;
    }

    /**
     * Gets the value of the aaaPrefillDriver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aaaPrefillDriver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAAAPrefillDriver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AAAPrefillDriver }
     * 
     * 
     */
    public List<AAAPrefillDriver> getAAAPrefillDriver() {
        if (aaaPrefillDriver == null) {
            aaaPrefillDriver = new ArrayList<AAAPrefillDriver>();
        }
        return this.aaaPrefillDriver;
    }

    /**
     * Gets the value of the aaaPrefillAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AAAPrefillAddress }
     *     
     */
    public AAAPrefillAddress getAAAPrefillAddress() {
        return aaaPrefillAddress;
    }

    /**
     * Sets the value of the aaaPrefillAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAAPrefillAddress }
     *     
     */
    public void setAAAPrefillAddress(AAAPrefillAddress value) {
        this.aaaPrefillAddress = value;
    }

}
