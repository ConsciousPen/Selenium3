
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_db;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AntiTheftCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AntiTheftCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NA"/&gt;
 *     &lt;enumeration value="OPT"/&gt;
 *     &lt;enumeration value="STD"/&gt;
 *     &lt;enumeration value="UNK"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
