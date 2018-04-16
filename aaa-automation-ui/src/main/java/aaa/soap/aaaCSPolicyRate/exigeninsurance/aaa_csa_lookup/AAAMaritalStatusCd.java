
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAMaritalStatusCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAMaritalStatusCd">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="M"/>
 *     &lt;enumeration value="O"/>
 *     &lt;enumeration value="P"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="W"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAMaritalStatusCd", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAMaritalStatusCd {

    C,
    D,
    M,
    O,
    P,
    S,
    W;

    public String value() {
        return name();
    }

    public static AAAMaritalStatusCd fromValue(String v) {
        return valueOf(v);
    }

}
