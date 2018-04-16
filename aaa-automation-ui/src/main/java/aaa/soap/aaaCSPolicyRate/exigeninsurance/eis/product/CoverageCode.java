
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis.product;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CoverageCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CoverageCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALLRISK"/>
 *     &lt;enumeration value="COMPDED"/>
 *     &lt;enumeration value="COLLDED"/>
 *     &lt;enumeration value="ETEC"/>
 *     &lt;enumeration value="BI"/>
 *     &lt;enumeration value="PD"/>
 *     &lt;enumeration value="UMBI"/>
 *     &lt;enumeration value="UIMBI"/>
 *     &lt;enumeration value="MEDPM"/>
 *     &lt;enumeration value="GLASS"/>
 *     &lt;enumeration value="NEWCAR"/>
 *     &lt;enumeration value="RIDESHARE"/>
 *     &lt;enumeration value="LOAN"/>
 *     &lt;enumeration value="SPECEQUIP"/>
 *     &lt;enumeration value="UMPD"/>
 *     &lt;enumeration value="CDW"/>
 *     &lt;enumeration value="RREIM"/>
 *     &lt;enumeration value="TOWINGLABOR"/>
 *     &lt;enumeration value="OEM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
