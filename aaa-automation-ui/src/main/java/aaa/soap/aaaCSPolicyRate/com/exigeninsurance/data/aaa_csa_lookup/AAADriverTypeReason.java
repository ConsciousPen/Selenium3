
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

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
 *     &lt;enumeration value="COMREQDRREC"/&gt;
 *     &lt;enumeration value="COMREQUDR"/&gt;
 *     &lt;enumeration value="DIST"/&gt;
 *     &lt;enumeration value="INSREQNONDR"/&gt;
 *     &lt;enumeration value="INSREQOTH"/&gt;
 *     &lt;enumeration value="INSREQPREM"/&gt;
 *     &lt;enumeration value="NCNU"/&gt;
 *     &lt;enumeration value="OCAR"/&gt;
 *     &lt;enumeration value="OTH"/&gt;
 *     &lt;enumeration value="UNLSP"/&gt;
 *     &lt;enumeration value="CONVERSION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
