
package aaa.soap.aaaCSPolicyRate.exigeninsurance.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ComponentState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ComponentState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NoChange"/>
 *     &lt;enumeration value="Created"/>
 *     &lt;enumeration value="Updated"/>
 *     &lt;enumeration value="Deleted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ComponentState")
@XmlEnum
public enum ComponentState {

    @XmlEnumValue("NoChange")
    NO_CHANGE("NoChange"),
    @XmlEnumValue("Created")
    CREATED("Created"),
    @XmlEnumValue("Updated")
    UPDATED("Updated"),
    @XmlEnumValue("Deleted")
    DELETED("Deleted");
    private final String value;

    ComponentState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ComponentState fromValue(String v) {
        for (ComponentState c: ComponentState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
