
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EndorsementForms complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EndorsementForms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="endorsementForm" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}RiskForm" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="endorsementFormsExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndorsementForms", propOrder = {
    "endorsementForm",
    "endorsementFormsExtension"
})
public class EndorsementForms {

    @XmlElement(nillable = true)
    protected List<RiskForm> endorsementForm;
    @XmlElement(nillable = true)
    protected List<Object> endorsementFormsExtension;

    /**
     * Gets the value of the endorsementForm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endorsementForm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndorsementForm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RiskForm }
     * 
     * 
     */
    public List<RiskForm> getEndorsementForm() {
        if (endorsementForm == null) {
            endorsementForm = new ArrayList<RiskForm>();
        }
        return this.endorsementForm;
    }

    /**
     * Gets the value of the endorsementFormsExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endorsementFormsExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndorsementFormsExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getEndorsementFormsExtension() {
        if (endorsementFormsExtension == null) {
            endorsementFormsExtension = new ArrayList<Object>();
        }
        return this.endorsementFormsExtension;
    }

}
