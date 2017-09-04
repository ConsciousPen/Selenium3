package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.helpers.xml.models.DocumentPackage;

public final class DocumentDataSectionNode extends SearchBy<DocumentDataSectionNode, DocumentDataSection> {
	public DocumentDataElementNode documentDataElement = new DocumentDataElementNode();

	public DocumentDataSectionNode sectionName(String value) {
		return addCondition(dds -> Objects.equals(dds.getSectionName(), value));
	}

	@Override
	public List<DocumentDataSection> search(List<DocumentPackage> documentsList) {
		List<DocumentDataSection> filteredDds = new ArrayList<>();
		for (Document doc : documentPackage.document.search(documentsList)) {
			filteredDds.addAll(doc.getDocumentDataSections().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}

		return filteredDds;
	}
}
