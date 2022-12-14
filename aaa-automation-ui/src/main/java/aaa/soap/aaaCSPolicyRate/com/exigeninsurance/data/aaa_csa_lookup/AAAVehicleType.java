
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

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
 *     &lt;enumeration value="Antique"/&gt;
 *     &lt;enumeration value="Camper"/&gt;
 *     &lt;enumeration value="Motor"/&gt;
 *     &lt;enumeration value="Regular"/&gt;
 *     &lt;enumeration value="Trailer"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAVehicleType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAVehicleType {

    @XmlEnumValue("Antique")
    ANTIQUE("Antique"),
    @XmlEnumValue("Camper")
    CAMPER("Camper"),
    @XmlEnumValue("Motor")
    MOTOR("Motor"),
    @XmlEnumValue("Regular")
    REGULAR("Regular"),
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
