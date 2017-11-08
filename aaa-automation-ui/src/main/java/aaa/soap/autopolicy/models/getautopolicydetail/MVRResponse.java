
package aaa.soap.autopolicy.models.getautopolicydetail;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MVRResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MVRResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mvrResponseIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eyeColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hairColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mvrViolations" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverViolations" minOccurs="0"/>
 *         &lt;element name="mvrAccident" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}DriverAccidents" minOccurs="0"/>
 *         &lt;element name="mVRResponseExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MVRResponse", propOrder = {
    "mvrResponseIdentifier",
    "height",
    "weight",
    "eyeColor",
    "hairColor",
    "mvrViolations",
    "mvrAccident",
    "mvrResponseExtension"
})
public class MVRResponse {

    protected String mvrResponseIdentifier;
    protected String height;
    protected String weight;
    protected String eyeColor;
    protected String hairColor;
    protected DriverViolations mvrViolations;
    protected DriverAccidents mvrAccident;
    @XmlElement(name = "mVRResponseExtension")
    protected List<Object> mvrResponseExtension;

    /**
     * Gets the value of the mvrResponseIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMvrResponseIdentifier() {
        return mvrResponseIdentifier;
    }

    /**
     * Sets the value of the mvrResponseIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMvrResponseIdentifier(String value) {
        this.mvrResponseIdentifier = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeight(String value) {
        this.weight = value;
    }

    /**
     * Gets the value of the eyeColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEyeColor() {
        return eyeColor;
    }

    /**
     * Sets the value of the eyeColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEyeColor(String value) {
        this.eyeColor = value;
    }

    /**
     * Gets the value of the hairColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHairColor() {
        return hairColor;
    }

    /**
     * Sets the value of the hairColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHairColor(String value) {
        this.hairColor = value;
    }

    /**
     * Gets the value of the mvrViolations property.
     * 
     * @return
     *     possible object is
     *     {@link DriverViolations }
     *     
     */
    public DriverViolations getMvrViolations() {
        return mvrViolations;
    }

    /**
     * Sets the value of the mvrViolations property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverViolations }
     *     
     */
    public void setMvrViolations(DriverViolations value) {
        this.mvrViolations = value;
    }

    /**
     * Gets the value of the mvrAccident property.
     * 
     * @return
     *     possible object is
     *     {@link DriverAccidents }
     *     
     */
    public DriverAccidents getMvrAccident() {
        return mvrAccident;
    }

    /**
     * Sets the value of the mvrAccident property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverAccidents }
     *     
     */
    public void setMvrAccident(DriverAccidents value) {
        this.mvrAccident = value;
    }

    /**
     * Gets the value of the mvrResponseExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mvrResponseExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMVRResponseExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getMVRResponseExtension() {
        if (mvrResponseExtension == null) {
            mvrResponseExtension = new ArrayList<Object>();
        }
        return this.mvrResponseExtension;
    }

}
