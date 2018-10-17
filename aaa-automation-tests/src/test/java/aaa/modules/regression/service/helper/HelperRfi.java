package aaa.modules.regression.service.helper;

import java.util.Arrays;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.rest.dtoAdmin.RfiDocumentResponse;
import aaa.main.enums.DocGenEnum;
import toolkit.verification.ETCSCoreSoftAssertions;

public class HelperRfi {
	public static final String UPDATE_DOCUMENT_STATUS = "UPDATE SUPPORTINGDOCENTITY " +
			"SET DOCUMENTSTATUS = 'Archived', DOCUMENTUPDATETIME=TO_DATE('%s', 'MM/dd/yyyy')  " +
			"WHERE DOCUMENTTEXT = '%s' " +
			"AND AAAAUTOPOLICY_ID IN ( " +
			"SELECT ID FROM POLICYSUMMARY WHERE POLICYNUMBER='%s')";
	public static final String GET_POLICY_SUMMARY_FIELD = "select %s from(\n"
			+ "select * \n"
			+ "from policysummary \n"
			+ "where POLICYNUMBER = '%s' \n"
			+ "order by id desc\n"
			+ ") where rownum=1";
	public static final String UPDATE_POLICY_VERSION = "update POLICYSUMMARY set version = %s where id = '%s'";

	public static void policyServiceRfiValuesCheck(RfiDocumentResponse[] result, String rfiName, String documentType, String status, ETCSCoreSoftAssertions softly) {
		RfiDocumentResponse allDocuments = Arrays.stream(result).filter(doc -> rfiName.equals(doc.documentName)).findFirst().orElse(null);
		softly.assertThat(allDocuments).as(rfiName + " rfiName not existent").isNotNull();
		if (allDocuments != null) {
			softly.assertThat(documentType).as(rfiName + " has incorrect documentType").isEqualTo(allDocuments.documentType);
			softly.assertThat(status).as(rfiName + " has incorrect status").isEqualTo(allDocuments.status);
		}
	}

	public static void rfiTagCheck(String query, String tag, String tagValue, ETCSCoreSoftAssertions softly) {
		softly.assertThat(DocGenHelper.getDocumentDataElemByName(tag, DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
				.getTextField()).as(tag + " has a problem.").isEqualTo(tagValue);
	}
}
