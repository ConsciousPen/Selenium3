
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateProvCd.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StateProvCd"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AK"/&gt;
 *     &lt;enumeration value="AL"/&gt;
 *     &lt;enumeration value="AR"/&gt;
 *     &lt;enumeration value="AZ"/&gt;
 *     &lt;enumeration value="CA"/&gt;
 *     &lt;enumeration value="CO"/&gt;
 *     &lt;enumeration value="CT"/&gt;
 *     &lt;enumeration value="CW"/&gt;
 *     &lt;enumeration value="DC"/&gt;
 *     &lt;enumeration value="DE"/&gt;
 *     &lt;enumeration value="DK"/&gt;
 *     &lt;enumeration value="FL"/&gt;
 *     &lt;enumeration value="FR"/&gt;
 *     &lt;enumeration value="GA"/&gt;
 *     &lt;enumeration value="HI"/&gt;
 *     &lt;enumeration value="IA"/&gt;
 *     &lt;enumeration value="ID"/&gt;
 *     &lt;enumeration value="IL"/&gt;
 *     &lt;enumeration value="IN"/&gt;
 *     &lt;enumeration value="KS"/&gt;
 *     &lt;enumeration value="KY"/&gt;
 *     &lt;enumeration value="LA"/&gt;
 *     &lt;enumeration value="MA"/&gt;
 *     &lt;enumeration value="MD"/&gt;
 *     &lt;enumeration value="ME"/&gt;
 *     &lt;enumeration value="MI"/&gt;
 *     &lt;enumeration value="MN"/&gt;
 *     &lt;enumeration value="MO"/&gt;
 *     &lt;enumeration value="MS"/&gt;
 *     &lt;enumeration value="MT"/&gt;
 *     &lt;enumeration value="NC"/&gt;
 *     &lt;enumeration value="ND"/&gt;
 *     &lt;enumeration value="NE"/&gt;
 *     &lt;enumeration value="NH"/&gt;
 *     &lt;enumeration value="NJ"/&gt;
 *     &lt;enumeration value="NM"/&gt;
 *     &lt;enumeration value="NV"/&gt;
 *     &lt;enumeration value="NY"/&gt;
 *     &lt;enumeration value="OH"/&gt;
 *     &lt;enumeration value="OK"/&gt;
 *     &lt;enumeration value="ON"/&gt;
 *     &lt;enumeration value="OR"/&gt;
 *     &lt;enumeration value="PA"/&gt;
 *     &lt;enumeration value="PR"/&gt;
 *     &lt;enumeration value="RI"/&gt;
 *     &lt;enumeration value="SC"/&gt;
 *     &lt;enumeration value="SD"/&gt;
 *     &lt;enumeration value="TN"/&gt;
 *     &lt;enumeration value="TX"/&gt;
 *     &lt;enumeration value="UT"/&gt;
 *     &lt;enumeration value="VA"/&gt;
 *     &lt;enumeration value="VT"/&gt;
 *     &lt;enumeration value="WA"/&gt;
 *     &lt;enumeration value="WI"/&gt;
 *     &lt;enumeration value="WV"/&gt;
 *     &lt;enumeration value="WY"/&gt;
 *     &lt;enumeration value="FOR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "StateProvCd", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum StateProvCd {

    AK,
    AL,
    AR,
    AZ,
    CA,
    CO,
    CT,
    CW,
    DC,
    DE,
    DK,
    FL,
    FR,
    GA,
    HI,
    IA,
    ID,
    IL,
    IN,
    KS,
    KY,
    LA,
    MA,
    MD,
    ME,
    MI,
    MN,
    MO,
    MS,
    MT,
    NC,
    ND,
    NE,
    NH,
    NJ,
    NM,
    NV,
    NY,
    OH,
    OK,
    ON,
    OR,
    PA,
    PR,
    RI,
    SC,
    SD,
    TN,
    TX,
    UT,
    VA,
    VT,
    WA,
    WI,
    WV,
    WY,
    FOR;

    public String value() {
        return name();
    }

    public static StateProvCd fromValue(String v) {
        return valueOf(v);
    }

}
