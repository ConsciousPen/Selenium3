
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAALienholder.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAALienholder">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LI001"/>
 *     &lt;enumeration value="LI002"/>
 *     &lt;enumeration value="LI003"/>
 *     &lt;enumeration value="LI004"/>
 *     &lt;enumeration value="LI005"/>
 *     &lt;enumeration value="LI006"/>
 *     &lt;enumeration value="LI007"/>
 *     &lt;enumeration value="LI008"/>
 *     &lt;enumeration value="LI009"/>
 *     &lt;enumeration value="LI010"/>
 *     &lt;enumeration value="LI011"/>
 *     &lt;enumeration value="LI012"/>
 *     &lt;enumeration value="LI013"/>
 *     &lt;enumeration value="LI014"/>
 *     &lt;enumeration value="LI015"/>
 *     &lt;enumeration value="LI016"/>
 *     &lt;enumeration value="LI017"/>
 *     &lt;enumeration value="LI018"/>
 *     &lt;enumeration value="LI019"/>
 *     &lt;enumeration value="LI020"/>
 *     &lt;enumeration value="LI021"/>
 *     &lt;enumeration value="LI022"/>
 *     &lt;enumeration value="LI023"/>
 *     &lt;enumeration value="LI024"/>
 *     &lt;enumeration value="LI025"/>
 *     &lt;enumeration value="LI026"/>
 *     &lt;enumeration value="LI027"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAALienholder", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAALienholder {

    @XmlEnumValue("LI001")
    LI_001("LI001"),
    @XmlEnumValue("LI002")
    LI_002("LI002"),
    @XmlEnumValue("LI003")
    LI_003("LI003"),
    @XmlEnumValue("LI004")
    LI_004("LI004"),
    @XmlEnumValue("LI005")
    LI_005("LI005"),
    @XmlEnumValue("LI006")
    LI_006("LI006"),
    @XmlEnumValue("LI007")
    LI_007("LI007"),
    @XmlEnumValue("LI008")
    LI_008("LI008"),
    @XmlEnumValue("LI009")
    LI_009("LI009"),
    @XmlEnumValue("LI010")
    LI_010("LI010"),
    @XmlEnumValue("LI011")
    LI_011("LI011"),
    @XmlEnumValue("LI012")
    LI_012("LI012"),
    @XmlEnumValue("LI013")
    LI_013("LI013"),
    @XmlEnumValue("LI014")
    LI_014("LI014"),
    @XmlEnumValue("LI015")
    LI_015("LI015"),
    @XmlEnumValue("LI016")
    LI_016("LI016"),
    @XmlEnumValue("LI017")
    LI_017("LI017"),
    @XmlEnumValue("LI018")
    LI_018("LI018"),
    @XmlEnumValue("LI019")
    LI_019("LI019"),
    @XmlEnumValue("LI020")
    LI_020("LI020"),
    @XmlEnumValue("LI021")
    LI_021("LI021"),
    @XmlEnumValue("LI022")
    LI_022("LI022"),
    @XmlEnumValue("LI023")
    LI_023("LI023"),
    @XmlEnumValue("LI024")
    LI_024("LI024"),
    @XmlEnumValue("LI025")
    LI_025("LI025"),
    @XmlEnumValue("LI026")
    LI_026("LI026"),
    @XmlEnumValue("LI027")
    LI_027("LI027"),
    OTHER("OTHER");
    private final String value;

    AAALienholder(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAALienholder fromValue(String v) {
        for (AAALienholder c: AAALienholder.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
