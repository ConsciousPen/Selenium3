
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InsuredType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BSNESS"/&gt;
 *     &lt;enumeration value="INDV"/&gt;
 *     &lt;enumeration value="JOINT"/&gt;
 *     &lt;enumeration value="TRUST"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InsuredType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum InsuredType {

    BSNESS,
    INDV,
    JOINT,
    TRUST;

    public String value() {
        return name();
    }

    public static InsuredType fromValue(String v) {
        return valueOf(v);
    }

}
