
package aaa.soap.getAutoPolicyDetails.aaancnu_getautopolicydetail_version2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MVRReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MVRReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reportDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="mvrResponse" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}MVRResponse" minOccurs="0"/>
 *         &lt;element name="mVRReportExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MVRReport", propOrder = {
    "reportDate",
    "mvrResponse",
    "mvrReportExtension"
})
public class MVRReport {

    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reportDate;
    protected MVRResponse mvrResponse;
    @XmlElement(name = "mVRReportExtension")
    protected List<Object> mvrReportExtension;

    /**
     * Gets the value of the reportDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReportDate() {
        return reportDate;
    }

    /**
     * Sets the value of the reportDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReportDate(XMLGregorianCalendar value) {
        this.reportDate = value;
    }

    /**
     * Gets the value of the mvrResponse property.
     * 
     * @return
     *     possible object is
     *     {@link MVRResponse }
     *     
     */
    public MVRResponse getMvrResponse() {
        return mvrResponse;
    }

    /**
     * Sets the value of the mvrResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link MVRResponse }
     *     
     */
    public void setMvrResponse(MVRResponse value) {
        this.mvrResponse = value;
    }

    /**
     * Gets the value of the mvrReportExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mvrReportExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMVRReportExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getMVRReportExtension() {
        if (mvrReportExtension == null) {
            mvrReportExtension = new ArrayList<Object>();
        }
        return this.mvrReportExtension;
    }

}
