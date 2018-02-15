package aaa.modules.regression.service.helper;

import java.util.Arrays;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import toolkit.verification.CustomAssert;

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

    public static void policyServiceRfiValuesCheck(RfiDocumentResponse[] result, String rfiName, String documentType, String status) {
        RfiDocumentResponse allDocuments = Arrays.stream(result).filter(doc -> rfiName.equals(doc.documentName)).findFirst().orElse(null);
        CustomAssert.assertTrue(rfiName + " rfiName not existent", allDocuments != null);
        if(!allDocuments.equals(null)) {
            CustomAssert.assertTrue(rfiName + " has incorrect documentType", documentType.equals(allDocuments.documentType));
            CustomAssert.assertTrue(rfiName + " has incorrect status", status.equals(allDocuments.status));
        }
    }

    public static void rfiTagCheck(String query, String tag, String tagValue) {
        CustomAssert.assertEquals(
                tag + " has a problem.", DocGenHelper.getDocumentDataElemByName(tag, DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
                        .getTextField(), tagValue);
    }
}
