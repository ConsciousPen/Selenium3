
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.StateProvCd;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis.ComponentState;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for CIPCSR1PEndorsementForm complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CIPCSR1PEndorsementForm"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="caseNumber" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="PremiumEntry" type="{http://exigeninsurance.com/eis/product/schema/AAA_CSA/1.0}PremiumEntry" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="state" type="{http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0}StateProvCd" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="state" type="{http://www.exigeninsurance.com/data/EIS/1.0}ComponentState" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CIPCSR1PEndorsementForm", propOrder = {
		"caseNumber",
		"expirationDate",
		"filingDate",
		"premiumEntry",
		"state"
})
public class CIPCSR1PEndorsementForm {

	protected BigDecimal caseNumber;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar expirationDate;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar filingDate;
	@XmlElement(name = "PremiumEntry")
	protected List<PremiumEntry> premiumEntry;
	@XmlSchemaType(name = "string")
	protected StateProvCd state;
	@XmlAttribute(name = "oid")
	protected String oid;
	@XmlAttribute(name = "state")
	protected ComponentState componentState;

	/**
	 * Gets the value of the caseNumber property.
	 *
	 * @return possible object is
	 * {@link BigDecimal }
	 */
	public BigDecimal getCaseNumber() {
		return caseNumber;
	}

	/**
	 * Sets the value of the caseNumber property.
	 *
	 * @param value allowed object is
	 *              {@link BigDecimal }
	 */
	public void setCaseNumber(BigDecimal value) {
		this.caseNumber = value;
	}

	/**
	 * Gets the value of the expirationDate property.
	 *
	 * @return possible object is
	 * {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Sets the value of the expirationDate property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setExpirationDate(XMLGregorianCalendar value) {
		this.expirationDate = value;
	}

	/**
	 * Gets the value of the filingDate property.
	 *
	 * @return possible object is
	 * {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getFilingDate() {
		return filingDate;
	}

	/**
	 * Sets the value of the filingDate property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setFilingDate(XMLGregorianCalendar value) {
		this.filingDate = value;
	}

	/**
	 * Gets the value of the premiumEntry property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the premiumEntry property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getPremiumEntry().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PremiumEntry }
	 */
	public List<PremiumEntry> getPremiumEntry() {
		if (premiumEntry == null) {
			premiumEntry = new ArrayList<PremiumEntry>();
		}
		return this.premiumEntry;
	}

	/**
	 * Gets the value of the state property.
	 *
	 * @return possible object is
	 * {@link StateProvCd }
	 */
	public StateProvCd getState() {
		return state;
	}

	/**
	 * Sets the value of the state property.
	 *
	 * @param state allowed object is
	 *              {@link StateProvCd }
	 */
	public void setState(StateProvCd state) {
		this.state = state;
	}

	/**
	 * Gets the value of the oid property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Sets the value of the oid property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setOid(String value) {
		this.oid = value;
	}

	/**
	 * Gets the value of the state property.
	 *
	 * @return possible object is
	 * {@link ComponentState }
	 */
	public ComponentState getComponentState() {
		return componentState;
	}

	public void setComponentState(ComponentState componentState) {
		this.componentState = componentState;
	}

}
