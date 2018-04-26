
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MostRecentGPA.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MostRecentGPA"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="astud"/&gt;
 *     &lt;enumeration value="bstud"/&gt;
 *     &lt;enumeration value="colgrad"/&gt;
 *     &lt;enumeration value="cstud"/&gt;
 *     &lt;enumeration value="fail"/&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="pass"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "MostRecentGPA", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
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
    @XmlEnumValue("fail")
    FAIL("fail"),
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
