package aaa.helpers.docgen;

import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.xml.XmlHelper;
import aaa.helpers.xml.models.CreateDocuments;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;
import aaa.main.enums.DocGenEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocGenHelper {
	public static final String DOCGEN_SOURCE_FOLDER = "/home/DocGen/";
	public static final String DOCGEN_BATCH_SOURCE_FOLDER = DOCGEN_SOURCE_FOLDER + "Batch/";
	public static final String JOBS_DOCGEN_SOURCE_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/";
	private static final int DOCUMENT_GENERATION_TIMEOUT = 30;

	private static Logger log = LoggerFactory.getLogger(DocGenHelper.class);

	/**
	 * Cleanup all doc gen folders
	 */
	public static void clearDocGenFolders() {
		try {
			RemoteHelper.clearFolder(DocGenHelper.JOBS_DOCGEN_SOURCE_FOLDER);
			RemoteHelper.clearFolder(DocGenHelper.DOCGEN_SOURCE_FOLDER);
			RemoteHelper.clearFolder(DocGenHelper.DOCGEN_BATCH_SOURCE_FOLDER);
		} catch (Exception e) {
			Assert.fail("Clearing doc gen folder failed: \n", e);
		}
	}

	public static StandardDocumentRequest verifyDocumentsGenerated(String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(true, false, policyNumber, documents);
	}

	public static StandardDocumentRequest verifyDocumentsGenerated(boolean documentsExistence, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(documentsExistence, false, policyNumber, documents);
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
	 * @return unmarshalled StandardDocumentRequest object from found xml document
	 * @throws AssertionError <p>1) if no xml document(s) with <b>policyNumber</b> inside were found within timeout ({@link #DOCUMENT_GENERATION_TIMEOUT} seconds by default).
	 *                        <p>2) if provided documents are present or absent in found xml model (depending on <b>documentsExistence</b> value).
	 *                        <p>3) if <b>documents</b> array is empty and <b>documentsExistence</b> argument is false.
	 * @throws IstfException  if unmarshalling of found xml file to object model fails.
	 *                        By default strict match check is used, this means exception will be thrown if xml content differs from existing model (e.g. has extra tags)
	 */
	public static StandardDocumentRequest verifyDocumentsGenerated(boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		CustomAssert.assertFalse("Unable to call method with empty \"documents\" array and false \"documentsExistence\" argument values!", documents.length == 0 && !documentsExistence);

		log.info(String.format("Verifying that document with \"%1$s\" quote/policy number is generated%2$s%3$s.",
				policyNumber, generatedByJob ? " by job" : "",
				documents.length > 0 ? String.format(" and %1$s documents: %2$s", documentsExistence ? "contains all" : "does not contain", Arrays.asList(documents)) : ""));

		StandardDocumentRequest standardDocumentRequest = getDocumentRequest(generatedByJob, policyNumber, documentsExistence ? documents : new DocGenEnum.Documents[0]);
		CustomAssert.assertTrue(String.format("Policy number \"%s\" is absent in documents object model.", policyNumber),
				standardDocumentRequest.getDocumentPackages().getDocumentPackages().stream().anyMatch(p -> p.getPackageIdentifier().equals(policyNumber)));

		Map<DocGenEnum.Documents, Boolean> documentsExistenceMap = new HashMap<>(documents.length);
		Arrays.stream(documents).forEach(d -> documentsExistenceMap.put(d, false));
		for (DocGenEnum.Documents document : documents) {
			for (DocumentPackage packages : standardDocumentRequest.getDocumentPackages().getDocumentPackages()) {
				for (Document doc : packages.getDocuments().getDocuments()) {
					if (doc.getTemplateId().equals(document.getIdInXml())) {
						documentsExistenceMap.replace(document, true);
						break;
					}
				}
			}
		}

		List<DocGenEnum.Documents> failedDocsList = documentsExistenceMap.entrySet().stream().filter(d -> d.getValue() != documentsExistence).map(Map.Entry::getKey).collect(Collectors.toList());
		CustomAssert.assertTrue(String.format("Documents are %1$s: %2$s in object model.", documentsExistence ? "absent" : "present", failedDocsList), failedDocsList.isEmpty());
		log.info("Documents generation verification has been successfully passed.");

		return standardDocumentRequest;
	}

	public static StandardDocumentRequest getDocumentRequest(String policyNumber, DocGenEnum.Documents... documents) {
		return getDocumentRequest(false, policyNumber, documents);
	}

	/**
	 * Search xml document by <b>policyNumber</b> text and get <b>StandardDocumentRequest</b> object model from it.
	 *
	 * @param generatedByJob if true then file search will be performed in appropriate jobs generation folder location
	 * @param policyNumber   quote or policy number to be used for finding xml document.
	 *                       If more than one file with <b>policyNumber</b> is found then newest one (last modified) will be used for getting document model.
	 * @return unmarshalled StandardDocumentRequest object from found xml document
	 * @throws AssertionError if no xml document(s) with <b>policyNumber</b> inside were found within timeout ({@link #DOCUMENT_GENERATION_TIMEOUT} seconds by default)
	 * @throws IstfException  if unmarshalling of found xml file to object model fails.
	 *                        By default strict match check is used, this means exception will be thrown if xml content differs from existing model (e.g. has extra tags)
	 */
	public static StandardDocumentRequest getDocumentRequest(boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		List<String> documentsFilePaths = waitForDocumentsAppearance(generatedByJob, policyNumber, documents);
		if (documentsFilePaths.size() > 1) {
			log.warn(String.format("More than one (%1$s) xml document files were found with quote/policy number \"%2$s\"%3$s:\n%4$s.\nNewest one (last modified) will be used for getting StandardDocumentRequest model.",
					documentsFilePaths.size(),
					policyNumber,
					documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "",
					documentsFilePaths));
		}

		String content = RemoteHelper.getFileContent(documentsFilePaths.get(0));
		log.info(String.format("Getting object model from found xml document: \"%s\".", documentsFilePaths.get(0)));
		CreateDocuments createDocuments = XmlHelper.xmlToModel(content, CreateDocuments.class);
		return createDocuments.getStandardDocumentRequest();
	}

	public static List<String> waitForDocumentsAppearance(String policyNumber, DocGenEnum.Documents... documents) {
		return waitForDocumentsAppearance(false, policyNumber, documents);
	}

	/**
	 * Wait for generated document(s) appearance containing <b>policyNumber</b> and all documents ids from <b>documents<b/> array and return absolute paths list of found files in chronological order (latest one comes first)
	 *
	 * @param generatedByJob if true then file(s) search will be performed in appropriate jobs generation folder location
	 * @param policyNumber   quote or policy number to be used for finding xml document(s).
	 * @param documents      documents ids to be used for finding xml document(s). If array is empty then search by <b>policyNumber</b> will be performed only.
	 * @return list with absolute paths of found xml documents with <b>policyNumber</b> and documents ids inside in chronological order (latest one comes first)
	 * @throws AssertionError if no xml document(s) were found within timeout ({@link #DOCUMENT_GENERATION_TIMEOUT} seconds by default)
	 */
	public static List<String> waitForDocumentsAppearance(boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		String docGenSourcePath = generatedByJob ? JOBS_DOCGEN_SOURCE_FOLDER : DOCGEN_SOURCE_FOLDER;
		log.info(String.format("Waiting for xml document file(s) appearance with \"%1$s\" policy number%2$s in \"%3$s\" folder.",
				policyNumber, documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "", docGenSourcePath));

		String[] textsToSearchPatterns = new String[documents.length + 1];
		textsToSearchPatterns[0] = String.format("<%1$s:PackageIdentifier>%2$s</%1$s:PackageIdentifier>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, policyNumber);
		for (int i = 0; i < documents.length; i++) {
			textsToSearchPatterns[i + 1] = String.format("<%1$s:TemplateId>%2$s</%1$s:TemplateId>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, documents[i].getIdInXml());
		}
		return RemoteHelper.waitForFilesAppearance(docGenSourcePath, "xml", DOCUMENT_GENERATION_TIMEOUT, textsToSearchPatterns);
	}
}
