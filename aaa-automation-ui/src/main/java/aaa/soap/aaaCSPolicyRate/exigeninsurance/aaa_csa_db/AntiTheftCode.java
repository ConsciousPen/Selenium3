
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_db;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AntiTheftCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AntiTheftCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NA"/>
 *     &lt;enumeration value="OPT"/>
 *     &lt;enumeration value="STD"/>
 *     &lt;enumeration value="UNK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AntiTheftCode", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_DB/1.0")
@XmlEnum
public enum AntiTheftCode {

    NA,
    OPT,
    STD,
    UNK;

    public String value() {
        return name();
    }

    public static AntiTheftCode fromValue(String v) {
        return valueOf(v);
    }

}
