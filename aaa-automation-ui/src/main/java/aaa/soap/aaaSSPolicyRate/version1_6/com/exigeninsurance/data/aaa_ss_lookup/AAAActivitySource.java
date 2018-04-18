
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAActivitySource.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAActivitySource"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CLUE"/&gt;
 *     &lt;enumeration value="CSAAClaims"/&gt;
 *     &lt;enumeration value="MVR"/&gt;
 *     &lt;enumeration value="cust"/&gt;
 *     &lt;enumeration value="cmp"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAActivitySource", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAActivitySource {

    CLUE("CLUE"),
    @XmlEnumValue("CSAAClaims")
    CSAA_CLAIMS("CSAAClaims"),
    MVR("MVR"),
    @XmlEnumValue("cust")
    CUST("cust"),
    @XmlEnumValue("cmp")
    CMP("cmp");
    private final String value;

    AAAActivitySource(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAActivitySource fromValue(String v) {
        for (AAAActivitySource c: AAAActivitySource.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
