
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAEmployeeBenefitCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAEmployeeBenefitCd"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ACTIVE_EMPLOYEE"/&gt;
 *     &lt;enumeration value="BOADRD_OF_DIRECTORS"/&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="RETIREE"/&gt;
 *     &lt;enumeration value="SURVIVING_SPOUSE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAEmployeeBenefitCd", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAEmployeeBenefitCd {

    ACTIVE_EMPLOYEE,
    BOADRD_OF_DIRECTORS,
    NONE,
    RETIREE,
    SURVIVING_SPOUSE;

    public String value() {
        return name();
    }

    public static AAAEmployeeBenefitCd fromValue(String v) {
        return valueOf(v);
    }

}
