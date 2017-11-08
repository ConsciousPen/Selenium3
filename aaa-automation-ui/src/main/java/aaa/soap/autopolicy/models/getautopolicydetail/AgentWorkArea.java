
package aaa.soap.autopolicy.models.getautopolicydetail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AgentWorkArea complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AgentWorkArea">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="riskState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generalNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legacyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gaName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riskClub" type="{http://www.aaancnuit.com.AAANCNU_GetAutoPolicyDetail_version2}OrganizationHeader" minOccurs="0"/>
 *         &lt;element name="agentWorkAreaExtension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgentWorkArea", propOrder = {
    "riskState",
    "generalNumber",
    "legacyId",
    "dataSource",
    "gaName",
    "riskClub",
    "agentWorkAreaExtension"
})
public class AgentWorkArea {

    protected String riskState;
    protected String generalNumber;
    protected String legacyId;
    protected String dataSource;
    protected String gaName;
    protected OrganizationHeader riskClub;
    @XmlElement(nillable = true)
    protected List<Object> agentWorkAreaExtension;

    /**
     * Gets the value of the riskState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskState() {
        return riskState;
    }

    /**
     * Sets the value of the riskState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskState(String value) {
        this.riskState = value;
    }

    /**
     * Gets the value of the generalNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneralNumber() {
        return generalNumber;
    }

    /**
     * Sets the value of the generalNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneralNumber(String value) {
        this.generalNumber = value;
    }

    /**
     * Gets the value of the legacyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegacyId() {
        return legacyId;
    }

    /**
     * Sets the value of the legacyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegacyId(String value) {
        this.legacyId = value;
    }

    /**
     * Gets the value of the dataSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of the dataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataSource(String value) {
        this.dataSource = value;
    }

    /**
     * Gets the value of the gaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGaName() {
        return gaName;
    }

    /**
     * Sets the value of the gaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGaName(String value) {
        this.gaName = value;
    }

    /**
     * Gets the value of the riskClub property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationHeader }
     *     
     */
    public OrganizationHeader getRiskClub() {
        return riskClub;
    }

    /**
     * Sets the value of the riskClub property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationHeader }
     *     
     */
    public void setRiskClub(OrganizationHeader value) {
        this.riskClub = value;
    }

    /**
     * Gets the value of the agentWorkAreaExtension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agentWorkAreaExtension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAgentWorkAreaExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAgentWorkAreaExtension() {
        if (agentWorkAreaExtension == null) {
            agentWorkAreaExtension = new ArrayList<Object>();
        }
        return this.agentWorkAreaExtension;
    }

}
