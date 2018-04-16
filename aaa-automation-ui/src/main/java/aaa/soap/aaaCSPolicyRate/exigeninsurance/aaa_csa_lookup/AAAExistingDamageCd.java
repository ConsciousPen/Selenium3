
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAExistingDamageCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAExistingDamageCd">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RUST"/>
 *     &lt;enumeration value="SDDOS"/>
 *     &lt;enumeration value="NWAT"/>
 *     &lt;enumeration value="COBW"/>
 *     &lt;enumeration value="MDTB"/>
 *     &lt;enumeration value="BSM"/>
 *     &lt;enumeration value="OCD"/>
 *     &lt;enumeration value="BHOT"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="OBD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAExistingDamageCd", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAExistingDamageCd {

    RUST,
    SDDOS,
    NWAT,
    COBW,
    MDTB,
    BSM,
    OCD,
    BHOT,
    OTHER,
    NONE,
    OBD;

    public String value() {
        return name();
    }

    public static AAAExistingDamageCd fromValue(String v) {
        return valueOf(v);
    }

}
