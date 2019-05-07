
package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EsignatureDetails", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, propOrder = {
    "orginatorEmailAddress",
    "password"
})
public class EsignatureDetails {

    @XmlElement(name = "OrginatorEmailAddress", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = true, nillable = true)
    protected String orginatorEmailAddress;
    @XmlElement(name = "Password", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2, required = false)
    protected String password;

    /**
     * Gets the value of the orginatorEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrginatorEmailAddress() {
        return orginatorEmailAddress;
    }

    /**
     * Sets the value of the orginatorEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrginatorEmailAddress(String value) {
        this.orginatorEmailAddress = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

}
