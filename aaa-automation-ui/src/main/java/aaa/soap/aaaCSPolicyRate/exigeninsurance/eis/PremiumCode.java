
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PremiumCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PremiumCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GWT"/>
 *     &lt;enumeration value="NWT"/>
 *     &lt;enumeration value="NWT/R"/>
 *     &lt;enumeration value="RAW"/>
 *     &lt;enumeration value="MDD"/>
 *     &lt;enumeration value="GDD"/>
 *     &lt;enumeration value="GSD"/>
 *     &lt;enumeration value="NDD"/>
 *     &lt;enumeration value="SDD"/>
 *     &lt;enumeration value="MVD"/>
 *     &lt;enumeration value="MPD"/>
 *     &lt;enumeration value="CMS"/>
 *     &lt;enumeration value="CMR"/>
 *     &lt;enumeration value="CMOS"/>
 *     &lt;enumeration value="CNSD"/>
 *     &lt;enumeration value="CALBD"/>
 *     &lt;enumeration value="CPD"/>
 *     &lt;enumeration value="CGTD"/>
 *     &lt;enumeration value="COUD"/>
 *     &lt;enumeration value="CVD"/>
 *     &lt;enumeration value="CDTD"/>
 *     &lt;enumeration value="CGDD"/>
 *     &lt;enumeration value="CATD"/>
 *     &lt;enumeration value="CBUS"/>
 *     &lt;enumeration value="CSHS"/>
 *     &lt;enumeration value="CMVD"/>
 *     &lt;enumeration value="CDD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PremiumCode")
@XmlEnum
public enum PremiumCode {

    GWT("GWT"),
    NWT("NWT"),
    @XmlEnumValue("NWT/R")
    NWT_R("NWT/R"),
    RAW("RAW"),
    MDD("MDD"),
    GDD("GDD"),
    GSD("GSD"),
    NDD("NDD"),
    SDD("SDD"),
    MVD("MVD"),
    MPD("MPD"),
    CMS("CMS"),
    CMR("CMR"),
    CMOS("CMOS"),
    CNSD("CNSD"),
    CALBD("CALBD"),
    CPD("CPD"),
    CGTD("CGTD"),
    COUD("COUD"),
    CVD("CVD"),
    CDTD("CDTD"),
    CGDD("CGDD"),
    CATD("CATD"),
    CBUS("CBUS"),
    CSHS("CSHS"),
    CMVD("CMVD"),
    CDD("CDD");
    private final String value;

    PremiumCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PremiumCode fromValue(String v) {
        for (PremiumCode c: PremiumCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
