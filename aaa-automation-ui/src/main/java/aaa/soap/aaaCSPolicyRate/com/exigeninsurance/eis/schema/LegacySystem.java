
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LegacySystem.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LegacySystem"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ADES"/&gt;
 *     &lt;enumeration value="HUON"/&gt;
 *     &lt;enumeration value="MPOQ"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
