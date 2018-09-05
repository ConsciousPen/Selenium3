package aaa.helpers.db.queries;

import java.util.Optional;
import toolkit.db.DBService;

public class AAAMembershipQueries {

    /**
     * Verified Membership states from PAS.
     */
    public enum AAAMembershipStatus {
        ACTIVE, LAPSED, CANCELED, FUTURE_DATED, PENDING, Error, No_Hit
    }

    /**
     * Verified Best Membership states from PAS.
     */
    public enum AAABestMembershipStatus {
        FOUND_STG1, NOTFOUND_STG1, FOUND_STG2, NOHIT_STG1, NOHIT_STG2, NOHIT_STG3, NOHIT_STG4, NOTFOUND_STG2, ERROR_STG1,
        ERROR_STG2, FOUND_STG3, NOTFOUND_STG3, ERROR_STG3, FOUND_STG4, NOTFOUND_STG4, ERROR_STG4
    }

    /**
     * Returns the AAA BestMembership Status from DB
     * @param quoteOrPolicyNumber is the quote or policy number to query against.
     * @return
     */
    public static Optional<AAABestMembershipStatus> getAAABestMembershipStatusFromSQL(String quoteOrPolicyNumber) {
        String query = getStandardMembershipQuery("MS.AAABESTMEMBERSHIPSTATUS", quoteOrPolicyNumber);

        Optional<String> dbResponse = DBService.get().getValue(query);

        Optional<AAABestMembershipStatus> bestMembershipStatus = Optional.empty();

        if(dbResponse.isPresent()){
            AAABestMembershipStatus bestMembershipValue = AAABestMembershipStatus.valueOf(dbResponse.get());
            bestMembershipStatus = Optional.of(bestMembershipValue);
        }

        return bestMembershipStatus;
    }

    /**
     * Returns the AAA Membership Member Since Date from DB.
     * @param quoteOrPolicyNumber is the quote or policy number to query against.
     * @return
     */
    public static java.util.Optional<String> getAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber) {
        String query = getStandardMembershipQuery("MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE", quoteOrPolicyNumber);
        return DBService.get().getValue(query);
    }

    /**
     * Returns the AAA Order Membership Number from DB. <br>
     * ORDERMEMBERSHIPNUMBER is the response from Elastic Search
     * @param quoteOrPolicyNumber is the quote or policy number to query against.
     * @return
     */
    public static java.util.Optional<String> getAAAOrderMembershipNumberFromSQL(String quoteOrPolicyNumber) {
        String query = getStandardMembershipQuery("MS.ORDERMEMBERSHIPNUMBER", quoteOrPolicyNumber);
        return DBService.get().getValue(query);
    }

    /**
     * Changes the Membership status *After* policy was created to specific status. <br>
     * Does NOT support Quotes.
     * @param policyNumber is the policy number to query against.
     * @param updatedStatus is what AAA Membership Status to set in the database.
     * @throws IllegalArgumentException When given a Quote Number.
     */
    public static void updateAAAMembershipStatusInSQL(String policyNumber, AAAMembershipStatus updatedStatus)
            throws IllegalArgumentException {

        if (isQuote(policyNumber)) {
            throw new IllegalArgumentException("updateAAAMembershipStatusInSQL() does not support Quotes. " +
                    "Arg policyNumber: " + policyNumber);
        }

        String query =
                String.format(
                        "UPDATE membershipsummaryentity mse SET mse.membershipstatus = '%1s' " +
                                "WHERE mse.id IN (" +
                                "SELECT ms.id FROM policysummary ps " +
                                "JOIN membershipsummaryentity ms ON ms.id = ps.membershipsummary_id " +
                                "AND PS.policynumber='%2s')",
                        updatedStatus.name(),   //%1s
                        policyNumber);          //%2s

        DBService.get().executeUpdate(query);
    }

    /**
     * Returns the standard membership db query for the column and appropriate quote/policy joined on.
     * @param selectStatmentColumn sets which column you want the query to return
     * @param quoteOrPolicyNumber is the quote or policy number to query against.
     * @return SQL Query that will get specific requested column based on quoteOrPolicyNumber
     */
    private static String getStandardMembershipQuery(String selectStatmentColumn, String quoteOrPolicyNumber) {
        String query =
                "SELECT " + selectStatmentColumn.trim() + " " +
                        "FROM policysummary ps " +
                        "JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' " +
                        getQuoteOrPolicyNumberJoinSQL(quoteOrPolicyNumber) +
                        "JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID " +
                        "ORDER BY ps.EFFECTIVE DESC"; // Makes sure first returned row is latest DB Entry.

        return query;
    }

    /**
     * Evaluates whether or not quoteOrPolicyNumber starts with a Q
     * @param quoteOrPolicyNumber The quote or policy number to be evaluated
     * @return true if quoteOrPolicyNumber starts with Q
     */
    private static boolean isQuote(String quoteOrPolicyNumber) {
        return quoteOrPolicyNumber.toUpperCase().startsWith("Q");
    }

    /**
     * Used to get an appropriate AND column statement using passed in quoteOrPolicyNumber for AAA Membership Queries. <br>
     * <br>
     * EX: quoteOrPolicyNumber("QAZSS952918540") returns "AND ps.quotenumber='QAZSS952918540' " <br>
     * EX: quoteOrPolicyNumber("AZSS952918540" ) returns "AND ps.policynumber='AZSS952918540' " <br>
     * @param quoteOrPolicyNumber quote or policy number to query on
     * @return SQL join statement with appropriate column selection and inserted quote or policy number.
     */
    private static String getQuoteOrPolicyNumberJoinSQL(String quoteOrPolicyNumber) {
        String quoteOrPolicyColumnName = isQuote(quoteOrPolicyNumber) ? "quotenumber" : "policynumber";
        return String.format("AND ps.%1s ='%2s' ", quoteOrPolicyColumnName, quoteOrPolicyNumber);
    }
}
