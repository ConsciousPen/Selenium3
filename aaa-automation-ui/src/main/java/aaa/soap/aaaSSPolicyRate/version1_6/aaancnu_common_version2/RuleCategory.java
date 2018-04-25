
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleCategory.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RuleCategory"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Product Constraint"/&gt;
 *     &lt;enumeration value="Mandatory Data"/&gt;
 *     &lt;enumeration value="Eligibility"/&gt;
 *     &lt;enumeration value="Underwriting"/&gt;
 *     &lt;enumeration value="Informational"/&gt;
 *     &lt;enumeration value="Clerical"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RuleCategory")
@XmlEnum
public enum RuleCategory {

    @XmlEnumValue("Product Constraint")
    PRODUCT_CONSTRAINT("Product Constraint"),
    @XmlEnumValue("Mandatory Data")
    MANDATORY_DATA("Mandatory Data"),
    @XmlEnumValue("Eligibility")
    ELIGIBILITY("Eligibility"),
    @XmlEnumValue("Underwriting")
    UNDERWRITING("Underwriting"),
    @XmlEnumValue("Informational")
    INFORMATIONAL("Informational"),
    @XmlEnumValue("Clerical")
    CLERICAL("Clerical");
    private final String value;

    RuleCategory(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RuleCategory fromValue(String v) {
        for (RuleCategory c: RuleCategory.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
