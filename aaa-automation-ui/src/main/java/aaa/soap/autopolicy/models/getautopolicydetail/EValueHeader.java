
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EValueHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EValueHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enrollmentStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optInIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="eValueHeaderExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EValueHeader", propOrder = {
    "enrollmentStatus",
    "optInIndicator",
    "eValueHeaderExtension"
})
public class EValueHeader {

    protected String enrollmentStatus;
    protected Boolean optInIndicator;
    @XmlElement(nillable = true)
    protected List<Object> eValueHeaderExtension;

    /**
     * Gets the value of the enrollmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    /**
     * Sets the value of the enrollmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrollmentStatus(String value) {
        this.enrollmentStatus = value;
    }

    /**
     * Gets the value of the optInIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptInIndicator() {
        return optInIndicator;
    }

    /**
     * Sets the value of the optInIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptInIndicator(Boolean value) {
        this.optInIndicator = value;
    }

    /**
     * Gets the value of the eValueHeaderExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eValueHeaderExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEValueHeaderExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getEValueHeaderExtension() {
        if (eValueHeaderExtension == null) {
            eValueHeaderExtension = new ArrayList<Object>();
        }
        return this.eValueHeaderExtension;
    }

}
