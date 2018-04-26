
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PreferredPhone.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PreferredPhone"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="homePhone"/&gt;
 *     &lt;enumeration value="workPhone"/&gt;
 *     &lt;enumeration value="mobilePhone"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PreferredPhone", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum PreferredPhone {

    @XmlEnumValue("homePhone")
    HOME_PHONE("homePhone"),
    @XmlEnumValue("workPhone")
    WORK_PHONE("workPhone"),
    @XmlEnumValue("mobilePhone")
    MOBILE_PHONE("mobilePhone");
    private final String value;

    PreferredPhone(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PreferredPhone fromValue(String v) {
        for (PreferredPhone c: PreferredPhone.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
