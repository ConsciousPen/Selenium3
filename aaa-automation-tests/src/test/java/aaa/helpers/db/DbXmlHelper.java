package aaa.helpers.db;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.DocGenEnum;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.text.StrSubstitutor;
import toolkit.db.DBService;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
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
	public static String getXmlByDocName(@Nonnull DocGenEnum.Documents docId, String selectPolicyData, DocGenEnum.XmlnsDbFormat... xmlns) {
		Map<String, String> params = ImmutableMap.of(
				"DOC_NAME", docId.getIdInXml(),
				"GET_DATA", selectPolicyData,
				"NS_VALUES", Arrays.stream(xmlns).map(DocGenEnum.XmlnsDbFormat::getXmlns).
						collect(Collectors.joining(","))

		);

		String query = StrSubstitutor.replace(GET_PART_OF_XML_BY_XPATH, params);
		return DBService.get().getValue(query).get();
	}

	/**
	 * Select the generated Document xml from Oracle DB by policy number
	 *
	 * @param policyNumber
	 * @param eventName {@link aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames} event that triggered document generation
	 * @return XML content in String format
	 */
	public static String getXmlByPolicyNumber(@Nonnull String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, eventName);
		return DBService.get().getValue(query).get();
	}

	/**
	 * Select all generated Document xmls from Oracle DB by policy number
	 *
	 * @param policyNumber
	 * @param eventName {@link EventNames} event that triggered document generation
	 * @return XML content in String format
	 */
	public static List<Map<String, String>> getXmlsByPolicyNumber(@Nonnull String policyNumber, EventNames eventName) {
		String query = String.format(AaaDocGenEntityQueries.GET_ALL_DOCUMENTS_BY_POLICY_NUMBER, policyNumber, eventName);
		return DBService.get().getRows(query);
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
