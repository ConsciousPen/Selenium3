
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAASuffix.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAASuffix"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SR"/&gt;
 *     &lt;enumeration value="JR"/&gt;
 *     &lt;enumeration value="II"/&gt;
 *     &lt;enumeration value="III"/&gt;
 *     &lt;enumeration value="IV"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAASuffix", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAASuffix {

    SR,
    JR,
    II,
    III,
    IV;

    public String value() {
        return name();
    }

    public static AAASuffix fromValue(String v) {
        return valueOf(v);
    }

}
