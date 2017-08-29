
package aaa.soap.getAutoPolicyDetails.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleSeverity.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RuleSeverity">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Informational"/>
 *     &lt;enumeration value="High"/>
 *     &lt;enumeration value="Medium"/>
 *     &lt;enumeration value="Low"/>
 *     &lt;enumeration value="Error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
