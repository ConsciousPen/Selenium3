
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AAADriverAssignment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAADriverAssignment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="driverAssignmentOid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="relationshipType" type="{http://exigeninsurance.com/eis/product/schema/AAA_SS/1.0}DriverRelationshipType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAADriverAssignment", propOrder = {
    "driverAssignmentOid",
    "relationshipType"
})
public class AAADriverAssignment {

    @XmlElement(required = true)
    protected String driverAssignmentOid;
    @XmlSchemaType(name = "string")
    protected DriverRelationshipType relationshipType;

    /**
     * Gets the value of the driverAssignmentOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverAssignmentOid() {
        return driverAssignmentOid;
    }

    /**
     * Sets the value of the driverAssignmentOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverAssignmentOid(String value) {
        this.driverAssignmentOid = value;
    }

    /**
     * Gets the value of the relationshipType property.
     * 
     * @return
     *     possible object is
     *     {@link DriverRelationshipType }
     *     
     */
    public DriverRelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * Sets the value of the relationshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DriverRelationshipType }
     *     
     */
    public void setRelationshipType(DriverRelationshipType value) {
        this.relationshipType = value;
    }

}
