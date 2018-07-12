
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAVehicleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAVehicleType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AC"/&gt;
 *     &lt;enumeration value="Conversion"/&gt;
 *     &lt;enumeration value="Golf"/&gt;
 *     &lt;enumeration value="Motor"/&gt;
 *     &lt;enumeration value="PPA"/&gt;
 *     &lt;enumeration value="Trailer"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAVehicleType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAVehicleType {

    AC("AC"),
    @XmlEnumValue("Conversion")
    CONVERSION("Conversion"),
    @XmlEnumValue("Golf")
    GOLF("Golf"),
    @XmlEnumValue("Motor")
    MOTOR("Motor"),
    PPA("PPA"),
    @XmlEnumValue("Trailer")
    TRAILER("Trailer");
    private final String value;

    AAAVehicleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAVehicleType fromValue(String v) {
        for (AAAVehicleType c: AAAVehicleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
