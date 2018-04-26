
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAVehicleUseCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAVehicleUseCd"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Artisan"/&gt;
 *     &lt;enumeration value="Business"/&gt;
 *     &lt;enumeration value="Farm"/&gt;
 *     &lt;enumeration value="Pleasure"/&gt;
 *     &lt;enumeration value="WorkCommute"/&gt;
 *     &lt;enumeration value="OCCASIONAL"/&gt;
 *     &lt;enumeration value="RG_AFFORDED"/&gt;
 *     &lt;enumeration value="RG_NO_AFFORDED"/&gt;
 *     &lt;enumeration value="PU30Y"/&gt;
 *     &lt;enumeration value="PU100Y"/&gt;
 *     &lt;enumeration value="PU150Y"/&gt;
 *     &lt;enumeration value="TPR"/&gt;
 *     &lt;enumeration value="NTPR"/&gt;
 *     &lt;enumeration value="FU"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAVehicleUseCd", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAAVehicleUseCd {

    @XmlEnumValue("Artisan")
    ARTISAN("Artisan"),
    @XmlEnumValue("Business")
    BUSINESS("Business"),
    @XmlEnumValue("Farm")
    FARM("Farm"),
    @XmlEnumValue("Pleasure")
    PLEASURE("Pleasure"),
    @XmlEnumValue("WorkCommute")
    WORK_COMMUTE("WorkCommute"),
    OCCASIONAL("OCCASIONAL"),
    RG_AFFORDED("RG_AFFORDED"),
    RG_NO_AFFORDED("RG_NO_AFFORDED"),
    @XmlEnumValue("PU30Y")
    PU_30_Y("PU30Y"),
    @XmlEnumValue("PU100Y")
    PU_100_Y("PU100Y"),
    @XmlEnumValue("PU150Y")
    PU_150_Y("PU150Y"),
    TPR("TPR"),
    NTPR("NTPR"),
    FU("FU");
    private final String value;

    AAAVehicleUseCd(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAVehicleUseCd fromValue(String v) {
        for (AAAVehicleUseCd c: AAAVehicleUseCd.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
