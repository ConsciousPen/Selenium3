
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CoverageCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CoverageCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ALLRISK"/&gt;
 *     &lt;enumeration value="COMPDED"/&gt;
 *     &lt;enumeration value="COLLDED"/&gt;
 *     &lt;enumeration value="ETEC"/&gt;
 *     &lt;enumeration value="BI"/&gt;
 *     &lt;enumeration value="PD"/&gt;
 *     &lt;enumeration value="UMBI"/&gt;
 *     &lt;enumeration value="UIMBI"/&gt;
 *     &lt;enumeration value="MEDPM"/&gt;
 *     &lt;enumeration value="GLASS"/&gt;
 *     &lt;enumeration value="NEWCAR"/&gt;
 *     &lt;enumeration value="RIDESHARE"/&gt;
 *     &lt;enumeration value="LOAN"/&gt;
 *     &lt;enumeration value="SPECEQUIP"/&gt;
 *     &lt;enumeration value="UMPD"/&gt;
 *     &lt;enumeration value="CDW"/&gt;
 *     &lt;enumeration value="RREIM"/&gt;
 *     &lt;enumeration value="TOWINGLABOR"/&gt;
 *     &lt;enumeration value="OEM"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CoverageCode")
@XmlEnum
public enum CoverageCode {

    ALLRISK,
    COMPDED,
    COLLDED,
    ETEC,
    BI,
    PD,
    UMBI,
    UIMBI,
    MEDPM,
    GLASS,
    NEWCAR,
    RIDESHARE,
    LOAN,
    SPECEQUIP,
    UMPD,
    CDW,
    RREIM,
    TOWINGLABOR,
    OEM;

    public String value() {
        return name();
    }

    public static CoverageCode fromValue(String v) {
        return valueOf(v);
    }

}
