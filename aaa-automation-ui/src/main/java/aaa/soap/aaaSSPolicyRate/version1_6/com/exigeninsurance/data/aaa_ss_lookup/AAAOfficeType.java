
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

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
 *     &lt;enumeration value="AAA IE DSU"/&gt;
 *     &lt;enumeration value="AZ Club Agent"/&gt;
 *     &lt;enumeration value="AZ Independent Agent"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAOfficeType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAOfficeType {

    AAAE("AAAE"),
    BO("BO"),
    CC("CC"),
    ON("ON"),
    @XmlEnumValue("AAA IE DSU")
    AAA_IE_DSU("AAA IE DSU"),
    @XmlEnumValue("AZ Club Agent")
    AZ_CLUB_AGENT("AZ Club Agent"),
    @XmlEnumValue("AZ Independent Agent")
    AZ_INDEPENDENT_AGENT("AZ Independent Agent");
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
