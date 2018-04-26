
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAVehOtherBodyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAVehOtherBodyType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SEDAN"/&gt;
 *     &lt;enumeration value="COUPE"/&gt;
 *     &lt;enumeration value="COUPEH"/&gt;
 *     &lt;enumeration value="CONV"/&gt;
 *     &lt;enumeration value="PICKUPR"/&gt;
 *     &lt;enumeration value="PICKUPS"/&gt;
 *     &lt;enumeration value="PICKUPC"/&gt;
 *     &lt;enumeration value="SW"/&gt;
 *     &lt;enumeration value="HBACK"/&gt;
 *     &lt;enumeration value="FBACK"/&gt;
 *     &lt;enumeration value="LBACK"/&gt;
 *     &lt;enumeration value="UTILITY"/&gt;
 *     &lt;enumeration value="NBACK"/&gt;
 *     &lt;enumeration value="TTOP"/&gt;
 *     &lt;enumeration value="VAN"/&gt;
 *     &lt;enumeration value="VANE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAVehOtherBodyType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAVehOtherBodyType {

    SEDAN,
    COUPE,
    COUPEH,
    CONV,
    PICKUPR,
    PICKUPS,
    PICKUPC,
    SW,
    HBACK,
    FBACK,
    LBACK,
    UTILITY,
    NBACK,
    TTOP,
    VAN,
    VANE;

    public String value() {
        return name();
    }

    public static AAAVehOtherBodyType fromValue(String v) {
        return valueOf(v);
    }

}
