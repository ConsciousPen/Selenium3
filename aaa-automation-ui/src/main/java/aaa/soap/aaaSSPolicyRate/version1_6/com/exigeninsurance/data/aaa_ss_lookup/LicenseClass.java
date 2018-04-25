
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

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
 *     &lt;enumeration value="FOR"/&gt;
 *     &lt;enumeration value="LEAP"/&gt;
 *     &lt;enumeration value="LICCA"/&gt;
 *     &lt;enumeration value="LICUS"/&gt;
 *     &lt;enumeration value="NEVL"/&gt;
 *     &lt;enumeration value="NOTL"/&gt;
 *     &lt;enumeration value="PERMREV"/&gt;
 *     &lt;enumeration value="PERMSUSP"/&gt;
 *     &lt;enumeration value="TEMPREV"/&gt;
 *     &lt;enumeration value="TEMPSUSP"/&gt;
 *     &lt;enumeration value="UNKNOWN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LicenseClass", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum LicenseClass {

    EXP,
    FOR,
    LEAP,
    LICCA,
    LICUS,
    NEVL,
    NOTL,
    PERMREV,
    PERMSUSP,
    TEMPREV,
    TEMPSUSP,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static LicenseClass fromValue(String v) {
        return valueOf(v);
    }

}
