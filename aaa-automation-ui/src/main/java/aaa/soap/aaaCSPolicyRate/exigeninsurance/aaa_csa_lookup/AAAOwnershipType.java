
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAOwnershipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAOwnershipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FNC"/>
 *     &lt;enumeration value="LSD"/>
 *     &lt;enumeration value="OWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAOwnershipType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAOwnershipType {

    FNC,
    LSD,
    OWN;

    public String value() {
        return name();
    }

    public static AAAOwnershipType fromValue(String v) {
        return valueOf(v);
    }

}
