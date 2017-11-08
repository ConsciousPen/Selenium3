package aaa.helpers.docgen;

public interface AaaDocGenEntityQueries {
    String GET_DOCUMENT_BY_EVENT_NAME = "select data from( " +
            "select data from aaadocgenentity " +
            "where 1=1 " +
            "and data like '%%%s%%' " +
            "and data like '%%%s%%' " +
            "and eventname like '%s' " +
            "order by id desc) " +
        "where rownum=1 " ;

    String GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME = "select count(*) from aaadocgenentity " +
            "where 1=1 " +
            "and data like '%%%s%%' " +
            "and data like '%%%s%%' " +
            "and eventname like '%s'";
}
