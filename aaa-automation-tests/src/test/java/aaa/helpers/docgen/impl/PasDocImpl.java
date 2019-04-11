package aaa.helpers.docgen.impl;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.docgen.DocumentWrapper;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.xml.XmlHelper;
import aaa.helpers.xml.model.StandardDocumentRequest;
import aaa.helpers.xml.model.pasdoc.Document;
import aaa.helpers.xml.model.pasdoc.DocumentGenerationRequest;
import aaa.main.enums.DocGenEnum;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.ETCSCoreSoftAssertions;

public class PasDocImpl extends DocumentWrapper {
	private static final String SQL_GET_DOC_GEN_FOLDER = "select  value\n"
			+ "from PROPERTYCONFIGURERENTITY\n"
			+ "where propertyname ='aaaDocGenSerializer.exportDocumentLocation'";
	private static String DOCGEN_ROOT_FOLDER = DBService.get().getValue(SQL_GET_DOC_GEN_FOLDER).orElse("null");
	public static String PASDOC_SOURCE_FOLDER = DOCGEN_ROOT_FOLDER + "/pasdoc/outbound";
	private static final int DOCUMENT_GENERATION_TIMEOUT = 40;

	public PasDocImpl(StandardDocumentRequest standardDocumentRequest) {
	}

	public PasDocImpl() {
	}

	private static Logger log = LoggerFactory.getLogger(PasDocImpl.class);

	public static DocumentGenerationRequest getDocumentRequest(String policyNumber, DocGenEnum.Documents... documents) {
		return getDocumentRequest(policyNumber, null, documents);
	}

	public static DocumentGenerationRequest getDocumentRequest(String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents... documents) {
		List<String> documentsFilePaths = waitForDocumentsAppearance(policyNumber, eventName, documents);
		if (documentsFilePaths.size() > 1) {
			log.warn(String
					.format("More than one (%1$s) xml document files were found with quote/policy number \"%2$s\"%3$s:\n%4$s.\nNewest one (last modified) will be used for getting CreateDocuments model.",
							documentsFilePaths.size(),
							policyNumber,
							documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "",
							documentsFilePaths));
		}
		Collections.sort(documentsFilePaths, Collections.reverseOrder());
		String content = RemoteHelper.get().getFileContent(documentsFilePaths.get(0));
		log.info("Getting object model from found xml document: \"{}\".", documentsFilePaths.get(0));

		DocumentGenerationRequest documentGenerationRequest;
		documentGenerationRequest = XmlHelper.xmlToModel(content, DocumentGenerationRequest.class, false);

		return documentGenerationRequest;
	}

	public static List<String> waitForDocumentsAppearance(String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents... documents) {
		List<String> textsToSearchPatterns = new ArrayList<>();

		textsToSearchPatterns.add(String.format("<%1$s:PolicyNumber>%2$s</%1$s:PolicyNumber>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, policyNumber));
		if (eventName != null) {
			textsToSearchPatterns.add(String.format("<%1$s:EventName>%2$s</%1$s:EventName>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, eventName));
		}

		for (int i = 0; i < documents.length; i++) {
			Arrays.asList(documents).stream().forEach(documents1 ->
					textsToSearchPatterns.add(String.format("<%1$s:TemplateId>%2$s</%1$s:TemplateId>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, documents1.getIdInXml()))
			);
		}
		log.info(String.format("Waiting for xml document file(s) appearance with \"%1$s\" policy number%2$s in \"%3$s\" folder.",
				policyNumber, documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "", PASDOC_SOURCE_FOLDER));

		return RemoteHelper.get().waitForDocAppearance(PASDOC_SOURCE_FOLDER, DOCUMENT_GENERATION_TIMEOUT, policyNumber, textsToSearchPatterns);
	}

	public static DocumentGenerationRequest verifyDocumentsGenerated(String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(null, true, false, policyNumber, documents);
	}

	public static DocumentGenerationRequest verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(softly, true, false, policyNumber, documents);
	}

	public static DocumentGenerationRequest verifyDocumentsGenerated(boolean documentsExistence, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(null, documentsExistence, false, policyNumber, documents);
	}

	public static DocumentGenerationRequest verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, boolean documentsExistence, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(softly, documentsExistence, false, policyNumber, documents);
	}

	public static DocumentGenerationRequest verifyDocumentsGenerated(boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(null, documentsExistence, generatedByJob, policyNumber, documents);
	}

	/**
	 * Verifies that all <b>documents</b> exist (or not exist if <b>documentsExistence</b> is false) in found xml file with <b>policyNumber</b> inside after documents generation
	 * (generation should be performed before this method call).
	 *
	 * @param documentsExistence defines expected documents presence or absence verification
	 * @param generatedByJob     if true then file search will be performed in appropriate jobs generation folder location
	 * @param policyNumber       quote or policy number to be used for finding xml document for further documents searching.
	 *                           If more than one file with <b>policyNumber</b> is found then newest one (last modified) will be used for documents searching.
	 * @param documents          array of documents to be searched in found xml file. If array is empty then only existence of <b>policyNumber</b> will be verified.
	 * @return DocumentWrapper object with StandardDocumentRequest object unmarshalled from xml document and search/verification helper methods
	 * @throws AssertionError <p>1) if no xml document(s) with <b>policyNumber</b> inside were found within timeout ({@link #DOCUMENT_GENERATION_TIMEOUT} seconds by default).
	 *                        <p>2) if provided documents are present or absent in found xml model (depending on <b>documentsExistence</b> value).
	 *                        <p>3) if <b>documents</b> array is empty and <b>documentsExistence</b> argument is false.
	 * @throws IstfException  if unmarshalling of found xml file to object model fails.
	 *                        By default strict match check is used, this means exception will be thrown if xml content differs from existing model (e.g. has extra tags)
	 */
	public static DocumentGenerationRequest verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(softly, documentsExistence, generatedByJob, policyNumber, null, documents);
	}

	/**
	 * Verifies that all <b>documents</b> exist (or not exist if <b>documentsExistece</b> is false) in found xml file with <b>policyNumber</b> and <b>EventName</b> inside after documents generation
	 * (generation should be performed before this method call). 
	 *
	 * @param softly             defines using of soft assertions
	 * @param documentsExistence defines expected documents presence or absence verification
	 * @param generatedByJob     if true then file search will be performed in appropriate jobs generation folder location
	 * @param policyNumber       quote or policy number to be used for finding xml document for further documents searching. 
	 *                           If more than one file with <b>policyNumber</b> is found then newest one (last modified) will be used for documents searching
	 * @param eventName          expected EventName tag value in generated xml file
	 * @param documents          array of documents to be searched in found xml file. If array is empty then only existence of <b>policyNumber</b> will be verified
	 * @return DocumentWrapper   object with StandardDocumentRequest object unmarshalled from xml document and search/verification helper methods
	 */
	public static DocumentGenerationRequest verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents... documents) {
		assertThat(documents.length == 0 && !documentsExistence).as("Unable to call method with empty \"documents\" array and false \"documentsExistence\" argument values!").isFalse();

		log.info(String.format("Verifying that document with \"%1$s\" quote/policy number is generated%2$s%3$s.",
				policyNumber, generatedByJob ? " by job" : "",
				documents.length > 0 ? String.format(" and %1$s documents: %2$s", documentsExistence ? "contains all" : "does not contain", Arrays.asList(documents)) : ""));

		DocumentGenerationRequest documentGenerationRequest = getDocumentRequest(policyNumber, eventName, documentsExistence ? documents : new DocGenEnum.Documents[0]);

		for (DocGenEnum.Documents document : documents) {
			String docPolicyNum = documentGenerationRequest.getDocumentData().getPolicyNumber();
			Document doc = documentGenerationRequest.getDocuments().stream().filter(document1 -> document1.getTemplateId().equals(document.getIdInXml())).findFirst().orElse(null);
			String errorMessagePolicy = String.format("Policy number in generated document '%s' doesn't match expected '%s'", docPolicyNum, policyNumber);
			String errorMessageDocId;
			if (doc != null) {
				errorMessageDocId = String.format("Document ID '%s' doesn't match expected '%s'", doc.getTemplateId(), document.getIdInXml());
			} else {
				errorMessageDocId = String.format("Document ID '%s' is present true, but expected false", document.getIdInXml());
			}

			if (softly == null) {
				assertThat(docPolicyNum).as(errorMessagePolicy).isEqualTo(policyNumber);
				if (!documentsExistence) {
					assertThat(doc).as(errorMessageDocId).isNull();
				} else {
					assertThat(doc).as(errorMessageDocId).isNotNull();
					assertThat(doc.getTemplateId()).as(errorMessageDocId).isEqualTo(document.getIdInXml());
				}

			} else {
				softly.assertThat(docPolicyNum).as(errorMessagePolicy).isEqualTo(policyNumber);
				if (!documentsExistence) {
					softly.assertThat(doc).as(errorMessageDocId).isNull();
				} else {
					softly.assertThat(doc).as(errorMessageDocId).isNotNull();
					softly.assertThat(doc.getTemplateId()).as(errorMessageDocId).isEqualTo(document.getIdInXml());
				}

			}
		}

		log.info("Documents generation verification has been successfully passed.");
		return documentGenerationRequest;
	}
}
