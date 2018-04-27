
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAASDLookup.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAASDLookup"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Level 1"/&gt;
 *     &lt;enumeration value="Level 2"/&gt;
 *     &lt;enumeration value="Level 3"/&gt;
 *     &lt;enumeration value="Level 4"/&gt;
 *     &lt;enumeration value="Level 5"/&gt;
 *     &lt;enumeration value="No Discount"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAASDLookup", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAASDLookup {

    @XmlEnumValue("Level 1")
    LEVEL_1("Level 1"),
    @XmlEnumValue("Level 2")
    LEVEL_2("Level 2"),
    @XmlEnumValue("Level 3")
    LEVEL_3("Level 3"),
    @XmlEnumValue("Level 4")
    LEVEL_4("Level 4"),
    @XmlEnumValue("Level 5")
    LEVEL_5("Level 5"),
    @XmlEnumValue("No Discount")
    NO_DISCOUNT("No Discount");
    private final String value;

    AAAASDLookup(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAASDLookup fromValue(String v) {
        for (AAAASDLookup c: AAAASDLookup.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
