
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LiabilityCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LiabilityCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AF"/&gt;
 *     &lt;enumeration value="BD"/&gt;
 *     &lt;enumeration value="NF"/&gt;
 *     &lt;enumeration value="PF"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
