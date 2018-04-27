
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAOccupation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAOccupation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Clergy"/&gt;
 *     &lt;enumeration value="Employed"/&gt;
 *     &lt;enumeration value="Homemaker"/&gt;
 *     &lt;enumeration value="Military"/&gt;
 *     &lt;enumeration value="Retired"/&gt;
 *     &lt;enumeration value="Self-Employed"/&gt;
 *     &lt;enumeration value="Student"/&gt;
 *     &lt;enumeration value="Unemployed"/&gt;
 *     &lt;enumeration value="Farmer"/&gt;
 *     &lt;enumeration value="MTNG"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAOccupation", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAOccupation {

    @XmlEnumValue("Clergy")
    CLERGY("Clergy"),
    @XmlEnumValue("Employed")
    EMPLOYED("Employed"),
    @XmlEnumValue("Homemaker")
    HOMEMAKER("Homemaker"),
    @XmlEnumValue("Military")
    MILITARY("Military"),
    @XmlEnumValue("Retired")
    RETIRED("Retired"),
    @XmlEnumValue("Self-Employed")
    SELF_EMPLOYED("Self-Employed"),
    @XmlEnumValue("Student")
    STUDENT("Student"),
    @XmlEnumValue("Unemployed")
    UNEMPLOYED("Unemployed"),
    @XmlEnumValue("Farmer")
    FARMER("Farmer"),
    MTNG("MTNG"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    AAAOccupation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAOccupation fromValue(String v) {
        for (AAAOccupation c: AAAOccupation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
