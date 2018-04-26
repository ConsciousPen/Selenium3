
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAPolicyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAPolicyType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DBA"/&gt;
 *     &lt;enumeration value="NANO"/&gt;
 *     &lt;enumeration value="STD"/&gt;
 *     &lt;enumeration value="TRUST"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAPolicyType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAPolicyType {

    DBA,
    NANO,
    STD,
    TRUST;

    public String value() {
        return name();
    }

    public static AAAPolicyType fromValue(String v) {
        return valueOf(v);
    }

}
