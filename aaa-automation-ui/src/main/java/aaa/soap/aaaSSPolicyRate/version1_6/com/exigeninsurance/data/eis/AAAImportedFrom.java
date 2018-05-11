
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAImportedFrom.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAImportedFrom"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MPOQ"/&gt;
 *     &lt;enumeration value="WMSB2C"/&gt;
 *     &lt;enumeration value="EZLYNX"/&gt;
 *     &lt;enumeration value="PWRQ"/&gt;
 *     &lt;enumeration value="PLRATE"/&gt;
 *     &lt;enumeration value="MLTIONL"/&gt;
 *     &lt;enumeration value="DSB2B"/&gt;
 *     &lt;enumeration value="TRBRTR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAImportedFrom")
@XmlEnum
public enum AAAImportedFrom {

    MPOQ("MPOQ"),
    @XmlEnumValue("WMSB2C")
    WMSB_2_C("WMSB2C"),
    EZLYNX("EZLYNX"),
    PWRQ("PWRQ"),
    PLRATE("PLRATE"),
    MLTIONL("MLTIONL"),
    @XmlEnumValue("DSB2B")
    DSB_2_B("DSB2B"),
    TRBRTR("TRBRTR");
    private final String value;

    AAAImportedFrom(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAImportedFrom fromValue(String v) {
        for (AAAImportedFrom c: AAAImportedFrom.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
