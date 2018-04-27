
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingPaymentPlans.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BillingPaymentPlans"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="lowDepositMoSS"/&gt;
 *     &lt;enumeration value="MonthlySS"/&gt;
 *     &lt;enumeration value="standartSS"/&gt;
 *     &lt;enumeration value="quaterlySS"/&gt;
 *     &lt;enumeration value="semiAnnualSS"/&gt;
 *     &lt;enumeration value="annualSS"/&gt;
 *     &lt;enumeration value="standart6SS"/&gt;
 *     &lt;enumeration value="quaterly6SS"/&gt;
 *     &lt;enumeration value="lowDeposit6SS"/&gt;
 *     &lt;enumeration value="lowDepositSS"/&gt;
 *     &lt;enumeration value="semiAnnual6SS"/&gt;
 *     &lt;enumeration value="zeroDownSS"/&gt;
 *     &lt;enumeration value="zeroDown6SS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BillingPaymentPlans", namespace = "http://www.exigeninsurance.com/data/AAA_SS_Lookup/1.0")
@XmlEnum
public enum BillingPaymentPlans {

    @XmlEnumValue("lowDepositMoSS")
    LOW_DEPOSIT_MO_SS("lowDepositMoSS"),
    @XmlEnumValue("MonthlySS")
    MONTHLY_SS("MonthlySS"),
    @XmlEnumValue("standartSS")
    STANDART_SS("standartSS"),
    @XmlEnumValue("quaterlySS")
    QUATERLY_SS("quaterlySS"),
    @XmlEnumValue("semiAnnualSS")
    SEMI_ANNUAL_SS("semiAnnualSS"),
    @XmlEnumValue("annualSS")
    ANNUAL_SS("annualSS"),
    @XmlEnumValue("standart6SS")
    STANDART_6_SS("standart6SS"),
    @XmlEnumValue("quaterly6SS")
    QUATERLY_6_SS("quaterly6SS"),
    @XmlEnumValue("lowDeposit6SS")
    LOW_DEPOSIT_6_SS("lowDeposit6SS"),
    @XmlEnumValue("lowDepositSS")
    LOW_DEPOSIT_SS("lowDepositSS"),
    @XmlEnumValue("semiAnnual6SS")
    SEMI_ANNUAL_6_SS("semiAnnual6SS"),
    @XmlEnumValue("zeroDownSS")
    ZERO_DOWN_SS("zeroDownSS"),
    @XmlEnumValue("zeroDown6SS")
    ZERO_DOWN_6_SS("zeroDown6SS");
    private final String value;

    BillingPaymentPlans(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BillingPaymentPlans fromValue(String v) {
        for (BillingPaymentPlans c: BillingPaymentPlans.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
