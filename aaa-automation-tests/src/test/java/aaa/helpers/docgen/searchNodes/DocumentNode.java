package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DocumentNode extends SearchBy<DocumentNode, Document> {
	public DocumentDataSectionNode documentDataSection = new DocumentDataSectionNode();

	public DocumentNode sequence(String value) {
		return addCondition(d -> Objects.equals(d.getSequence(), value));
	}

	public DocumentNode templateId(String value) {
		return addCondition(d -> Objects.equals(d.getTemplateId(), value));
	}

	public DocumentNode xPathInfo(String value) {
		return addCondition(d -> Objects.equals(d.getxPathInfo(), value));
	}

	public DocumentNode eSignatureDocument(String value) {
		return addCondition(d -> Objects.equals(d.geteSignatureDocument(), value));
	}

	@Override
	public List<Document> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<Document> copiedCondition = getConditionAndClear();
		List<Document> filteredDocs = new ArrayList<>();
		for (DocumentPackage dp : standardDocumentRequest.documentPackage.search(sDocumentRequest)) {
			filteredDocs.addAll(dp.getDocuments().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDocs;
	}
}
