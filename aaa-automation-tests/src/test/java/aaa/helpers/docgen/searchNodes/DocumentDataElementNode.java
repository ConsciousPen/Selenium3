package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DocumentDataElementNode extends SearchBy<DocumentDataElementNode, DocumentDataElement> {
	public DataElementChoiceNode dataElementChoice = new DataElementChoiceNode();

	public DocumentDataElementNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DocumentDataElement> copiedCondition = getConditionAndClear();
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (DocumentDataSection dde : standardDocumentRequest.documentPackage.document.documentDataSection.search(sDocumentRequest)) {
			filteredDdes.addAll(dde.getDocumentDataElements().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}
