package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DocumentDataSectionNode extends SearchBy<DocumentDataSectionNode, DocumentDataSection> {
	public DocumentDataElementNode documentDataElement = new DocumentDataElementNode();

	public DocumentDataSectionNode sectionName(String value) {
		return addCondition(dds -> Objects.equals(dds.getSectionName(), value));
	}

	@Override
	public List<DocumentDataSection> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DocumentDataSection> copiedCondition = getConditionAndClear();
		List<DocumentDataSection> filteredDds = new ArrayList<>();
		for (Document doc : standardDocumentRequest.documentPackage.document.search(sDocumentRequest)) {
			filteredDds.addAll(doc.getDocumentDataSections().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDds;
	}
}
