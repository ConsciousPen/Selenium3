
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAActivityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAActivityType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="afa"/&gt;
 *     &lt;enumeration value="arv"/&gt;
 *     &lt;enumeration value="cc"/&gt;
 *     &lt;enumeration value="gol"/&gt;
 *     &lt;enumeration value="majv"/&gt;
 *     &lt;enumeration value="minv"/&gt;
 *     &lt;enumeration value="nafa"/&gt;
 *     &lt;enumeration value="nmv"/&gt;
 *     &lt;enumeration value="pafa"/&gt;
 *     &lt;enumeration value="spd"/&gt;
 *     &lt;enumeration value="mpol"/&gt;
 *     &lt;enumeration value="pipol"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAActivityType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAActivityType {

    @XmlEnumValue("afa")
    AFA("afa"),
    @XmlEnumValue("arv")
    ARV("arv"),
    @XmlEnumValue("cc")
    CC("cc"),
    @XmlEnumValue("gol")
    GOL("gol"),
    @XmlEnumValue("majv")
    MAJV("majv"),
    @XmlEnumValue("minv")
    MINV("minv"),
    @XmlEnumValue("nafa")
    NAFA("nafa"),
    @XmlEnumValue("nmv")
    NMV("nmv"),
    @XmlEnumValue("pafa")
    PAFA("pafa"),
    @XmlEnumValue("spd")
    SPD("spd"),
    @XmlEnumValue("mpol")
    MPOL("mpol"),
    @XmlEnumValue("pipol")
    PIPOL("pipol");
    private final String value;

    AAAActivityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAActivityType fromValue(String v) {
        for (AAAActivityType c: AAAActivityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
