
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyImageTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PolicyImageTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Delta"/&gt;
 *     &lt;enumeration value="Full"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PolicyImageTypeEnum")
@XmlEnum
public enum PolicyImageTypeEnum {

    @XmlEnumValue("Delta")
    DELTA("Delta"),
    @XmlEnumValue("Full")
    FULL("Full");
    private final String value;

    PolicyImageTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PolicyImageTypeEnum fromValue(String v) {
        for (PolicyImageTypeEnum c: PolicyImageTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
