
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleSeverity.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RuleSeverity"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Informational"/&gt;
 *     &lt;enumeration value="High"/&gt;
 *     &lt;enumeration value="Medium"/&gt;
 *     &lt;enumeration value="Low"/&gt;
 *     &lt;enumeration value="Error"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RuleSeverity")
@XmlEnum
public enum RuleSeverity {

    @XmlEnumValue("Informational")
    INFORMATIONAL("Informational"),
    @XmlEnumValue("High")
    HIGH("High"),
    @XmlEnumValue("Medium")
    MEDIUM("Medium"),
    @XmlEnumValue("Low")
    LOW("Low"),
    @XmlEnumValue("Error")
    ERROR("Error");
    private final String value;

    RuleSeverity(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RuleSeverity fromValue(String v) {
        for (RuleSeverity c: RuleSeverity.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
