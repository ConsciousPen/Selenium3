package aaa.helpers.db.queries;

import toolkit.db.DBService;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AAAMultiPolicyDiscountQueries {

    private static String SELECT_MPD_STATUS = "SELECT ps.mpdvalidationstatus from policysummary ps where ps.policynumber in '%s' order by ps.id desc" ;
    private static String SELECT_MPD_ENDO_COUNT = "Select count(*)from policysummary ps where ps.policynumber='%s' and ps.txtype='endorsement'" ;
    private static String SELECT_MPD_DECPAGE_ENDO_COUNT = "select count (*) from AAADOCGENENTITY where ENTITYID in (Select id from policySummary where POLICYNUMBER='%s')  and eventname ='ENDORSEMENT_ISSUE' order by id desc" ;


    private static String SELECT_MPD_ENDO_AND_DECPAGE_COUNT ="select count(*) from AAADOCGENENTITY where ENTITYID in (Select id from policySummary where POLICYNUMBER='%s') " +
            "and eventname ='%s' " +
            "and instr(DATA,'%s') >0 " +
            "AND instr(DATA,'%s')>0" ;


    public static Optional<String> getMPDVEndoAndDocgenFromDB(@Nonnull String policyNumber, String eventName, String document, String discount) {
        String query = String.format(SELECT_MPD_ENDO_AND_DECPAGE_COUNT, policyNumber, eventName, document, discount);
        return DBService.get().getValue(query);
    }

    public static Optional<String> getMPDVStatusDB(@Nonnull String policyNumber) {
        String query = String.format(SELECT_MPD_STATUS, policyNumber);
        return DBService.get().getValue(query);
    }

    public static Optional<String> getMPDVEndoCountFromDB(@Nonnull String policyNumber) {
        String query = String.format(SELECT_MPD_ENDO_COUNT, policyNumber);
        return DBService.get().getValue(query);
    }

    public static Optional<String> getMPDVDocgenFromDB(@Nonnull String policyNumber) {
        String query = String.format(SELECT_MPD_DECPAGE_ENDO_COUNT, policyNumber);
        return DBService.get().getValue(query);
    }
}