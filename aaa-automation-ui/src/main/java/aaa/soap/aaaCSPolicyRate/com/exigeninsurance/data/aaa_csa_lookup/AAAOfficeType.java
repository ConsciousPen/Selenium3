
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAOfficeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAOfficeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AAAE"/&gt;
 *     &lt;enumeration value="BO"/&gt;
 *     &lt;enumeration value="CC"/&gt;
 *     &lt;enumeration value="ON"/&gt;
 *     &lt;enumeration value="AZ Club Agent"/&gt;
 *     &lt;enumeration value="SubProducer Agency"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAOfficeType", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAOfficeType {

    AAAE("AAAE"),
    BO("BO"),
    CC("CC"),
    ON("ON"),
    @XmlEnumValue("AZ Club Agent")
    AZ_CLUB_AGENT("AZ Club Agent"),
    @XmlEnumValue("SubProducer Agency")
    SUB_PRODUCER_AGENCY("SubProducer Agency");
    private final String value;

    AAAOfficeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAOfficeType fromValue(String v) {
        for (AAAOfficeType c: AAAOfficeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
