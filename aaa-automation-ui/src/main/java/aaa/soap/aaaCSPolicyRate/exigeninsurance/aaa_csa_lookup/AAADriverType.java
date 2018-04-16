
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAADriverType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAADriverType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="afr"/>
 *     &lt;enumeration value="excl"/>
 *     &lt;enumeration value="nafr"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAADriverType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAADriverType {

    @XmlEnumValue("afr")
    AFR("afr"),
    @XmlEnumValue("excl")
    EXCL("excl"),
    @XmlEnumValue("nafr")
    NAFR("nafr");
    private final String value;

    AAADriverType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAADriverType fromValue(String v) {
        for (AAADriverType c: AAADriverType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
