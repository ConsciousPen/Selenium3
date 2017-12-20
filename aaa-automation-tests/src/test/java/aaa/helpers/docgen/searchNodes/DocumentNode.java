package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.StandardDocumentRequest;

public final class DocumentNode extends SearchBy<DocumentNode, Document> {
	public DocumentDataSectionNode documentDataSection = new DocumentDataSectionNode();

	public DocumentNode documentsData(String value) {
		return addCondition("DocumentsData", Document::getDocumentsData, value);
	}

	public DocumentNode sequence(String value) {
		return addCondition("Sequence", Document::getSequence, value);
	}

	public DocumentNode templateId(String value) {
		return addCondition("TemplateId", Document::getTemplateId, value);
	}

	public DocumentNode xPathInfo(String value) {
		return addCondition("XPathInfo", Document::getxPathInfo, value);
	}

	public DocumentNode eSignatureDocument(String value) {
		return addCondition("eSignatureDocument", Document::geteSignatureDocument, value);
	}

	@Override
	public List<Document> search(StandardDocumentRequest sDocumentRequest) {
		List<Document> filteredDocs = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDocs.addAll(filter(l.getDocuments())));
		clearConditions();
		return filteredDocs;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\Document";
	}
}
