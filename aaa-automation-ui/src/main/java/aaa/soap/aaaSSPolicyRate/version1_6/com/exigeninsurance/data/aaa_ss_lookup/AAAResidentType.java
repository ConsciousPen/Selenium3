
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAResidentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAResidentType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OWNHME"/&gt;
 *     &lt;enumeration value="OWNCON"/&gt;
 *     &lt;enumeration value="OWNMHM"/&gt;
 *     &lt;enumeration value="RNTMFD"/&gt;
 *     &lt;enumeration value="RNTSFD"/&gt;
 *     &lt;enumeration value="LIVPRT"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAResidentType", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAResidentType {

    OWNHME,
    OWNCON,
    OWNMHM,
    RNTMFD,
    RNTSFD,
    LIVPRT,
    OTHER;

    public String value() {
        return name();
    }

    public static AAAResidentType fromValue(String v) {
        return valueOf(v);
    }

}
