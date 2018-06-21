
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAASource.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAASource"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NEW"/&gt;
 *     &lt;enumeration value="SPIN"/&gt;
 *     &lt;enumeration value="SPLIT"/&gt;
 *     &lt;enumeration value="REW"/&gt;
 *     &lt;enumeration value="CONV"/&gt;
 *     &lt;enumeration value="BOOKROLL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAASource", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAASource {

    NEW,
    SPIN,
    SPLIT,
    REW,
    CONV,
    BOOKROLL;

    public String value() {
        return name();
    }

    public static AAASource fromValue(String v) {
        return valueOf(v);
    }

}
