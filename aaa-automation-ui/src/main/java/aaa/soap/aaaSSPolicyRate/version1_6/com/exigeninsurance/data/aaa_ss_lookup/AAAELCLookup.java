
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAELCLookup.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAELCLookup"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="CATASTROPHICEVENT"/&gt;
 *     &lt;enumeration value="SERIOUSILLORINJURY"/&gt;
 *     &lt;enumeration value="DOFSCP"/&gt;
 *     &lt;enumeration value="DIVORCEORINVOLINT"/&gt;
 *     &lt;enumeration value="TEMPLOSSOFEMP"/&gt;
 *     &lt;enumeration value="MDO"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *     &lt;enumeration value="CATASTROPHICILLNESSORINJURY"/&gt;
 *     &lt;enumeration value="DIVORCE"/&gt;
 *     &lt;enumeration value="IDENTITYTHEFTEVNT"/&gt;
 *     &lt;enumeration value="DECLINED"/&gt;
 *     &lt;enumeration value="HOUINH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAELCLookup", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAELCLookup {

    NONE,
    CATASTROPHICEVENT,
    SERIOUSILLORINJURY,
    DOFSCP,
    DIVORCEORINVOLINT,
    TEMPLOSSOFEMP,
    MDO,
    OTHER,
    CATASTROPHICILLNESSORINJURY,
    DIVORCE,
    IDENTITYTHEFTEVNT,
    DECLINED,
    HOUINH;

    public String value() {
        return name();
    }

    public static AAAELCLookup fromValue(String v) {
        return valueOf(v);
    }

}
