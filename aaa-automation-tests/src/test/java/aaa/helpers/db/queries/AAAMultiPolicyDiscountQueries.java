package aaa.helpers.db.queries;

import toolkit.db.DBService;

import javax.annotation.Nonnull;
import java.util.Optional;

public class AAAMultiPolicyDiscountQueries {

    private static String SELECT_MPD_STATUS = "SELECT ps.mpdvalidationstatus from policysummary ps where ps.policynumber in '%s' order by ps.id desc" ;
    //TODO: THIS query needs fixed for the escape characters IF NEEDED
    private static String SELECT_MPD_COUNT = "Select count(*)\n" +
            "from policysummary ps  LEFT JOIN OtherOrPriorPolicy o\n" +
            " ON ps.policydetail_id=o.policydetail_id  where PS.TXTYPE='endorsement' and ps.mpdvalidationstatus='FOUND_STG1' and ps.policynumber in '%s'";

    public static Optional<String> getMPDVStatusandTxTypeFromDB(@Nonnull String policyNumber) {
        String query = String.format(SELECT_MPD_STATUS, policyNumber);
        return DBService.get().getValue(query);
    }
}