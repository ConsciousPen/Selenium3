package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.StandardDocumentRequest;

public final class DocumentDataElementNode extends SearchBy<DocumentDataElementNode, DocumentDataElement> {
	public DataElementChoiceNode dataElementChoice = new DataElementChoiceNode();

	public DocumentDataElementNode name(String value) {
		return addCondition("Name", DocumentDataElement::getName, value);
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		standardDocumentRequest.documentPackage.document.documentDataSection.search(sDocumentRequest).forEach(l -> filteredDdes.addAll(filter(l.getDocumentDataElements())));
		clearConditions();
		return filteredDdes;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\Document\\DocumentDataSection\\DocumentDataElement";
	}
}
