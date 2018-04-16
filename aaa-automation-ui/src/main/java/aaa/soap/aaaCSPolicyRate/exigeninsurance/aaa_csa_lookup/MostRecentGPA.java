
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MostRecentGPA.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MostRecentGPA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="astud"/>
 *     &lt;enumeration value="bstud"/>
 *     &lt;enumeration value="colgrad"/>
 *     &lt;enumeration value="cstud"/>
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="pass"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MostRecentGPA", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum MostRecentGPA {

    @XmlEnumValue("astud")
    ASTUD("astud"),
    @XmlEnumValue("bstud")
    BSTUD("bstud"),
    @XmlEnumValue("colgrad")
    COLGRAD("colgrad"),
    @XmlEnumValue("cstud")
    CSTUD("cstud"),
    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("pass")
    PASS("pass");
    private final String value;

    MostRecentGPA(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MostRecentGPA fromValue(String v) {
        for (MostRecentGPA c: MostRecentGPA.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
