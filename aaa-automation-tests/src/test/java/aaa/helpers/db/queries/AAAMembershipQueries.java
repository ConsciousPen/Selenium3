package aaa.helpers.db.queries;

import toolkit.db.DBService;

public class AAAMembershipQueries {

    /**
     * Verified Membership states from PAS.
     */
    public enum AAAMembershipStatus {ACTIVE, LAPSED, CANCELED, FUTURE_DATED, PENDING, Error, No_Hit}

    // BondTODO: Make query for AAABESTMEMBERSHIPSTATUS
    // BondTODO: Make query for ORDERMEMBERSHIPNUMBER

    /**
     * Returns the AAA Membership Member Since Date from DB.
     * @param quoteOrPolicyNumber
     * @return
     */
    public static java.util.Optional<String> GetAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber){
        String query =
                "SELECT MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE " +
                        "FROM policysummary ps " +
                        "JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' " +
                        GetQuoteOrPolicyNumberJoinSQL(quoteOrPolicyNumber) +
                        "JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID";

        return DBService.get().getValue(query);
    }

    /**
     * Changes the Membership status *After* policy was created to specific status.
     * @param policyNumber
     * @param updatedStatus
     */
    public static void UpdateAAAMembershipStatusInSQL(String policyNumber, AAAMembershipStatus updatedStatus){
        // BondToDo: Check with Rajesh if this works for Quotes. Update line 43 to use the GetQuoteOrPolicyNumberJoinSQL() if so.
        String query =
                String.format(
                        "UPDATE membershipsummaryentity mse SET mse.membershipstatus = '%1s' " +
                        "WHERE mse.id IN (" +
                        "SELECT ms.id FROM policysummary ps JOIN membershipsummaryentity ms ON ms.id = ps.membershipsummary_id " +
                                "AND PS.policynumber='%2s')",
                        updatedStatus.name(),   //%1s
                        policyNumber);          //%2s

        DBService.get().executeUpdate(query);
    }

    /**
     * Used to get an appropriate AND column statement using passed in quoteOrPolicyNumber for AAA Membership Queries.
     * EX: quoteOrPolicyNumber("QAZSS952918540") returns "AND ps.quotenumber='QAZSS952918540' "
     * EX: quoteOrPolicyNumber("AZSS952918540" ) returns "AND ps.policynumber='AZSS952918540' "
     * @param quoteOrPolicyNumber
     * @return
     */
    private static String GetQuoteOrPolicyNumberJoinSQL(String quoteOrPolicyNumber){
        String quoteOrPolicyColumnName = quoteOrPolicyNumber.toUpperCase().startsWith("Q") ? "quotenumber" : "policynumber";
        String SQL = String.format("AND ps.%1s ='%2s' ", quoteOrPolicyColumnName, quoteOrPolicyNumber);
        return SQL;
    }
}
