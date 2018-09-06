package aaa.helpers.db.queries;

import toolkit.db.DBService;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AAAMembershipQueries {

    /**
     * Use this formatter to load SQL date/time results that have not been truncated, into a LocalDateTime instance. <br>
     * Example: <br>
     * | String dbMemberSinceDate = getAAAMemberSinceDateFromSQL(quoteNumber).orElse("Null Value"); <br>
     * | LocalDateTime memberSinceDateTime = LocalDateTime.parse(dbMemberSinceDate, SQLDateTimeFormatter); <br>
     * <br>
     */
    public static final DateTimeFormatter SQLDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
     * @return an optional AAABestMembershipStatus. If no DB rows come back, will be null.
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
     * @return an optional String. If no DB rows come back, will be null.
     */
    public static java.util.Optional<String> getAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber) {
        String query = getStandardMembershipQuery("MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE", quoteOrPolicyNumber);
        return DBService.get().getValue(query);
    }

    /**
     * Returns the AAA Order Membership Number from DB. <br>
     * ORDERMEMBERSHIPNUMBER is the response from Elastic Search
     * @param quoteOrPolicyNumber is the quote or policy number to query against.
     * @return an optional String. If no DB rows come back, will be null.
     */
    public static java.util.Optional<String> getAAAOrderMembershipNumberFromSQL(String quoteOrPolicyNumber) {
        String query = getStandardMembershipQuery("MS.ORDERMEMBERSHIPNUMBER", quoteOrPolicyNumber);
        return DBService.get().getValue(query);
    }

    /**
     * Returns the Policy Effective Date from DB. <br>
     * @param policyNumber is the policy number to query against.
     * @return an optional String. If no DB rows come back, will be null.
     * @throws IllegalArgumentException if a quote number is provided.
     */
    public static java.util.Optional<String> getPolicyEffectiveDateFromSQL(String policyNumber) throws IllegalArgumentException {

        if (isQuote(policyNumber)){
            throw new IllegalArgumentException(
                    "getMembershipEffectiveDataFromSQL() only accepts policy numbers. Attempted policy: " + policyNumber);
        }

        String query = getStandardMembershipQuery("ps.EFFECTIVE", policyNumber);
        return DBService.get().getValue(query);
    }

    /**
     * Changes the AAA Best Membership Status *After* policy was created to specific status. <br>
     * Does NOT support Quotes.
     * @param policyNumber is the policy number to query against.
     * @param updatedStatus is what AAA Membership Status to set in the database.
     * @throws IllegalArgumentException When given a Quote Number.
     */
    public static void updateAAABestMembershipStatusInSQL(String policyNumber, AAABestMembershipStatus updatedStatus)
            throws IllegalArgumentException {

        if (isQuote(policyNumber)) {
            throw new IllegalArgumentException("updateAAABestMembershipStatusInSQL() does not support Quotes. " +
                    "Arg policyNumber: " + policyNumber);
        }

        String query = getStandardMSEUpdateSQL("mse.aaabestmembershipstatus", updatedStatus.name(),
                policyNumber, 1);

        DBService.get().executeUpdate(query);
    }

    /**
     * Changes the Membership Status *After* policy was created to specific status. <br>
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

        String query = getStandardMSEUpdateSQL("mse.membershipstatus", updatedStatus.name(),
                policyNumber, 1);

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
     * Returns the standard membership summary entity db update query for the column and appropriate quote/policy joined on.
     * @param updateColumn is the column name to update the value of. EX: membershipsummaryentity
     * @param updateData is the String Value to insert into that column in the database.
     * @param quoteOrPolicyNumber is the quote or policy number to update rows against.
     * @param limitUpdateToNumberOfRows is the number of rows to update. Null or less than 1 updates ALL rows.
     *                                  Rows are in descending order so newest rows are first.
     * @return SQL Query that will update specific requested column with updateData based on quoteOrPolicyNumber
     */
    private static String getStandardMSEUpdateSQL(String updateColumn, String updateData, String quoteOrPolicyNumber,
                                                  Integer limitUpdateToNumberOfRows) throws IllegalArgumentException{

        // Validate column to update is in Membership Summary Entity table.
        if (!updateColumn.toLowerCase().startsWith("mse.")){
            throw new IllegalArgumentException(
                    "getStandardMSEUpdateSQL() only works with membership summary entity columns. " +
                            "Column must be prefixed from the membership summary entity table (mse.).");
        }

        // If limitUpdateToNumberOfRows is null or less than 1, update all rows.
        String rowLimiterLine = "";

        if (limitUpdateToNumberOfRows == 1){
            rowLimiterLine = "fetch first row only";
        }

        if (limitUpdateToNumberOfRows > 1){
            rowLimiterLine = "fetch first " + limitUpdateToNumberOfRows.toString() + "rows only";
        }

        String query =
                String.format(
                        "UPDATE membershipsummaryentity mse SET %1s = '%2s' " +
                                "WHERE mse.id IN (" +
                                "SELECT ms.id FROM policysummary ps " +
                                "JOIN membershipsummaryentity ms ON ms.id = ps.membershipsummary_id " +
                                "AND PS.policynumber='%3s' " +
                                "ORDER BY ps.EFFECTIVE DESC " + // Makes sure first returned row is latest DB Entry.
                                "%4s)",
                        updateColumn,           //%1s
                        updateData,             //%2s
                        quoteOrPolicyNumber,    //%3s
                        rowLimiterLine );       //%4s
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
