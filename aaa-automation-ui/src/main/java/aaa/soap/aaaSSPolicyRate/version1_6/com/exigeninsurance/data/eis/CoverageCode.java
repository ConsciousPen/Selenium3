
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CoverageCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CoverageCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ALLRISK"/&gt;
 *     &lt;enumeration value="COMPDED"/&gt;
 *     &lt;enumeration value="COLLDED"/&gt;
 *     &lt;enumeration value="ETEC"/&gt;
 *     &lt;enumeration value="BI"/&gt;
 *     &lt;enumeration value="PD"/&gt;
 *     &lt;enumeration value="UMBI"/&gt;
 *     &lt;enumeration value="UIMBI"/&gt;
 *     &lt;enumeration value="UIMPD"/&gt;
 *     &lt;enumeration value="UIMBI"/&gt;
 *     &lt;enumeration value="MEDPM"/&gt;
 *     &lt;enumeration value="TOWINGLABOR"/&gt;
 *     &lt;enumeration value="SPECEQUIP"/&gt;
 *     &lt;enumeration value="UMPD"/&gt;
 *     &lt;enumeration value="UMPDDED"/&gt;
 *     &lt;enumeration value="ADB"/&gt;
 *     &lt;enumeration value="TD"/&gt;
 *     &lt;enumeration value="LOAN"/&gt;
 *     &lt;enumeration value="IL"/&gt;
 *     &lt;enumeration value="PIP"/&gt;
 *     &lt;enumeration value="PIPDEDAPPTO"/&gt;
 *     &lt;enumeration value="FUNERAL"/&gt;
 *     &lt;enumeration value="EMB"/&gt;
 *     &lt;enumeration value="ADBC"/&gt;
 *     &lt;enumeration value="OBEL"/&gt;
 *     &lt;enumeration value="PIPDED"/&gt;
 *     &lt;enumeration value="APIP"/&gt;
 *     &lt;enumeration value="MEE"/&gt;
 *     &lt;enumeration value="UM/SUM"/&gt;
 *     &lt;enumeration value="RREIM"/&gt;
 *     &lt;enumeration value="PIPWORKLOSS"/&gt;
 *     &lt;enumeration value="PIPMEDICAL"/&gt;
 *     &lt;enumeration value="ADDPIP"/&gt;
 *     &lt;enumeration value="BPIP"/&gt;
 *     &lt;enumeration value="GPIP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CoverageCode")
@XmlEnum
public enum CoverageCode {

    ALLRISK("ALLRISK"),
    COMPDED("COMPDED"),
    COLLDED("COLLDED"),
    ETEC("ETEC"),
    BI("BI"),
    PD("PD"),
    UMBI("UMBI"),
    UIMBI("UIMBI"),
    UIMPD("UIMPD"),
    MEDPM("MEDPM"),
    TOWINGLABOR("TOWINGLABOR"),
    SPECEQUIP("SPECEQUIP"),
    UMPD("UMPD"),
    UMPDDED("UMPDDED"),
    ADB("ADB"),
    TD("TD"),
    LOAN("LOAN"),
    IL("IL"),
    PIP("PIP"),
    PIPDEDAPPTO("PIPDEDAPPTO"),
    FUNERAL("FUNERAL"),
    EMB("EMB"),
    ADBC("ADBC"),
    OBEL("OBEL"),
    PIPDED("PIPDED"),
    APIP("APIP"),
    MEE("MEE"),
    @XmlEnumValue("UM/SUM")
    UM_SUM("UM/SUM"),
    RREIM("RREIM"),
    PIPWORKLOSS("PIPWORKLOSS"),
    PIPMEDICAL("PIPMEDICAL"),
    ADDPIP("ADDPIP"),
    BPIP("BPIP"),
    GPIP("GPIP");
    private final String value;

    CoverageCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CoverageCode fromValue(String v) {
        for (CoverageCode c: CoverageCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
