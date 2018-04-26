
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAATrailerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAATrailerType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TT"/&gt;
 *     &lt;enumeration value="UT"/&gt;
 *     &lt;enumeration value="PC"/&gt;
 *     &lt;enumeration value="PT"/&gt;
 *     &lt;enumeration value="PUT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAATrailerType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAATrailerType {

    TT,
    UT,
    PC,
    PT,
    PUT;

    public String value() {
        return name();
    }

    public static AAATrailerType fromValue(String v) {
        return valueOf(v);
    }

}
