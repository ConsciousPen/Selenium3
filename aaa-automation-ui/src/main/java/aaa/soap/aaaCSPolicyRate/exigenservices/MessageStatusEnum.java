
package aaa.soap.aaaCSPolicyRate.exigenservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageStatusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MessageStatusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Error"/>
 *     &lt;enumeration value="Warning"/>
 *     &lt;enumeration value="Info"/>
 *     &lt;enumeration value="Success"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MessageStatusEnum")
@XmlEnum
public enum MessageStatusEnum {

    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("Warning")
    WARNING("Warning"),
    @XmlEnumValue("Info")
    INFO("Info"),
    @XmlEnumValue("Success")
    SUCCESS("Success");
    private final String value;

    MessageStatusEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MessageStatusEnum fromValue(String v) {
        for (MessageStatusEnum c: MessageStatusEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
