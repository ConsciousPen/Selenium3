
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DriverRelationshipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DriverRelationshipType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="primary"/&gt;
 *     &lt;enumeration value="occasional"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DriverRelationshipType")
@XmlEnum
public enum DriverRelationshipType {

    @XmlEnumValue("primary")
    PRIMARY("primary"),
    @XmlEnumValue("occasional")
    OCCASIONAL("occasional");
    private final String value;

    DriverRelationshipType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DriverRelationshipType fromValue(String v) {
        for (DriverRelationshipType c: DriverRelationshipType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
