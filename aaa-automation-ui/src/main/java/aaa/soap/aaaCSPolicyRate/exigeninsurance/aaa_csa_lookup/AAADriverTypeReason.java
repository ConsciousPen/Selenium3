
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAADriverTypeReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAADriverTypeReason">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMREQDRREC"/>
 *     &lt;enumeration value="COMREQUDR"/>
 *     &lt;enumeration value="DIST"/>
 *     &lt;enumeration value="INSREQNONDR"/>
 *     &lt;enumeration value="INSREQOTH"/>
 *     &lt;enumeration value="INSREQPREM"/>
 *     &lt;enumeration value="NCNU"/>
 *     &lt;enumeration value="OCAR"/>
 *     &lt;enumeration value="OTH"/>
 *     &lt;enumeration value="UNLSP"/>
 *     &lt;enumeration value="CONVERSION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAADriverTypeReason", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAADriverTypeReason {

    COMREQDRREC,
    COMREQUDR,
    DIST,
    INSREQNONDR,
    INSREQOTH,
    INSREQPREM,
    NCNU,
    OCAR,
    OTH,
    UNLSP,
    CONVERSION;

    public String value() {
        return name();
    }

    public static AAADriverTypeReason fromValue(String v) {
        return valueOf(v);
    }

}
