
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_db;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AntiTheftCode2.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AntiTheftCode2"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="STD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AntiTheftCode2", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_DB/1.0")
@XmlEnum
public enum AntiTheftCode2 {

    NONE,
    STD;

    public String value() {
        return name();
    }

    public static AntiTheftCode2 fromValue(String v) {
        return valueOf(v);
    }

}
