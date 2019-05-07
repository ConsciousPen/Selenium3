package aaa.helpers.db.queries;

import toolkit.db.DBService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PolicyQueries {

    /**
     * Use this formatter to load SQL date/time results that have not been truncated, into a LocalDateTime instance. <br>
     * Example: <br>
     * | String dbMemberSinceDate = getAAAMemberSinceDateFromSQL(quoteNumber).orElse("Null Value"); <br>
     * | LocalDateTime memberSinceDateTime = LocalDateTime.parse(dbMemberSinceDate, SQLDateTimeFormatter); <br>
     * <br>
     */
    public static final DateTimeFormatter SQLDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static java.util.Optional<String> getPolicyTermFromSQL(String policyNumber) throws IllegalArgumentException {

        if (isQuote(policyNumber)){
            throw new IllegalArgumentException(
                    "getPolicyTermFromSQL() only accepts policy numbers. Attempted policy: " + policyNumber);
        }

        String query =
                "select ps.CONTRACTTERMTYPECD " +
                "from policysummary ps " +
                "join PaymentOption po on ps.paymentOption_id = po.id " +
                "Where ps.policynumber ='"+ policyNumber + "'";

        return DBService.get().getValue(query);
    }

    /**
     * Evaluates whether or not quoteOrPolicyNumber starts with a Q
     * @param quoteOrPolicyNumber The quote or policy number to be evaluated
     * @return true if quoteOrPolicyNumber starts with Q
     */
    private static boolean isQuote(String quoteOrPolicyNumber) {
        return quoteOrPolicyNumber.toUpperCase().startsWith("Q");
    }
}
