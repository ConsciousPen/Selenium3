package aaa.helpers.db.queries;

public class HomeGranularityQueries {
    public static final String SELECT_CENSUS_BLOCK_GROUP =  "select ps.id, ps.txtype, ps.effective, dd.shapefilename, dd.censusblock from policysummary ps\n" +
            "join riskitem ri on ri.policydetail_id = ps.policydetail_id\n" +
            "join DwellDetail dd on dd.id = ri.DwellDetail_ID\n" +
            "where ps.policynumber = '%1$s'\n" +
            "order by id desc";
}