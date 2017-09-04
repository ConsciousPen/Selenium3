package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;

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

	@Override
	public List<Document> search(List<DocumentPackage> documentsList) {
		List<Document> filteredDocs = new ArrayList<>();
		for (DocumentPackage dp : documentPackage.search(documentsList)) {
			filteredDocs.addAll(dp.getDocuments().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}
		return filteredDocs;
	}
}
