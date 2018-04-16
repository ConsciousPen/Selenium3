
package aaa.soap.aaaCSPolicyRate.aaancnu_common_version2;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ResponseSort complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseSort">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sortDirection" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}SortDirection"/>
 *         &lt;element name="responseSortExtension" type="{http://www.aaancnuit.com.AAANCNU_Common_version2}ExtensionArea" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseSort", propOrder = {
    "sortBy",
    "sortDirection",
    "responseSortExtension"
})
public class ResponseSort {

    @XmlElement(required = true)
    protected String sortBy;
    @XmlElement(required = true, defaultValue = "Ascending")
    @XmlSchemaType(name = "string")
    protected SortDirection sortDirection;
    @XmlElement(nillable = true)
    protected List<ExtensionArea> responseSortExtension;

    /**
     * Gets the value of the sortBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortBy(String value) {
        this.sortBy = value;
    }

    /**
     * Gets the value of the sortDirection property.
     * 
     * @return
     *     possible object is
     *     {@link SortDirection }
     *     
     */
    public SortDirection getSortDirection() {
        return sortDirection;
    }

    /**
     * Sets the value of the sortDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link SortDirection }
     *     
     */
    public void setSortDirection(SortDirection value) {
        this.sortDirection = value;
    }

    /**
     * Gets the value of the responseSortExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responseSortExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponseSortExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtensionArea }
     * 
     * 
     */
    public List<ExtensionArea> getResponseSortExtension() {
        if (responseSortExtension == null) {
            responseSortExtension = new ArrayList<ExtensionArea>();
        }
        return this.responseSortExtension;
    }

}
