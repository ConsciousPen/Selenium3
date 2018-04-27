
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAALessor.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAALessor"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="LE001"/&gt;
 *     &lt;enumeration value="LE002"/&gt;
 *     &lt;enumeration value="LE003"/&gt;
 *     &lt;enumeration value="LE004"/&gt;
 *     &lt;enumeration value="LE005"/&gt;
 *     &lt;enumeration value="LE006"/&gt;
 *     &lt;enumeration value="LE007"/&gt;
 *     &lt;enumeration value="LE008"/&gt;
 *     &lt;enumeration value="LE009"/&gt;
 *     &lt;enumeration value="LE010"/&gt;
 *     &lt;enumeration value="LE011"/&gt;
 *     &lt;enumeration value="LE012"/&gt;
 *     &lt;enumeration value="LE013"/&gt;
 *     &lt;enumeration value="LE014"/&gt;
 *     &lt;enumeration value="LE015"/&gt;
 *     &lt;enumeration value="LE016"/&gt;
 *     &lt;enumeration value="LE017"/&gt;
 *     &lt;enumeration value="LE018"/&gt;
 *     &lt;enumeration value="LE019"/&gt;
 *     &lt;enumeration value="LE020"/&gt;
 *     &lt;enumeration value="LE021"/&gt;
 *     &lt;enumeration value="LE022"/&gt;
 *     &lt;enumeration value="LE023"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAALessor", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAALessor {

    @XmlEnumValue("LE001")
    LE_001("LE001"),
    @XmlEnumValue("LE002")
    LE_002("LE002"),
    @XmlEnumValue("LE003")
    LE_003("LE003"),
    @XmlEnumValue("LE004")
    LE_004("LE004"),
    @XmlEnumValue("LE005")
    LE_005("LE005"),
    @XmlEnumValue("LE006")
    LE_006("LE006"),
    @XmlEnumValue("LE007")
    LE_007("LE007"),
    @XmlEnumValue("LE008")
    LE_008("LE008"),
    @XmlEnumValue("LE009")
    LE_009("LE009"),
    @XmlEnumValue("LE010")
    LE_010("LE010"),
    @XmlEnumValue("LE011")
    LE_011("LE011"),
    @XmlEnumValue("LE012")
    LE_012("LE012"),
    @XmlEnumValue("LE013")
    LE_013("LE013"),
    @XmlEnumValue("LE014")
    LE_014("LE014"),
    @XmlEnumValue("LE015")
    LE_015("LE015"),
    @XmlEnumValue("LE016")
    LE_016("LE016"),
    @XmlEnumValue("LE017")
    LE_017("LE017"),
    @XmlEnumValue("LE018")
    LE_018("LE018"),
    @XmlEnumValue("LE019")
    LE_019("LE019"),
    @XmlEnumValue("LE020")
    LE_020("LE020"),
    @XmlEnumValue("LE021")
    LE_021("LE021"),
    @XmlEnumValue("LE022")
    LE_022("LE022"),
    @XmlEnumValue("LE023")
    LE_023("LE023"),
    OTHER("OTHER");
    private final String value;

    AAALessor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAALessor fromValue(String v) {
        for (AAALessor c: AAALessor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
