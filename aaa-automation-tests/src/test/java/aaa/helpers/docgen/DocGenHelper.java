package aaa.helpers.docgen;

import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.xml.XmlHelper;
import aaa.helpers.xml.models.CreateDocuments;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;
import aaa.main.enums.DocGenEnum;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.verification.CustomAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DocGenHelper {
	public static final String DOCGEN_SOURCE_FOLDER = "/home/DocGen/";
	public static final String DOCGEN_BATCH_SOURCE_FOLDER = DOCGEN_SOURCE_FOLDER + "Batch/";
	//public static final String DOCGEN_DESTINATION_FOLDER = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "xmls" + File.separator;
	public static final String JOB_GENERATION_DOCGEN_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/";
	private static final int DOCUMENT_GENERATION_TIMEOUT = 30;

	private static Logger log = LoggerFactory.getLogger(DocGenHelper.class);

	public static void verifyDocumentsGenerated(String policyNumber, DocGenEnum.Documents... documents) {
		verifyDocumentsGenerated(true, policyNumber, documents);
	}

	public static void verifyDocumentsGenerated(boolean expected, String policyNumber, DocGenEnum.Documents... documents) {
		StandardDocumentRequest standardDocumentRequest = getDocumentRequest(policyNumber);
		Map<DocGenEnum.Documents, Boolean> documentsExistence = new HashMap<>(documents.length);
		Arrays.stream(documents).forEach(d -> documentsExistence.put(d, false));

		for (DocGenEnum.Documents document : documents) {
			List<DocumentPackage> documentPackages = standardDocumentRequest.getDocumentPackages().getDocumentPackages();

			for (DocumentPackage packages : documentPackages) {
				List<Document> documentsList = packages.getDocuments().getDocuments();

				for (Document doc : documentsList) {
					if (doc.getTemplateId().equals(document.getIdInXml())) {
						documentsExistence.replace(document, true);
						break;
					}
				}
			}
		}

		if (expected) {
			CustomAssert.assertFalse("Documents where not found: " + documentsExistence.entrySet().stream().filter(m -> !m.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()),
					documentsExistence.containsValue(false));
		} else {
			CustomAssert.assertFalse("Documents where found: " + documentsExistence.entrySet().stream().filter(m -> m.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()),
					documentsExistence.containsValue(true));
		}
	}

	public static StandardDocumentRequest getDocumentRequest(String policyNumber) {
		List<String> documentsFileNames = waitForDocumentsAppearance(policyNumber);
		if (documentsFileNames.size() > 1) {
			log.warn(String.format("%1$s xml document files were found with policy number %2$s: %3$s. First one will be used for getting DocumentRequests model", documentsFileNames.size(), policyNumber, documentsFileNames));
		}
		//TODO-dchubkov: get newest file from list, maybe by their timestamps
		String content = RemoteHelper.getFileContent(DOCGEN_SOURCE_FOLDER + documentsFileNames.get(0));
		CreateDocuments createDocuments = XmlHelper.xmlToModel(content, CreateDocuments.class);

		return createDocuments.getStandardDocumentRequest();
	}

	public static List<String> waitForDocumentsAppearance(String policyNumber) {
		List<String> documentsFileNames = new ArrayList<>();
		String searchCommand = String.format("cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s'", DOCGEN_SOURCE_FOLDER, policyNumber);
		FluentWait<String> documentsGenerationWait = new FluentWait<>(searchCommand);

		documentsGenerationWait.withTimeout(DOCUMENT_GENERATION_TIMEOUT, TimeUnit.SECONDS)
				.withMessage(String.format("Files not found for policy %s in folder \"%s\".", policyNumber, DOCGEN_SOURCE_FOLDER))
				.until((e -> !RemoteHelper.executeCommand(e).isEmpty()));

		String[] documents = RemoteHelper.executeCommand(searchCommand).split("\n");
		for (String doc : documents) {
			documentsFileNames.add(doc.substring(doc.indexOf('/') + 1).trim());
		}

		return documentsFileNames;
	}

	//TODO-dchubkov: move & refactor verifyDocumentsGeneratedByJob() methods from old framework
}
