
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FirstPartyBenefits.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FirstPartyBenefits"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Basic"/&gt;
 *     &lt;enumeration value="Added"/&gt;
 *     &lt;enumeration value="Combo1"/&gt;
 *     &lt;enumeration value="Combo2"/&gt;
 *     &lt;enumeration value="Combo3"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FirstPartyBenefits", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum FirstPartyBenefits {

    @XmlEnumValue("Basic")
    BASIC("Basic"),
    @XmlEnumValue("Added")
    ADDED("Added"),
    @XmlEnumValue("Combo1")
    COMBO_1("Combo1"),
    @XmlEnumValue("Combo2")
    COMBO_2("Combo2"),
    @XmlEnumValue("Combo3")
    COMBO_3("Combo3");
    private final String value;

    FirstPartyBenefits(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FirstPartyBenefits fromValue(String v) {
        for (FirstPartyBenefits c: FirstPartyBenefits.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
