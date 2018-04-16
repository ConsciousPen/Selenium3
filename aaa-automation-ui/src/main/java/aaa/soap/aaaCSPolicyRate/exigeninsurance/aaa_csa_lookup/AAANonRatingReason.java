
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAANonRatingReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAANonRatingReason">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADMINPERSE"/>
 *     &lt;enumeration value="DISBYDMV"/>
 *     &lt;enumeration value="FAILURETOPAY"/>
 *     &lt;enumeration value="OTH"/>
 *     &lt;enumeration value="PENOFPERJ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
