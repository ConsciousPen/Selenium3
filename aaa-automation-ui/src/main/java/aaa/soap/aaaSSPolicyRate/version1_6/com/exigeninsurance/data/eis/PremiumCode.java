
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis;

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
 *     &lt;enumeration value="MVD"/&gt;
 *     &lt;enumeration value="MPD"/&gt;
 *     &lt;enumeration value="CMS"/&gt;
 *     &lt;enumeration value="CMR"/&gt;
 *     &lt;enumeration value="NCD"/&gt;
 *     &lt;enumeration value="ATD"/&gt;
 *     &lt;enumeration value="VAT"/&gt;
 *     &lt;enumeration value="PRD"/&gt;
 *     &lt;enumeration value="HD"/&gt;
 *     &lt;enumeration value="DCS"/&gt;
 *     &lt;enumeration value="PPD"/&gt;
 *     &lt;enumeration value="SR22"/&gt;
 *     &lt;enumeration value="DSD"/&gt;
 *     &lt;enumeration value="URS"/&gt;
 *     &lt;enumeration value="SDD"/&gt;
 *     &lt;enumeration value="DDD"/&gt;
 *     &lt;enumeration value="ASD"/&gt;
 *     &lt;enumeration value="LD"/&gt;
 *     &lt;enumeration value="UDRS"/&gt;
 *     &lt;enumeration value="FDLS"/&gt;
 *     &lt;enumeration value="MMD"/&gt;
 *     &lt;enumeration value="AFD"/&gt;
 *     &lt;enumeration value="FR44"/&gt;
 *     &lt;enumeration value="CDD"/&gt;
 *     &lt;enumeration value="TLD"/&gt;
 *     &lt;enumeration value="DRLD"/&gt;
 *     &lt;enumeration value="ALBD"/&gt;
 *     &lt;enumeration value="PRMS_KY"/&gt;
 *     &lt;enumeration value="PRMS_WV"/&gt;
 *     &lt;enumeration value="PREMT_COUNTY"/&gt;
 *     &lt;enumeration value="PREMT_CITY"/&gt;
 *     &lt;enumeration value="TDD"/&gt;
 *     &lt;enumeration value="NSD"/&gt;
 *     &lt;enumeration value="MDDD"/&gt;
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
    MVD("MVD"),
    MPD("MPD"),
    CMS("CMS"),
    CMR("CMR"),
    NCD("NCD"),
    ATD("ATD"),
    VAT("VAT"),
    PRD("PRD"),
    HD("HD"),
    DCS("DCS"),
    PPD("PPD"),
    @XmlEnumValue("SR22")
    SR_22("SR22"),
    DSD("DSD"),
    URS("URS"),
    SDD("SDD"),
    DDD("DDD"),
    ASD("ASD"),
    LD("LD"),
    UDRS("UDRS"),
    FDLS("FDLS"),
    MMD("MMD"),
    AFD("AFD"),
    @XmlEnumValue("FR44")
    FR_44("FR44"),
    CDD("CDD"),
    TLD("TLD"),
    DRLD("DRLD"),
    ALBD("ALBD"),
    PRMS_KY("PRMS_KY"),
    PRMS_WV("PRMS_WV"),
    PREMT_COUNTY("PREMT_COUNTY"),
    PREMT_CITY("PREMT_CITY"),
    TDD("TDD"),
    NSD("NSD"),
    MDDD("MDDD");
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
