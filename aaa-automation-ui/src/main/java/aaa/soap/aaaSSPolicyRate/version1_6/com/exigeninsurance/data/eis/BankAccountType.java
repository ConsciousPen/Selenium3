
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BankAccountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BankAccountType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="checking"/&gt;
 *     &lt;enumeration value="saving"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BankAccountType")
@XmlEnum
public enum BankAccountType {

    @XmlEnumValue("checking")
    CHECKING("checking"),
    @XmlEnumValue("saving")
    SAVING("saving");
    private final String value;

    BankAccountType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BankAccountType fromValue(String v) {
        for (BankAccountType c: BankAccountType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
