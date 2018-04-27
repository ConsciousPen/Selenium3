
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AddressType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="living"/&gt;
 *     &lt;enumeration value="mailing"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AddressType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AddressType {

    @XmlEnumValue("living")
    LIVING("living"),
    @XmlEnumValue("mailing")
    MAILING("mailing");
    private final String value;

    AddressType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AddressType fromValue(String v) {
        for (AddressType c: AddressType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
