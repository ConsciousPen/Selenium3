package aaa.helpers.db.queries;

import toolkit.db.DBService;

public class AAAMembershipQueries {

    /**
     * Returns the AAA Membership Member Since Date from DB.
     * @param quoteOrPolicyNumber
     * @return
     */
    public static java.util.Optional<String> GetAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber){

        String quoteOrPolicyColumn = quoteOrPolicyNumber.toUpperCase().startsWith("Q") ? "quotenumber" : "policynumber";

        String columnToJoinOn = String.format("AND ps.%1s ='%2s' ", quoteOrPolicyColumn, quoteOrPolicyNumber);

        String query =
                "SELECT MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE " +
                        "FROM policysummary ps " +
                        "JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' " +
                        columnToJoinOn +
                        "JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID";

        return DBService.get().getValue(query);
    }
}
