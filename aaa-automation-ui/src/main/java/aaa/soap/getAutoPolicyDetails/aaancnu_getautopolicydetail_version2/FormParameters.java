
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FormParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameter" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}RiskFormParameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="formParametersExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormParameters", propOrder = {
    "parameter",
    "formParametersExtension"
})
public class FormParameters {

    @XmlElement(nillable = true)
    protected List<RiskFormParameter> parameter;
    @XmlElement(nillable = true)
    protected List<Object> formParametersExtension;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RiskFormParameter }
     * 
     * 
     */
    public List<RiskFormParameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<RiskFormParameter>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the formParametersExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formParametersExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormParametersExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getFormParametersExtension() {
        if (formParametersExtension == null) {
            formParametersExtension = new ArrayList<Object>();
        }
        return this.formParametersExtension;
    }

}
