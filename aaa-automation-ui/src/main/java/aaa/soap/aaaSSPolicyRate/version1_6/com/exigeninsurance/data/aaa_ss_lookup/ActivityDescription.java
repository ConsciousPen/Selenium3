
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActivityDescription.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActivityDescription"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AA2"/&gt;
 *     &lt;enumeration value="AAF"/&gt;
 *     &lt;enumeration value="BOT"/&gt;
 *     &lt;enumeration value="CMG"/&gt;
 *     &lt;enumeration value="CML"/&gt;
 *     &lt;enumeration value="CMP"/&gt;
 *     &lt;enumeration value="CRD"/&gt;
 *     &lt;enumeration value="DEQ"/&gt;
 *     &lt;enumeration value="DEV"/&gt;
 *     &lt;enumeration value="DFC"/&gt;
 *     &lt;enumeration value="DLC"/&gt;
 *     &lt;enumeration value="DLP"/&gt;
 *     &lt;enumeration value="DOL"/&gt;
 *     &lt;enumeration value="DOS"/&gt;
 *     &lt;enumeration value="DR"/&gt;
 *     &lt;enumeration value="DSO"/&gt;
 *     &lt;enumeration value="DTB"/&gt;
 *     &lt;enumeration value="DUD"/&gt;
 *     &lt;enumeration value="DUI"/&gt;
 *     &lt;enumeration value="DWE"/&gt;
 *     &lt;enumeration value="DWI"/&gt;
 *     &lt;enumeration value="DWV"/&gt;
 *     &lt;enumeration value="DWW"/&gt;
 *     &lt;enumeration value="EOS"/&gt;
 *     &lt;enumeration value="EPR"/&gt;
 *     &lt;enumeration value="FDC"/&gt;
 *     &lt;enumeration value="FEL"/&gt;
 *     &lt;enumeration value="FLE"/&gt;
 *     &lt;enumeration value="FOE"/&gt;
 *     &lt;enumeration value="FTA"/&gt;
 *     &lt;enumeration value="FTC"/&gt;
 *     &lt;enumeration value="FTY"/&gt;
 *     &lt;enumeration value="FVC"/&gt;
 *     &lt;enumeration value="HAR"/&gt;
 *     &lt;enumeration value="HOM"/&gt;
 *     &lt;enumeration value="HOV"/&gt;
 *     &lt;enumeration value="IEV"/&gt;
 *     &lt;enumeration value="IL"/&gt;
 *     &lt;enumeration value="ILR"/&gt;
 *     &lt;enumeration value="ILU"/&gt;
 *     &lt;enumeration value="IMD"/&gt;
 *     &lt;enumeration value="IND"/&gt;
 *     &lt;enumeration value="IP"/&gt;
 *     &lt;enumeration value="IRA"/&gt;
 *     &lt;enumeration value="IS"/&gt;
 *     &lt;enumeration value="IT"/&gt;
 *     &lt;enumeration value="IUT"/&gt;
 *     &lt;enumeration value="LIC"/&gt;
 *     &lt;enumeration value="LTS"/&gt;
 *     &lt;enumeration value="MMV"/&gt;
 *     &lt;enumeration value="MRE"/&gt;
 *     &lt;enumeration value="NAF"/&gt;
 *     &lt;enumeration value="NGD"/&gt;
 *     &lt;enumeration value="NIN"/&gt;
 *     &lt;enumeration value="NLP"/&gt;
 *     &lt;enumeration value="OBT"/&gt;
 *     &lt;enumeration value="OSL"/&gt;
 *     &lt;enumeration value="OVH"/&gt;
 *     &lt;enumeration value="AAD"/&gt;
 *     &lt;enumeration value="AAB"/&gt;
 *     &lt;enumeration value="REF"/&gt;
 *     &lt;enumeration value="RKD"/&gt;
 *     &lt;enumeration value="SAF"/&gt;
 *     &lt;enumeration value="SBL"/&gt;
 *     &lt;enumeration value="SCH"/&gt;
 *     &lt;enumeration value="SPD"/&gt;
 *     &lt;enumeration value="SUS"/&gt;
 *     &lt;enumeration value="TOW"/&gt;
 *     &lt;enumeration value="UUL"/&gt;
 *     &lt;enumeration value="WOC"/&gt;
 *     &lt;enumeration value="WSR"/&gt;
 *     &lt;enumeration value="UEE"/&gt;
 *     &lt;enumeration value="SP2"/&gt;
 *     &lt;enumeration value="TGT"/&gt;
 *     &lt;enumeration value="SPW"/&gt;
 *     &lt;enumeration value="SPV"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ActivityDescription", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum ActivityDescription {

    @XmlEnumValue("AA2")
    AA_2("AA2"),
    AAF("AAF"),
    BOT("BOT"),
    CMG("CMG"),
    CML("CML"),
    CMP("CMP"),
    CRD("CRD"),
    DEQ("DEQ"),
    DEV("DEV"),
    DFC("DFC"),
    DLC("DLC"),
    DLP("DLP"),
    DOL("DOL"),
    DOS("DOS"),
    DR("DR"),
    DSO("DSO"),
    DTB("DTB"),
    DUD("DUD"),
    DUI("DUI"),
    DWE("DWE"),
    DWI("DWI"),
    DWV("DWV"),
    DWW("DWW"),
    EOS("EOS"),
    EPR("EPR"),
    FDC("FDC"),
    FEL("FEL"),
    FLE("FLE"),
    FOE("FOE"),
    FTA("FTA"),
    FTC("FTC"),
    FTY("FTY"),
    FVC("FVC"),
    HAR("HAR"),
    HOM("HOM"),
    HOV("HOV"),
    IEV("IEV"),
    IL("IL"),
    ILR("ILR"),
    ILU("ILU"),
    IMD("IMD"),
    IND("IND"),
    IP("IP"),
    IRA("IRA"),
    IS("IS"),
    IT("IT"),
    IUT("IUT"),
    LIC("LIC"),
    LTS("LTS"),
    MMV("MMV"),
    MRE("MRE"),
    NAF("NAF"),
    NGD("NGD"),
    NIN("NIN"),
    NLP("NLP"),
    OBT("OBT"),
    OSL("OSL"),
    OVH("OVH"),
    AAD("AAD"),
    AAB("AAB"),
    REF("REF"),
    RKD("RKD"),
    SAF("SAF"),
    SBL("SBL"),
    SCH("SCH"),
    SPD("SPD"),
    SUS("SUS"),
    TOW("TOW"),
    UUL("UUL"),
    WOC("WOC"),
    WSR("WSR"),
    UEE("UEE"),
    @XmlEnumValue("SP2")
    SP_2("SP2"),
    TGT("TGT"),
    SPW("SPW"),
    SPV("SPV");
    private final String value;

    ActivityDescription(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityDescription fromValue(String v) {
        for (ActivityDescription c: ActivityDescription.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
