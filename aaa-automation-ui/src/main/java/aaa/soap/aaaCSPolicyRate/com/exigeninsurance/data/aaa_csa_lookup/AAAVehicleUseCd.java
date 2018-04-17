
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAVehicleUseCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAVehicleUseCd"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BU"/&gt;
 *     &lt;enumeration value="FM"/&gt;
 *     &lt;enumeration value="FMB"/&gt;
 *     &lt;enumeration value="PL"/&gt;
 *     &lt;enumeration value="WC"/&gt;
 *     &lt;enumeration value="OCCASIONAL"/&gt;
 *     &lt;enumeration value="RG_AFFORDED"/&gt;
 *     &lt;enumeration value="RG_NO_AFFORDED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAVehicleUseCd", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAVehicleUseCd {

    BU,
    FM,
    FMB,
    PL,
    WC,
    OCCASIONAL,
    RG_AFFORDED,
    RG_NO_AFFORDED;

    public String value() {
        return name();
    }

    public static AAAVehicleUseCd fromValue(String v) {
        return valueOf(v);
    }

}
