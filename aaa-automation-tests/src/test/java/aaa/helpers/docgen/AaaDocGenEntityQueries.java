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

    String GET_PART_OF_XML_BY_XPATH = "select EXTRACT(xmltype(DATA), '//doc:TemplateId[contains(text(),\"${DOC_NAME}\")]/..',\n" +
            "'${NS_VALUES}').getClobVal() value \n" +
            "from (${GET_DATA})";
    String GET_DOCUMENT_BY_POLICY_NUMBER = "SELECT data \n"
            + "FROM aaadocgenentity doc INNER JOIN policysummary s ON s.id = doc.entityid\n"
            + "WHERE  s.policynumber = '%s' AND doc.eventname LIKE '%s'";

    enum EventNames{
        RENEWAL_OFFER,PREMIUM_CALCULATED,CANCEL_MIDTERM,ENDORSEMENT_ISSUE,ADHOC_DOC_GENERATE,POLICY_ISSUE
    }

}
