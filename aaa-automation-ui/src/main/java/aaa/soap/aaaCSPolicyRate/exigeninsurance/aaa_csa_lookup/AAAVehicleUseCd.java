
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAVehicleUseCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAVehicleUseCd">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BU"/>
 *     &lt;enumeration value="FM"/>
 *     &lt;enumeration value="FMB"/>
 *     &lt;enumeration value="PL"/>
 *     &lt;enumeration value="WC"/>
 *     &lt;enumeration value="OCCASIONAL"/>
 *     &lt;enumeration value="RG_AFFORDED"/>
 *     &lt;enumeration value="RG_NO_AFFORDED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
