
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredOrPrincipalRole.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InsuredOrPrincipalRole"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="policyHolder"/&gt;
 *     &lt;enumeration value="namedInsured"/&gt;
 *     &lt;enumeration value="additionalInsured"/&gt;
 *     &lt;enumeration value="primaryContact"/&gt;
 *     &lt;enumeration value="principal"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InsuredOrPrincipalRole", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum InsuredOrPrincipalRole {

    @XmlEnumValue("policyHolder")
    POLICY_HOLDER("policyHolder"),
    @XmlEnumValue("namedInsured")
    NAMED_INSURED("namedInsured"),
    @XmlEnumValue("additionalInsured")
    ADDITIONAL_INSURED("additionalInsured"),
    @XmlEnumValue("primaryContact")
    PRIMARY_CONTACT("primaryContact"),
    @XmlEnumValue("principal")
    PRINCIPAL("principal");
    private final String value;

    InsuredOrPrincipalRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InsuredOrPrincipalRole fromValue(String v) {
        for (InsuredOrPrincipalRole c: InsuredOrPrincipalRole.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
