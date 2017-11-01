
package aaa.soap.autopolicy.models.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleCategory.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RuleCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Product Constraint"/>
 *     &lt;enumeration value="Mandatory Data"/>
 *     &lt;enumeration value="Eligibility"/>
 *     &lt;enumeration value="Underwriting"/>
 *     &lt;enumeration value="Informational"/>
 *     &lt;enumeration value="Clerical"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
