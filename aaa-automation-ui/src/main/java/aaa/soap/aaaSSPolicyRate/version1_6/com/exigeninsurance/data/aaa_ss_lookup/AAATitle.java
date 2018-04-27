
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAATitle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAATitle"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="RADM"/&gt;
 *     &lt;enumeration value="RADMR"/&gt;
 *     &lt;enumeration value="CDR"/&gt;
 *     &lt;enumeration value="CDRRE"/&gt;
 *     &lt;enumeration value="CHIEF"/&gt;
 *     &lt;enumeration value="CHPLN"/&gt;
 *     &lt;enumeration value="COL"/&gt;
 *     &lt;enumeration value="COLRE"/&gt;
 *     &lt;enumeration value="CONG"/&gt;
 *     &lt;enumeration value="CPT"/&gt;
 *     &lt;enumeration value="CPTRE"/&gt;
 *     &lt;enumeration value="MAJGN"/&gt;
 *     &lt;enumeration value="MAJGR"/&gt;
 *     &lt;enumeration value="MAJRE"/&gt;
 *     &lt;enumeration value="MR"/&gt;
 *     &lt;enumeration value="MRS"/&gt;
 *     &lt;enumeration value="MS"/&gt;
 *     &lt;enumeration value="MSGR"/&gt;
 *     &lt;enumeration value="MSGT"/&gt;
 *     &lt;enumeration value="MSGTR"/&gt;
 *     &lt;enumeration value="PASTR"/&gt;
 *     &lt;enumeration value="PROF"/&gt;
 *     &lt;enumeration value="RABBI"/&gt;
 *     &lt;enumeration value="VADM"/&gt;
 *     &lt;enumeration value="VADMR"/&gt;
 *     &lt;enumeration value="BGEN"/&gt;
 *     &lt;enumeration value="BGENR"/&gt;
 *     &lt;enumeration value="BR"/&gt;
 *     &lt;enumeration value="BSHP"/&gt;
 *     &lt;enumeration value="BLANK"/&gt;
 *     &lt;enumeration value="ADM"/&gt;
 *     &lt;enumeration value="ADR"/&gt;
 *     &lt;enumeration value="ATTY"/&gt;
 *     &lt;enumeration value="REV"/&gt;
 *     &lt;enumeration value="SEN"/&gt;
 *     &lt;enumeration value="SGT"/&gt;
 *     &lt;enumeration value="SGTFC"/&gt;
 *     &lt;enumeration value="SGTRE"/&gt;
 *     &lt;enumeration value="SIR"/&gt;
 *     &lt;enumeration value="SIST"/&gt;
 *     &lt;enumeration value="SMAJ"/&gt;
 *     &lt;enumeration value="SMAJR"/&gt;
 *     &lt;enumeration value="SMSGT"/&gt;
 *     &lt;enumeration value="SR"/&gt;
 *     &lt;enumeration value="CWO"/&gt;
 *     &lt;enumeration value="DR"/&gt;
 *     &lt;enumeration value="FA"/&gt;
 *     &lt;enumeration value="GEN"/&gt;
 *     &lt;enumeration value="HON"/&gt;
 *     &lt;enumeration value="JUDGE"/&gt;
 *     &lt;enumeration value="JUST"/&gt;
 *     &lt;enumeration value="LADY"/&gt;
 *     &lt;enumeration value="LT"/&gt;
 *     &lt;enumeration value="LTC"/&gt;
 *     &lt;enumeration value="LTCD"/&gt;
 *     &lt;enumeration value="LTCDR"/&gt;
 *     &lt;enumeration value="LTCRE"/&gt;
 *     &lt;enumeration value="LTGN"/&gt;
 *     &lt;enumeration value="LTGNR"/&gt;
 *     &lt;enumeration value="LTRE"/&gt;
 *     &lt;enumeration value="MAJ"/&gt;
 *     &lt;enumeration value="MISS"/&gt;
 *     &lt;enumeration value="MOTHR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAATitle", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum AAATitle {

    RADM,
    RADMR,
    CDR,
    CDRRE,
    CHIEF,
    CHPLN,
    COL,
    COLRE,
    CONG,
    CPT,
    CPTRE,
    MAJGN,
    MAJGR,
    MAJRE,
    MR,
    MRS,
    MS,
    MSGR,
    MSGT,
    MSGTR,
    PASTR,
    PROF,
    RABBI,
    VADM,
    VADMR,
    BGEN,
    BGENR,
    BR,
    BSHP,
    BLANK,
    ADM,
    ADR,
    ATTY,
    REV,
    SEN,
    SGT,
    SGTFC,
    SGTRE,
    SIR,
    SIST,
    SMAJ,
    SMAJR,
    SMSGT,
    SR,
    CWO,
    DR,
    FA,
    GEN,
    HON,
    JUDGE,
    JUST,
    LADY,
    LT,
    LTC,
    LTCD,
    LTCDR,
    LTCRE,
    LTGN,
    LTGNR,
    LTRE,
    MAJ,
    MISS,
    MOTHR;

    public String value() {
        return name();
    }

    public static AAATitle fromValue(String v) {
        return valueOf(v);
    }

}
