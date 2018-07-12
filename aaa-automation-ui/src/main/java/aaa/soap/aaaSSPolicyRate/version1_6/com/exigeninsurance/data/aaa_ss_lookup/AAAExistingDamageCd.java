
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAExistingDamageCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAExistingDamageCd"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="RUST"/&gt;
 *     &lt;enumeration value="SDDOS"/&gt;
 *     &lt;enumeration value="NWAT"/&gt;
 *     &lt;enumeration value="COBW"/&gt;
 *     &lt;enumeration value="MDTB"/&gt;
 *     &lt;enumeration value="BSM"/&gt;
 *     &lt;enumeration value="OCD"/&gt;
 *     &lt;enumeration value="BHOT"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="OBD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAExistingDamageCd", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAExistingDamageCd {

    RUST,
    SDDOS,
    NWAT,
    COBW,
    MDTB,
    BSM,
    OCD,
    BHOT,
    OTHER,
    NONE,
    OBD;

    public String value() {
        return name();
    }

    public static AAAExistingDamageCd fromValue(String v) {
        return valueOf(v);
    }

}
