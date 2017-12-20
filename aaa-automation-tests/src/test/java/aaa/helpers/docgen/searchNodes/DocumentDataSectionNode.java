package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.helpers.xml.model.StandardDocumentRequest;

public final class DocumentDataSectionNode extends SearchBy<DocumentDataSectionNode, DocumentDataSection> {
	public DocumentDataElementNode documentDataElement = new DocumentDataElementNode();

	public DocumentDataSectionNode sectionName(String value) {
		return addCondition("SectionName", DocumentDataSection::getSectionName, value);
	}

	@Override
	public List<DocumentDataSection> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentDataSection> filteredDds = new ArrayList<>();
		standardDocumentRequest.documentPackage.document.search(sDocumentRequest).forEach(l -> filteredDds.addAll(filter(l.getDocumentDataSections())));
		clearConditions();
		return filteredDds;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\Document\\DocumentDataSection";
	}
}
