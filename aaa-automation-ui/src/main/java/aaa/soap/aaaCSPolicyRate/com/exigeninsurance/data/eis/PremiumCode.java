
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PremiumCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PremiumCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="GWT"/&gt;
 *     &lt;enumeration value="NWT"/&gt;
 *     &lt;enumeration value="NWT/R"/&gt;
 *     &lt;enumeration value="RAW"/&gt;
 *     &lt;enumeration value="MDD"/&gt;
 *     &lt;enumeration value="GDD"/&gt;
 *     &lt;enumeration value="GSD"/&gt;
 *     &lt;enumeration value="NDD"/&gt;
 *     &lt;enumeration value="SDD"/&gt;
 *     &lt;enumeration value="MVD"/&gt;
 *     &lt;enumeration value="MPD"/&gt;
 *     &lt;enumeration value="CMS"/&gt;
 *     &lt;enumeration value="CMR"/&gt;
 *     &lt;enumeration value="CMOS"/&gt;
 *     &lt;enumeration value="CNSD"/&gt;
 *     &lt;enumeration value="CALBD"/&gt;
 *     &lt;enumeration value="CPD"/&gt;
 *     &lt;enumeration value="CGTD"/&gt;
 *     &lt;enumeration value="COUD"/&gt;
 *     &lt;enumeration value="CVD"/&gt;
 *     &lt;enumeration value="CDTD"/&gt;
 *     &lt;enumeration value="CGDD"/&gt;
 *     &lt;enumeration value="CATD"/&gt;
 *     &lt;enumeration value="CBUS"/&gt;
 *     &lt;enumeration value="CSHS"/&gt;
 *     &lt;enumeration value="CMVD"/&gt;
 *     &lt;enumeration value="CDD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
