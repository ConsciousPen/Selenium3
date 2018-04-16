
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAFillingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAFillingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SR1P"/>
 *     &lt;enumeration value="SR22"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAFillingType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAFillingType {

    @XmlEnumValue("SR1P")
    SR_1_P("SR1P"),
    @XmlEnumValue("SR22")
    SR_22("SR22");
    private final String value;

    AAAFillingType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAFillingType fromValue(String v) {
        for (AAAFillingType c: AAAFillingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
