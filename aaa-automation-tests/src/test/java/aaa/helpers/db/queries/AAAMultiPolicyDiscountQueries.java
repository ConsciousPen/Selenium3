package aaa.helpers.db.queries;

import toolkit.db.DBService;

import javax.annotation.Nonnull;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AAAMultiPolicyDiscountQueries {
    //Select ps.txtype from policysummary ps where ps.policynumber in ('AZSS952918557');
    //  Select ps.mpdvalidationstatus, Ps.txtype from policysummary ps where ps.policynumber in ('AZSS952918557');
    private static String SELECT_MPD_STATUS = "SELECT ps.mpdvalidationstatus, Ps.txtype from policysummary ps where ps.= '%s' " ;
    //TODO: THIS query needs fixed for the escape characters
    private static String SELECT_MPD_COUNT = "Select count(*)\n" +
            "from policysummary ps  LEFT JOIN OtherOrPriorPolicy o\n" +
            " ON ps.policydetail_id=o.policydetail_id  where PS.TXTYPE='endorsement' and ps.mpdvalidationstatus='FOUND_STG1' and ps.policynumber in '%s'";

    /**
     * Use this formatter to load SQL date/time results that have not been truncated, into a LocalDateTime instance. <br>
     * Example: <br>
     * | String dbMemberSinceDate = getAAAMemberSinceDateFromSQL(quoteNumber).orElse("Null Value"); <br>
     * | LocalDateTime memberSinceDateTime = LocalDateTime.parse(dbMemberSinceDate, SQLDateTimeFormatter); <br>
     * <br>
     */
    public static final DateTimeFormatter SQLDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Verified Multi-Policy Discount status from PAS.
     */
    public enum AAAMultipolicyDiscountStatus {
        FOUND_STG1, NOTFOUND_STG1, ERROR_STG1
    }

    public static List<Map<String, String>> getMPDVStatusandTxTypeFromDB(@Nonnull String policyNumber) {
        String query = String.format(SELECT_MPD_STATUS, policyNumber);
        return DBService.get().getRows(query);
    }
}
