
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAARelationshipFNInsured.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAARelationshipFNInsured"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="D"/&gt;
 *     &lt;enumeration value="DP"/&gt;
 *     &lt;enumeration value="FR"/&gt;
 *     &lt;enumeration value="IN"/&gt;
 *     &lt;enumeration value="MR"/&gt;
 *     &lt;enumeration value="ORR"/&gt;
 *     &lt;enumeration value="OT"/&gt;
 *     &lt;enumeration value="S"/&gt;
 *     &lt;enumeration value="SP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
