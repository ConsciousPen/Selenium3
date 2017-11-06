package aaa.helpers.db;


import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.DocGenEnum;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.text.StrSubstitutor;
import toolkit.db.DBService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DbXmlHelper implements AaaDocGenEntityQueries {


    /**
     * Select part of Document Generated xml from Oracle DB by Xpath
     * @param docId generated Document Id
     * @param selectPolicyData query which returns CLOB data
     * @param xmlns Name Spaces which uses in Xpath
     * @return XML content in String format
     */
    public static String getXmlByDocName(DocGenEnum.Documents docId, String selectPolicyData, DocGenEnum.XmlnsDbFormat... xmlns) {
        Map<String, String> params = ImmutableMap.of(
                "DOC_NAME", docId.getId(),
                "GET_DATA", selectPolicyData,
                "NS_VALUES", Arrays.stream(xmlns).map(DocGenEnum.XmlnsDbFormat::getXmlns).
                        collect(Collectors.joining(","))

        );

        String query = StrSubstitutor.replace(getPartOfXmlByXpath, params);
        return DBService.get().getValue(query).get();
    }

    /**
     * Select part of Document Generated xml from Oracle DB by Xpath
     * Includes all Name Spaces
     * @param docId generated Document Id
     * @param selectPolicyData query which returns CLOB data
     * @return XML content in String format
     */
    public static String getXmlByDocName(DocGenEnum.Documents docId, String selectPolicyData) {
        return getXmlByDocName(docId, selectPolicyData, DocGenEnum.XmlnsDbFormat.values());
    }




}
