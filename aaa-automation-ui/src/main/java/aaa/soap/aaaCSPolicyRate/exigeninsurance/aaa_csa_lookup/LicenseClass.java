
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicenseClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LicenseClass">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EXP"/>
 *     &lt;enumeration value="LEAP"/>
 *     &lt;enumeration value="LICUS"/>
 *     &lt;enumeration value="NOTL"/>
 *     &lt;enumeration value="REV"/>
 *     &lt;enumeration value="SUSP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LicenseClass", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum LicenseClass {

    EXP,
    LEAP,
    LICUS,
    NOTL,
    REV,
    SUSP;

    public String value() {
        return name();
    }

    public static LicenseClass fromValue(String v) {
        return valueOf(v);
    }

}
