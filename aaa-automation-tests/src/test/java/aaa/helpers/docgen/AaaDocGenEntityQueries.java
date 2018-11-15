package aaa.helpers.docgen;

public interface AaaDocGenEntityQueries {
	String GET_DOCUMENT_BY_EVENT_NAME = "select data from( \n"
			+ "    select data from aaadocgenentity doc, policysummary ps\n"
			+ "    where 1=1 \n"
			+ "    and ps.id = doc.entityid\n"
			+ "    and ps.policynumber = '%s'\n"
			+ "    and doc.data like '%%%s%%' \n"
			+ "    and doc.eventname like '%s' \n"
			+ "    order by doc.creationdate desc) \n"
			+ "where rownum=1 ";

	String GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME = "select count(*) from aaadocgenentity " +
			"where 1=1 " +
			"and data like '%%%s%%' " +
			"and data like '%%%s%%' " +
			"and eventname like '%s'";

	String GET_PART_OF_XML_BY_XPATH = "select EXTRACT(xmltype(DATA), '//doc:TemplateId[contains(text(),\"${DOC_NAME}\")]/..',\n" +
			"'${NS_VALUES}').getClobVal() value \n" +
			"from (${GET_DATA})";

	String GET_DOCUMENT_BY_POLICY_NUMBER = "SELECT  data\n"
			+ "FROM ( SELECT data \n"
			+ "FROM aaadocgenentity doc INNER JOIN policysummary s ON s.id = doc.entityid\n"
			+ "WHERE  s.policynumber = '%s' AND doc.eventname LIKE '%s'\n"
			+ "ORDER BY doc.creationdate DESC\n"
			+ " ) WHERE ROWNUM = 1 ";

	String GET_ALL_DOCUMENTS_BY_POLICY_NUMBER = "SELECT  data\n"
			+ "FROM ( SELECT data \n"
			+ "FROM aaadocgenentity doc INNER JOIN policysummary s ON s.id = doc.entityid\n"
			+ "WHERE  s.policynumber = '%s' AND doc.eventname LIKE '%s'\n"
			+ "ORDER BY doc.creationdate DESC\n"
			+ " )";

	enum EventNames {
		RENEWAL_OFFER, PREMIUM_CALCULATED, CANCEL_MIDTERM, ENDORSEMENT_ISSUE, ADHOC_DOC_GENERATE, POLICY_ISSUE, PRE_RENEWAL, ADHOC_DOC_ON_DEMAND_GENERATE, RENEWAL_BILL, AUTO_PAY_METNOD_CHANGED, MORTGAGEE_BILL_FINAL_EXP_NOTICE, BILL_FIRST_RENEW_REMINDER_NOTICE, EXPIRATION_NOTICE
	}

}
