
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicenseClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LicenseClass"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EXP"/&gt;
 *     &lt;enumeration value="LEAP"/&gt;
 *     &lt;enumeration value="LICUS"/&gt;
 *     &lt;enumeration value="NOTL"/&gt;
 *     &lt;enumeration value="REV"/&gt;
 *     &lt;enumeration value="SUSP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
