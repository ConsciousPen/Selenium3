
package aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyStatusCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PolicyStatusCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="initiated"/&gt;
 *     &lt;enumeration value="dataGather"/&gt;
 *     &lt;enumeration value="pended"/&gt;
 *     &lt;enumeration value="rated"/&gt;
 *     &lt;enumeration value="proposed"/&gt;
 *     &lt;enumeration value="bound"/&gt;
 *     &lt;enumeration value="companyDeclined"/&gt;
 *     &lt;enumeration value="customerDeclined"/&gt;
 *     &lt;enumeration value="quoteExpired"/&gt;
 *     &lt;enumeration value="issued"/&gt;
 *     &lt;enumeration value="cancelled"/&gt;
 *     &lt;enumeration value="inForce"/&gt;
 *     &lt;enumeration value="expired"/&gt;
 *     &lt;enumeration value="cancelPending"/&gt;
 *     &lt;enumeration value="inForcePending"/&gt;
 *     &lt;enumeration value="pendingCompletion"/&gt;
 *     &lt;enumeration value="quoteSuspended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PolicyStatusCode")
@XmlEnum
public enum PolicyStatusCode {

    @XmlEnumValue("initiated")
    INITIATED("initiated"),
    @XmlEnumValue("dataGather")
    DATA_GATHER("dataGather"),
    @XmlEnumValue("pended")
    PENDED("pended"),
    @XmlEnumValue("rated")
    RATED("rated"),
    @XmlEnumValue("proposed")
    PROPOSED("proposed"),
    @XmlEnumValue("bound")
    BOUND("bound"),
    @XmlEnumValue("companyDeclined")
    COMPANY_DECLINED("companyDeclined"),
    @XmlEnumValue("customerDeclined")
    CUSTOMER_DECLINED("customerDeclined"),
    @XmlEnumValue("quoteExpired")
    QUOTE_EXPIRED("quoteExpired"),
    @XmlEnumValue("issued")
    ISSUED("issued"),
    @XmlEnumValue("cancelled")
    CANCELLED("cancelled"),
    @XmlEnumValue("inForce")
    IN_FORCE("inForce"),
    @XmlEnumValue("expired")
    EXPIRED("expired"),
    @XmlEnumValue("cancelPending")
    CANCEL_PENDING("cancelPending"),
    @XmlEnumValue("inForcePending")
    IN_FORCE_PENDING("inForcePending"),
    @XmlEnumValue("pendingCompletion")
    PENDING_COMPLETION("pendingCompletion"),
    @XmlEnumValue("quoteSuspended")
    QUOTE_SUSPENDED("quoteSuspended");
    private final String value;

    PolicyStatusCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PolicyStatusCode fromValue(String v) {
        for (PolicyStatusCode c: PolicyStatusCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
