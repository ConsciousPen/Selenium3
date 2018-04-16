
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAPolicyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAPolicyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DBA"/>
 *     &lt;enumeration value="NANO"/>
 *     &lt;enumeration value="STD"/>
 *     &lt;enumeration value="TRUST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAPolicyType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAPolicyType {

    DBA,
    NANO,
    STD,
    TRUST;

    public String value() {
        return name();
    }

    public static AAAPolicyType fromValue(String v) {
        return valueOf(v);
    }

}
