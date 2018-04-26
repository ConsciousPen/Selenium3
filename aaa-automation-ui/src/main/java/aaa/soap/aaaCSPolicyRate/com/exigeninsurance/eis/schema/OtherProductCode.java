
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OtherProductCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OtherProductCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="membership"/&gt;
 *     &lt;enumeration value="motorcycle"/&gt;
 *     &lt;enumeration value="renters"/&gt;
 *     &lt;enumeration value="home"/&gt;
 *     &lt;enumeration value="condo"/&gt;
 *     &lt;enumeration value="life"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "OtherProductCode")
@XmlEnum
public enum OtherProductCode {

    @XmlEnumValue("membership")
    MEMBERSHIP("membership"),
    @XmlEnumValue("motorcycle")
    MOTORCYCLE("motorcycle"),
    @XmlEnumValue("renters")
    RENTERS("renters"),
    @XmlEnumValue("home")
    HOME("home"),
    @XmlEnumValue("condo")
    CONDO("condo"),
    @XmlEnumValue("life")
    LIFE("life");
    private final String value;

    OtherProductCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OtherProductCode fromValue(String v) {
        for (OtherProductCode c: OtherProductCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
