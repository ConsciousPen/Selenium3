
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SortDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SortDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Descending"/&gt;
 *     &lt;enumeration value="Ascending"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SortDirection")
@XmlEnum
public enum SortDirection {

    @XmlEnumValue("Descending")
    DESCENDING("Descending"),
    @XmlEnumValue("Ascending")
    ASCENDING("Ascending");
    private final String value;

    SortDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SortDirection fromValue(String v) {
        for (SortDirection c: SortDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
