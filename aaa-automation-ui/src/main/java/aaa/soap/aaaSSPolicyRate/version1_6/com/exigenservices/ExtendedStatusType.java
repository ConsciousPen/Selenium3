
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ExtendedStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtendedStatusType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ExtendedStatusCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ExtendedStatus" type="{http://exigenservices.com/ipb/policy/integration}MessageStatusEnum"/&gt;
 *         &lt;element name="ExtendedStatusDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AppliesTo" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="IdRefs"&gt;
 *         &lt;simpleType&gt;
 *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedStatusType", propOrder = {
    "extendedStatusCd",
    "extendedStatus",
    "extendedStatusDesc",
    "appliesTo"
})
public class ExtendedStatusType {

    @XmlElement(name = "ExtendedStatusCd", required = true)
    protected String extendedStatusCd;
    @XmlElement(name = "ExtendedStatus", required = true)
    @XmlSchemaType(name = "string")
    protected MessageStatusEnum extendedStatus;
    @XmlElement(name = "ExtendedStatusDesc")
    protected String extendedStatusDesc;
    @XmlElement(name = "AppliesTo")
    protected List<String> appliesTo;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "IdRefs")
    @XmlIDREF
    protected List<Object> idRefs;

    /**
     * Gets the value of the extendedStatusCd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedStatusCd() {
        return extendedStatusCd;
    }

    /**
     * Sets the value of the extendedStatusCd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedStatusCd(String value) {
        this.extendedStatusCd = value;
    }

    /**
     * Gets the value of the extendedStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MessageStatusEnum }
     *     
     */
    public MessageStatusEnum getExtendedStatus() {
        return extendedStatus;
    }

    /**
     * Sets the value of the extendedStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatusEnum }
     *     
     */
    public void setExtendedStatus(MessageStatusEnum value) {
        this.extendedStatus = value;
    }

    /**
     * Gets the value of the extendedStatusDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedStatusDesc() {
        return extendedStatusDesc;
    }

    /**
     * Sets the value of the extendedStatusDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedStatusDesc(String value) {
        this.extendedStatusDesc = value;
    }

    /**
     * Gets the value of the appliesTo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the appliesTo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAppliesTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAppliesTo() {
        if (appliesTo == null) {
            appliesTo = new ArrayList<String>();
        }
        return this.appliesTo;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the idRefs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idRefs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdRefs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getIdRefs() {
        if (idRefs == null) {
            idRefs = new ArrayList<Object>();
        }
        return this.idRefs;
    }

}
