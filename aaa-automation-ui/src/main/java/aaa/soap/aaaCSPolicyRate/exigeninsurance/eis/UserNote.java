
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for UserNote complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserNote">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="confidential" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="updatedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="userText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserNote", propOrder = {
    "category",
    "confidential",
    "updatedOn",
    "userText"
})
public class UserNote {

    protected String category;
    protected Boolean confidential;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updatedOn;
    @XmlElement(required = true)
    protected String userText;

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Gets the value of the confidential property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConfidential() {
        return confidential;
    }

    /**
     * Sets the value of the confidential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConfidential(Boolean value) {
        this.confidential = value;
    }

    /**
     * Gets the value of the updatedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the value of the updatedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdatedOn(XMLGregorianCalendar value) {
        this.updatedOn = value;
    }

    /**
     * Gets the value of the userText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserText() {
        return userText;
    }

    /**
     * Sets the value of the userText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserText(String value) {
        this.userText = value;
    }

}
