
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LiabilityCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LiabilityCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AF"/>
 *     &lt;enumeration value="BD"/>
 *     &lt;enumeration value="NF"/>
 *     &lt;enumeration value="PF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LiabilityCode", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum LiabilityCode {

    AF,
    BD,
    NF,
    PF;

    public String value() {
        return name();
    }

    public static LiabilityCode fromValue(String v) {
        return valueOf(v);
    }

}
