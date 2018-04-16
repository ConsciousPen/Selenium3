
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LegacySystem.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LegacySystem">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADES"/>
 *     &lt;enumeration value="HUON"/>
 *     &lt;enumeration value="MPOQ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LegacySystem")
@XmlEnum
public enum LegacySystem {

    ADES,
    HUON,
    MPOQ;

    public String value() {
        return name();
    }

    public static LegacySystem fromValue(String v) {
        return valueOf(v);
    }

}
