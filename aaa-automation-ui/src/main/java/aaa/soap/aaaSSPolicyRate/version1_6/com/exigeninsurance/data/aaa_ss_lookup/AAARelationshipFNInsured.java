
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

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
 *     &lt;enumeration value="CH"/&gt;
 *     &lt;enumeration value="EMP"/&gt;
 *     &lt;enumeration value="IN"/&gt;
 *     &lt;enumeration value="ORR"/&gt;
 *     &lt;enumeration value="OT"/&gt;
 *     &lt;enumeration value="PA"/&gt;
 *     &lt;enumeration value="RDP"/&gt;
 *     &lt;enumeration value="SI"/&gt;
 *     &lt;enumeration value="SP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAARelationshipFNInsured", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAARelationshipFNInsured {

    CH,
    EMP,
    IN,
    ORR,
    OT,
    PA,
    RDP,
    SI,
    SP;

    public String value() {
        return name();
    }

    public static AAARelationshipFNInsured fromValue(String v) {
        return valueOf(v);
    }

}
