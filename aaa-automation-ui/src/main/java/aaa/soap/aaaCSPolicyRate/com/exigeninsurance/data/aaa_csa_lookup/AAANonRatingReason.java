
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAANonRatingReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAANonRatingReason"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ADMINPERSE"/&gt;
 *     &lt;enumeration value="DISBYDMV"/&gt;
 *     &lt;enumeration value="FAILURETOPAY"/&gt;
 *     &lt;enumeration value="OTH"/&gt;
 *     &lt;enumeration value="PENOFPERJ"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAANonRatingReason", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAANonRatingReason {

    ADMINPERSE,
    DISBYDMV,
    FAILURETOPAY,
    OTH,
    PENOFPERJ;

    public String value() {
        return name();
    }

    public static AAANonRatingReason fromValue(String v) {
        return valueOf(v);
    }

}
