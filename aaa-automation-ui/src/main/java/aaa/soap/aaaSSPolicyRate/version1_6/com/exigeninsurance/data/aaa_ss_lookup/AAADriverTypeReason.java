
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAADriverTypeReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAADriverTypeReason"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CR"/&gt;
 *     &lt;enumeration value="DR"/&gt;
 *     &lt;enumeration value="LEAP"/&gt;
 *     &lt;enumeration value="LRS"/&gt;
 *     &lt;enumeration value="NCNU"/&gt;
 *     &lt;enumeration value="OCAR"/&gt;
 *     &lt;enumeration value="OTH"/&gt;
 *     &lt;enumeration value="UNLSP"/&gt;
 *     &lt;enumeration value="NBLS"/&gt;
 *     &lt;enumeration value="LPRS"/&gt;
 *     &lt;enumeration value="AUDND"/&gt;
 *     &lt;enumeration value="DDND"/&gt;
 *     &lt;enumeration value="INC"/&gt;
 *     &lt;enumeration value="OOC"/&gt;
 *     &lt;enumeration value="CONVERSION"/&gt;
 *     &lt;enumeration value="FH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAADriverTypeReason", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAADriverTypeReason {

    CR,
    DR,
    LEAP,
    LRS,
    NCNU,
    OCAR,
    OTH,
    UNLSP,
    NBLS,
    LPRS,
    AUDND,
    DDND,
    INC,
    OOC,
    CONVERSION,
    FH;

    public String value() {
        return name();
    }

    public static AAADriverTypeReason fromValue(String v) {
        return valueOf(v);
    }

}
