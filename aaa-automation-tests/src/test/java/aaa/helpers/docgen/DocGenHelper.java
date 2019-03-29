package aaa.helpers.docgen;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import aaa.common.enums.Constants;
import aaa.config.CsaaTestProperties;
import aaa.helpers.db.DbXmlHelper;
import aaa.helpers.docgen.impl.DocGenImpl;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.docgen.searchNodes.SearchBy;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.xml.XmlHelper;
import aaa.helpers.xml.model.*;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

public class DocGenHelper {
	private static final String SQL_GET_DOC_GEN_FOLDER = "select  value\n"
			+ "from PROPERTYCONFIGURERENTITY\n"
			+ "where propertyname ='aaaDocGenSerializer.exportDocumentLocation'";
	private static String DOCGEN_ROOT_FOLDER = DBService.get().getValue(SQL_GET_DOC_GEN_FOLDER).orElse("null");
	private static final String DOCGEN_JOB_FOLDER = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER, "/home/mp2/pas/sit/");
	public static final String DOCGEN_BATCH_SOURCE_FOLDER = DOCGEN_ROOT_FOLDER + "Batch/";
	public static final String JOBS_DOCGEN_SOURCE_FOLDER = DOCGEN_JOB_FOLDER + "PAS_B_EXGPAS_DCMGMT_6500_D/outbound/";
	public static final DateTimeFormatter DATE_TIME_FIELD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX");
	private static final int DOCUMENT_GENERATION_TIMEOUT = 40;

	private static Logger log = LoggerFactory.getLogger(DocGenHelper.class);

	/**
	 * Cleanup all doc gen folders
	 */
	public static void clearDocGenFolders() {
		try {
			RemoteHelper.get()
					.clearFolder(JOBS_DOCGEN_SOURCE_FOLDER)
					.clearFolder(DOCGEN_JOB_FOLDER)
					.clearFolder(DOCGEN_BATCH_SOURCE_FOLDER);
		} catch (RuntimeException e) {
			CustomAssertions.fail("Clearing doc gen folder failed: \n", e);
		}
	}

	public static DocumentWrapper verifyDocumentsGenerated(String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(null, true, false, policyNumber, documents);
	}

	public static DocumentWrapper verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(softly, true, false, policyNumber, documents);
	}

	public static DocumentWrapper verifyDocumentsGenerated(boolean documentsExistence, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(null, documentsExistence, false, policyNumber, documents);
	}

	public static DocumentWrapper verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, boolean documentsExistence, String policyNumber, DocGenEnum.Documents... documents) {
		return verifyDocumentsGenerated(softly, documentsExistence, false, policyNumber, documents);
	}

	public static DocumentWrapper verifyDocumentsGenerated(boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
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
	public static DocumentWrapper verifyDocumentsGenerated(ETCSCoreSoftAssertions softly, boolean documentsExistence, boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		//checkPasDocEnabled(policyNumber);
		if (isPasDocEnabled(policyNumber)) {
			log.info(String.format("PasDoc is enabled for product and state combination: " + policyNumber + "."));
			PasDocImpl.verifyDocumentsGenerated(softly, documentsExistence, generatedByJob, policyNumber, documents);
			return new PasDocImpl();
		}
		assertThat(documents.length == 0 && !documentsExistence).as("Unable to call method with empty \"documents\" array and false \"documentsExistence\" argument values!").isFalse();

		log.info(String.format("Verifying that document with \"%1$s\" quote/policy number is generated%2$s%3$s.",
				policyNumber, generatedByJob ? " by job" : "",
				documents.length > 0 ? String.format(" and %1$s documents: %2$s", documentsExistence ? "contains all" : "does not contain", Arrays.asList(documents)) : ""));

		int searchRetryDelay = 5;
		int searchAttempt = 1;
		DocumentWrapper documentWrapper = getDocumentRequest(generatedByJob, policyNumber, documentsExistence ? documents : new DocGenEnum.Documents[0]);
		while (documentsExistence && searchAttempt < 3 && !isRequestValid(documentWrapper, policyNumber, documents)) {
			searchAttempt++;
			log.warn(String
					.format("Found documents are related to other policy number(s), probably desired document is not generated yet, performing search attempt #%1$s after %2$s seconds...", searchAttempt, searchRetryDelay));
			try {
				TimeUnit.SECONDS.sleep(searchRetryDelay);
			} catch (InterruptedException e) {
				log.debug(e.getMessage());
			}
			documentWrapper = getDocumentRequest(generatedByJob, policyNumber, documents);
		}

		for (DocGenEnum.Documents document : documents) {
			if (softly == null) {
				documentWrapper.verify.exists(documentsExistence, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber).document.templateId(document.getIdInXml()));
			} else {
				documentWrapper.verify.exists(documentsExistence, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber).document.templateId(document.getIdInXml()), softly);
			}
		}

		log.info("Documents generation verification has been successfully passed.");
		return documentWrapper;
	}

	/**
	 * Search xml document by <b>policyNumber</b> text and get <b>StandardDocumentRequest</b> object model from it.
	 *
	 * @param generatedByJob if true then file search will be performed in appropriate jobs generation folder location
	 * @param policyNumber   quote or policy number to be used for finding xml document.
	 *                       If more than one file with <b>policyNumber</b> is found then newest one (last modified) will be used for getting document model.
	 * @return DocumentWrapper object with StandardDocumentRequest object unmarshalled from xml document and search/verification helper methods
	 * @throws AssertionError if no xml document(s) with <b>policyNumber</b> inside were found within timeout ({@link #DOCUMENT_GENERATION_TIMEOUT} seconds by default)
	 * @throws IstfException  if unmarshalling of found xml file to object model fails.
	 *                        By default strict match check is used, this means exception will be thrown if xml content differs from existing model (e.g. has extra tags)
	 */
	public static DocumentWrapper getDocumentRequest(boolean generatedByJob, String policyNumber, DocGenEnum.Documents... documents) {
		List<String> documentsFilePaths = waitForDocumentsAppearance(generatedByJob, policyNumber, documents);
		if (documentsFilePaths.size() > 1) {
			log.warn(String
					.format("More than one (%1$s) xml document files were found with quote/policy number \"%2$s\"%3$s:\n%4$s.\nNewest one (last modified) will be used for getting CreateDocuments model.",
							documentsFilePaths.size(),
							policyNumber,
							documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "",
							documentsFilePaths));
		}

		String content = RemoteHelper.get().getFileContent(documentsFilePaths.get(0));
		log.info("Getting object model from found xml document: \"{}\".", documentsFilePaths.get(0));

		StandardDocumentRequest standardDocumentRequest;
		if (generatedByJob) {
			standardDocumentRequest = XmlHelper.xmlToModel(content, StandardDocumentRequest.class);
		} else {
			standardDocumentRequest = XmlHelper.xmlToModel(content, CreateDocuments.class, false).getStandardDocumentRequest();
		}

		return new DocGenImpl(standardDocumentRequest);
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
		String docGenSourcePath = generatedByJob ? JOBS_DOCGEN_SOURCE_FOLDER : DOCGEN_ROOT_FOLDER;
		log.info(String.format("Waiting for xml document file(s) appearance with \"%1$s\" policy number%2$s in \"%3$s\" folder.",
				policyNumber, documents.length > 0 ? " and documents: " + Arrays.asList(documents) : "", docGenSourcePath));

		String[] textsToSearchPatterns = new String[documents.length + 1];
		textsToSearchPatterns[0] = String.format("<%1$s:PackageIdentifier>%2$s</%1$s:PackageIdentifier>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, policyNumber);
		for (int i = 0; i < documents.length; i++) {
			textsToSearchPatterns[i + 1] = String.format("<%1$s:TemplateId>%2$s</%1$s:TemplateId>", DocGenEnum.XmlnsNamespaces.DOC_PREFIX, documents[i].getIdInXml());
		}
		return RemoteHelper.get().waitForFilesAppearance(docGenSourcePath, "xml", DOCUMENT_GENERATION_TIMEOUT, textsToSearchPatterns);
	}

	public static String convertToZonedDateTime(LocalDateTime date) {
		String zoneId = RemoteHelper.get().getServerTimeZone();
		return date.atZone(ZoneId.of(zoneId)).format(DATE_TIME_FIELD_FORMAT);
	}


	/**
	 * Extracts data from Document model
	 * Extract only Data Sections which have corresponding sectionName Tag
	 *
	 * @param sectionName      section name to select
	 * @param docId            generated Document Id
	 * @param selectPolicyData query which returns CLOB data
	 */
	@Deprecated
	public static List<DocumentDataSection> getDocumentDataSectionsByName(String sectionName, DocGenEnum.Documents docId, String selectPolicyData) {
		Document doc = getDocument(docId, selectPolicyData);
		return doc.getDocumentDataSections().stream().
				filter(v -> v.getSectionName().equals(sectionName)).collect(Collectors.toList());
	}

	/**
	 * Extracts data from Document model
	 * Extract only Data Sections which contains dataElemName
	 * Data Section will contains only node with expected dataElemName
	 *
	 * @param dataElemName     elem Name which will be in the section
	 * @param docId            generated Document Id
	 * @param selectPolicyData query which returns CLOB data
	 */
	@Deprecated
	public static List<DocumentDataSection> getDocumentDataElemByName(String dataElemName, DocGenEnum.Documents docId, String selectPolicyData) {
		Document doc = getDocument(docId, selectPolicyData);
		doc.getDocumentDataSections().forEach(v1 -> v1.setDocumentDataElements(v1.getDocumentDataElements().stream().
				filter(inner -> inner.getName().equals(dataElemName)).collect(Collectors.toList())));
		return doc.getDocumentDataSections().stream().filter(list -> !list.getDocumentDataElements().isEmpty()).
				collect(Collectors.toList());
	}

	/**
	 * Find first specific element tag value for package section of the policy by policyNumber and generated by eventName event.
	 *
	 * Todo: This is a temporary solution since now we don't have a solid approach for document verification.
	 *
	 * @param policyNumber - policy number for which document was generated
	 * @param sectionName - section name to search in
	 * @param tag - tag to get value from
	 * @param eventName - event name which triggered the document
	 * @return tag value
	 */
	@Deprecated
	public static String getPackageDataElemByName(String policyNumber, String sectionName, String tag, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		List<DocumentDataSection> documentDataSection = getDocumentPackage(policyNumber, eventName).getDocumentPackageData().getDocumentDataSection();
		return documentDataSection.stream()
				.filter(a -> a.getSectionName().equals(sectionName))
				.findFirst().orElseThrow(() -> new NoSuchFieldException("Section " + sectionName + " not found."))
				.getDocumentDataElements().stream()
				.filter(b -> b.getName().equals(tag))
				.findFirst().orElseThrow(() -> new NoSuchFieldException("Tag " + tag + " not found."))
				.getDataElementChoice().getTextField();
	}

	/**
	 * Get All Documents from Document Package List
	 * @param allDocumentPackages getAllDocumentPackages()
	 * @return List<Document>
	 */
	@Deprecated
	public static List<String> getPackageDataElementsByNameFromDocumentPackageList(List<DocumentPackage> allDocumentPackages, String sectionName, String tag) throws NoSuchFieldException {
		List<String> dataElements = new ArrayList<>();
		for (DocumentPackage documentPackage : allDocumentPackages) {
			List<DocumentDataSection> documentDataSection = documentPackage.getDocumentPackageData().getDocumentDataSection();
			dataElements.add(documentDataSection.stream()
					.filter(a -> a.getSectionName().equals(sectionName))
					.findFirst().orElseThrow(() -> new NoSuchFieldException("Section " + sectionName + " not found."))
					.getDocumentDataElements().stream()
					.filter(b -> b.getName().equals(tag))
					.findFirst().orElseThrow(() -> new NoSuchFieldException("Tag " + tag + " not found."))
					.getDataElementChoice().getTextField());
		}
		return dataElements;
	}

	/**
	 * Extracts list of documents from {@link DocumentPackage} model
	 *
	 * @param policyNumber
	 * @param eventName    {@link AaaDocGenEntityQueries.EventNames} event that triggered document generation
	 */
	@Deprecated
	public static List<Document> getDocumentsList(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		DocumentPackage docPackage = getDocumentPackage(policyNumber, eventName);
		return docPackage.getDocuments();
	}

	/**
	 * Find DataElem in the document
	 *
	 * @param dataElemName elem Name which will be in the section
	 * @param document     generated Document
	 */
	@Deprecated
	public static DocumentDataElement getDocumentDataElemByName(String dataElemName, Document document) {
		List<DocumentDataSection> sections = document.getDocumentDataSections().stream()
				.filter(section -> section.getDocumentDataElements().stream()
						.anyMatch(elem -> elem.getName().equals(dataElemName))).collect(Collectors.toList());
		assertThat(sections.size()).as(MessageFormat.format("More than one element \"{0}\" found.", dataElemName)).isLessThanOrEqualTo(1);

		return sections.stream().findFirst().get().getDocumentDataElements().stream().filter(elem -> elem.getName().equals(dataElemName)).findFirst().orElse(null);
	}

	/**
	 * Wait for document(s) request appearance in database for specific <b>docId</b> with timeout {@link DocGenHelper#DOCUMENT_GENERATION_TIMEOUT}
	 *
	 * @param docId       documents ids to be used for waiting xml document.
	 * @param quoteNumber quote/policy number
	 * @param eventName   event name of the generated document
	 */
	@Deprecated
	public static Document waitForDocumentsAppearanceInDB(DocGenEnum.Documents docId, String quoteNumber, AaaDocGenEntityQueries.EventNames eventName) {
		return waitForDocumentsAppearanceInDB(docId, quoteNumber, eventName, true);
	}

	/**
	 * Wait for document(s) request appearance in database for specific <b>docId</b> with timeout {@link DocGenHelper#DOCUMENT_GENERATION_TIMEOUT}
	 *
	 * @param docId       documents ids to be used for waiting xml document.
	 * @param quoteNumber quote/policy number
	 * @param eventName   event name of the generated document
	 */
	@Deprecated
	public static List<Document> waitForMultipleDocumentsAppearanceInDB(DocGenEnum.Documents docId, String quoteNumber, AaaDocGenEntityQueries.EventNames eventName) {
		return waitForMultipleDocumentsAppearanceInDB(docId, quoteNumber, eventName, true);
	}

	/**
	 * Wait for document(s) request appearance in database for specific <b>docId</b> with timeout {@link DocGenHelper#DOCUMENT_GENERATION_TIMEOUT}
	 *
	 * @param docId       documents ids to be used for waiting xml document.
	 * @param quoteNumber quote/policy number
	 * @param eventName   event name of the generated document
	 * @param assertExists   assert if the generated document exists
	 */
	@Deprecated
	public static Document waitForDocumentsAppearanceInDB(DocGenEnum.Documents docId, String quoteNumber, AaaDocGenEntityQueries.EventNames eventName, boolean assertExists) {
		long conditionCheckPoolingIntervalInSeconds = 1;
		log.info(String.format("Waiting for xml document \"%1$s\" request appearance in database.", docId.getId()));

		long searchStart = System.currentTimeMillis();
		long timeout = searchStart + DOCUMENT_GENERATION_TIMEOUT * 1000;
		Document document;
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, quoteNumber, docId.getId(), eventName);
		do {
			try {
				document = getDocument(docId, query);
			} catch (Exception e) {
				document = null;
			}
			if (document != null) {
				break;
			}
			try {
				TimeUnit.SECONDS.sleep(conditionCheckPoolingIntervalInSeconds);
			} catch (InterruptedException e) {
				log.debug(e.getMessage());
			}
		}
		while (timeout > System.currentTimeMillis());
		long searchTime = System.currentTimeMillis() - searchStart;

		if (assertExists) {
			assertThat(document).as(MessageFormat.format("Xml document \"{0}\" not found. Search time:  \"{1}\"", docId.getId(), searchTime)).isNotNull();
		} else {
			assertThat(document).as(MessageFormat.format("Xml document \"{0}\" found. Document should not exist", docId.getId())).isNull();
		}
		log.info(MessageFormat.format((document == null ? "Document not found " : "Found document ") + "\"{0}\" after {1} milliseconds", docId.getId(), searchTime));
		return document;
	}

	/**
	 * Wait for document(s) request appearance in database for specific <b>docId</b> with timeout {@link DocGenHelper#DOCUMENT_GENERATION_TIMEOUT}
	 *
	 * @param docId       documents ids to be used for waiting xml document.
	 * @param quoteNumber quote/policy number
	 * @param eventName   event name of the generated document(s)
	 * @param assertExists   assert if the generated documents exist
	 */
	@Deprecated
	public static List<Document> waitForMultipleDocumentsAppearanceInDB(DocGenEnum.Documents docId, String quoteNumber, AaaDocGenEntityQueries.EventNames eventName, boolean assertExists) {
		long conditionCheckPoolingIntervalInSeconds = 1;
		log.info(String.format("Waiting for xml document \"%1$s\" request appearance in database.", docId.getId()));

		long searchStart = System.currentTimeMillis();
		long timeout = searchStart + DOCUMENT_GENERATION_TIMEOUT * 1000;
		List<Document> documents;
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, quoteNumber, docId.getId(), eventName);
		do {
			try {
				documents = getDocuments(docId, query);
			} catch (Exception e) {
				documents = null;
			}
			if (CollectionUtils.isNotEmpty(documents)) {
				return documents;
			}
			try {
				TimeUnit.SECONDS.sleep(conditionCheckPoolingIntervalInSeconds);
			} catch (InterruptedException e) {
				log.debug(e.getMessage());
			}
		}
		while (timeout > System.currentTimeMillis());
		long searchTime = System.currentTimeMillis() - searchStart;

		if (assertExists) {
			assertThat(documents).as(MessageFormat.format("Xml document(s) \"{0}\" found. Search time:  \"{1}\"", docId.getId(), searchTime)).isNotEmpty();
		}
		log.info(MessageFormat.format((CollectionUtils.isEmpty(documents) ? "Document(s) not found " : "Found document(s) ") + "\"{0}\" after {1} milliseconds", docId.getId(), searchTime));
		return documents;
	}

	/**
	 * Check that documents DOES NOT exist in XML in aaaDocGenEntity event (XML is sent to DCS to create actual .pdf documents)
	 *
	 * @param policyNumber - policy number
	 * @param eventName - event name on which documents are suppose to be generated
	 * @param docs - documents
	 */
	@Deprecated
	public static void checkDocumentsDoesNotExistInXml(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, DocGenEnum.Documents... docs) {
		List<Document> policyDocuments = getDocumentsList(policyNumber, eventName);
		Object[] documentTemplate = policyDocuments.stream().map(Document::getTemplateId).toArray();
		for (DocGenEnum.Documents doc : docs) {
			assertThat(documentTemplate).doesNotContain(doc.getIdInXml());
		}
	}
	@Deprecated
	public static Document getDocument(DocGenEnum.Documents value, String query) {
		String xmlDocData = DbXmlHelper.getXmlByDocName(value, query);
		return XmlHelper.xmlToModelByPartOfXml(xmlDocData, Document.class);
	}

	@Deprecated
	public static List<Document> getDocuments(DocGenEnum.Documents value, String query) {
		String xmlDocData = DbXmlHelper.getXmlByDocName(value, query);
		List<Document> docs = new ArrayList<>();

		List<String> separateXmlDocs = splitToDocuments(xmlDocData);
		for (String separateXmlDoc : separateXmlDocs) {
			docs.add(XmlHelper.xmlToModelByPartOfXml(separateXmlDoc, Document.class));
		}
		return docs;
	}

	@Deprecated
	public static DocumentPackage getDocumentPackage(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		String xmlDocData = DbXmlHelper.getXmlByPolicyNumber(policyNumber, eventName);

		//TODO: Change this to 'always cast to DocumentPackage' once all VDMS get aaaDocGenSerializer.callDCSInstant set to TRUE
		//In the meantime, this hook will work fine
		return getDocumentPackage(xmlDocData);
	}

	@Deprecated
	public static DocumentPackage getDocumentPackage(String xmlDocData) {
		DocumentPackage documentPackage;
		boolean callDCSInstantly = !xmlDocData.startsWith("<doc:CreateDocuments");
		if (callDCSInstantly) {
			documentPackage = XmlHelper.xmlToModel(xmlDocData, DocumentPackage.class);
		} else {
			CreateDocuments doc = XmlHelper.xmlToModel(xmlDocData, CreateDocuments.class);
			//get the only document package
			documentPackage = doc.getStandardDocumentRequest().getDocumentPackages().get(0);
		}
		return documentPackage;
	}

	@Deprecated
	public static List<DocumentPackage> getAllDocumentPackages(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		List<Map<String, String>> allDocs = DbXmlHelper.getXmlsByPolicyNumber(policyNumber, eventName);
		List<DocumentPackage> listOfDocumentPackages = new ArrayList<>();

		for (Map<String, String> doc : allDocs) {
			String xmlDoc = doc.get("DATA");
			DocumentPackage documentPackage = getDocumentPackage(xmlDoc);
			listOfDocumentPackages.add(documentPackage);
		}

		return listOfDocumentPackages;
	}

	/**
	 * Get All Documents from Document Package List
	 * @param allDocumentPackages getAllDocumentPackages()
	 * @return List<Document>
	 */
	@Deprecated
	public static List<Document> getDocumentsFromDocumentPackagesList(List<DocumentPackage> allDocumentPackages) {
		List<Document> actualDocumentsListAfterFirstRenewal = new ArrayList<>();
		for (DocumentPackage documentPackage : allDocumentPackages) {
			actualDocumentsListAfterFirstRenewal.addAll(documentPackage.getDocuments());
		}
		return actualDocumentsListAfterFirstRenewal;
	}

	public static Boolean isPasDocEnabled(String state, PolicyType pType) {
		HashMap<String, ProductCode> pMap = new HashMap<>();
		pMap.put(PolicyType.HOME_SS_HO3.getName(), ProductCode.AAA_HO_SS);
		pMap.put(PolicyType.AUTO_SS.getName(), ProductCode.AAA_SS);
		pMap.put(PolicyType.AUTO_CA_SELECT.getName(), ProductCode.AAA_CSA);
		pMap.put(PolicyType.HOME_CA_HO3.getName(), ProductCode.AAA_HO_CA);
		pMap.put(PolicyType.PUP.getName(), ProductCode.AAA_PUP_SS);
		assertThat(pMap.get(pType.getName()).name()).as("Policy Type " + pType.getName() + " is not in a range").isNotEmpty();
		return executePasDocQuery(state, pMap.get(pType.getName()).name());
	}

	public static Boolean isPasDocEnabled(String policyNum) {
		assertThat(policyNum).as("Policy number is not defined").isNotEmpty();
		String template = parsePolicyNum(policyNum);
		String state;
		ProductCode product = null;
		if (template.startsWith("CA")) {
			state = Constants.States.CA;
			if (template.startsWith("CAH") || template.startsWith("CAD")) {
				product = ProductCode.AAA_HO_CA;
			}
			if (template.startsWith("CAA")) {
				product = ProductCode.AAA_CSA;
			}
			if (template.startsWith("CAPU")) {
				product = ProductCode.AAA_PUP_SS;
			}
		} else {
			state = template.substring(0, 2);
			String prodTemp = template.substring(2);
			if (prodTemp.startsWith("H") || prodTemp.startsWith("D")) {
				product = ProductCode.AAA_HO_SS;
			}
			if (prodTemp.startsWith("SS")) {
				product = ProductCode.AAA_SS;
			}
			if (prodTemp.startsWith("PU")) {
				product = ProductCode.AAA_PUP_SS;
			}
		}
		return executePasDocQuery(state, product.name());
	}

	public static void checkPasDocEnabled(String state, PolicyType pType, Boolean expectedValue) {
		Boolean isEnabled = isPasDocEnabled(state, pType);
		if (!isEnabled.equals(expectedValue)) {
			throw new SkipException(String.format("PasDoc for product and state combination doesn't match. PasDoc switching-on expected: '%s', actual: '%s' - for product: '%s' and state: '%s'. Test will be skipped", expectedValue, isEnabled, pType, state));
		}
	}

	public static void checkPasDocEnabled(String policyNum, Boolean expectedValue) {
		Boolean isEnabled = isPasDocEnabled(policyNum);
		if (!isEnabled.equals(expectedValue)) {
			throw new SkipException(String.format("PasDoc for product and state combination doesn't match. PasDoc switching-on expected: '%s', actual: '%s' - for policy: '%s'. Test will be skipped", expectedValue, isEnabled, policyNum));
		}
	}

	private static List<String> splitToDocuments(String xmlDocData) {
		List<String> docs = new ArrayList<>();
		Matcher matcher = Pattern.compile("<doc:Document(.*?)</doc:Document>").matcher(xmlDocData);
		while (matcher.find()) {
			docs.add(matcher.group());
		}
		return docs;
	}

	private static boolean isRequestValid(DocumentWrapper dw, String policyNumber, DocGenEnum.Documents[] documents) {
		if (documents.length > 0) {
			List<String> expectedDocumentIds = Arrays.stream(documents).map(DocGenEnum.Documents::getIdInXml).collect(Collectors.toList());
			return dw.getAllDocuments(policyNumber).stream().map(Document::getTemplateId).collect(Collectors.toList()).containsAll(expectedDocumentIds);
		} else {
			return dw.getList(SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber)).size() > 0;
		}
	}

	private static Boolean executePasDocQuery(String state, String pType) {
		assertThat(state).as("State is not defined").isNotEmpty();
		assertThat(pType).as("Policy Type is not defined").isNotNull();
		final String queryTemplate = "select lv.displayvalue from lookuplist ll inner join lookupvalue lv on lv.lookuplist_id=ll.id where ll.lookupname='AAARolloutEligibilityLookup' and lv.code='PASDoc' and lv.productcd='%s' and lv.riskstatecd='%s'";
		String value = DBService.get().getValue(String.format(queryTemplate, pType, state)).orElse("false");
		return value.equalsIgnoreCase("true");
	}

	private static String parsePolicyNum(String policyNum) {
		Pattern r = Pattern.compile("Q?([A-Z]*).*");
		Matcher m = r.matcher(policyNum);

		if (!m.find()) {
			throw new SkipException("Policy number can't be parsed: " + policyNum);
		}
		return m.group(1);
	}

	private enum ProductCode {
		AAA_HO_SS, AAA_SS, AAA_CSA, AAA_HO_CA, AAA_PUP_SS
	}

}
