
package aaa.soap.autopolicy.models.getautopolicydetail;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Extns complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Extns">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extn" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}Extn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extnsExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extns", propOrder = {
    "extn",
    "extnsExtension"
})
public class Extns {

    @XmlElement(nillable = true)
    protected List<Extn> extn;
    @XmlElement(nillable = true)
    protected List<Object> extnsExtension;

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
     * Gets the value of the extnsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extnsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtnsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getExtnsExtension() {
        if (extnsExtension == null) {
            extnsExtension = new ArrayList<Object>();
        }
        return this.extnsExtension;
    }

}
