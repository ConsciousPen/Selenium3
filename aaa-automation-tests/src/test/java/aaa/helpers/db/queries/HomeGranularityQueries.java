package aaa.helpers.db.queries;

public class HomeGranularityQueries {
    public static final String SELECT_CENSUS_BLOCK_GROUP =  "select ps.id, ps.txtype, ps.effective, ce.latitude, ce.longitude, dd.shapefilename, dd.censusblock from policysummary ps\n" +
            "join riskitem ri on ri.policydetail_id = ps.policydetail_id\n" +
            "join DwellDetail dd on dd.id = ri.DwellDetail_ID\n" +
            "join Location l on l.id = ri.riskItem_ID\n" +
            "join ContactEntity ce on ce.id = l.AddressContactEntity_ID\n" +
            "where ps.policynumber = '%1$s'\n" +
            "order by id desc";
}
