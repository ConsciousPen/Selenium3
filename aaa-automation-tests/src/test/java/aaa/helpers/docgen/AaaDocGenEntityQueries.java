package aaa.helpers.docgen;

public interface AaaDocGenEntityQueries {
    String GET_DOCUMENT_BY_EVENT_NAME = "select data from aaadocgenentity " +
            "where 1=1 " +
            "and data like '%%%s%%' " +
            "and data like '%%%s%%' " +
            "and eventname = '%s'";
}
