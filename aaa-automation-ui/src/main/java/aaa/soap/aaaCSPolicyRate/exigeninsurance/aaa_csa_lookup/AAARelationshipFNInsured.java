
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAARelationshipFNInsured.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAARelationshipFNInsured">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="DP"/>
 *     &lt;enumeration value="FR"/>
 *     &lt;enumeration value="IN"/>
 *     &lt;enumeration value="MR"/>
 *     &lt;enumeration value="ORR"/>
 *     &lt;enumeration value="OT"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="SP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAARelationshipFNInsured", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAARelationshipFNInsured {

    D,
    DP,
    FR,
    IN,
    MR,
    ORR,
    OT,
    S,
    SP;

    public String value() {
        return name();
    }

    public static AAARelationshipFNInsured fromValue(String v) {
        return valueOf(v);
    }

}
