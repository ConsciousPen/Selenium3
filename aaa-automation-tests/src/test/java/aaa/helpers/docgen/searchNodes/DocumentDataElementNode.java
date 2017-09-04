package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.helpers.xml.models.DocumentPackage;

public class DocumentDataElementNode extends SearchBy<DocumentDataElementNode, DocumentDataElement> {
	public DataElementChoiceNode dataElementChoice = new DataElementChoiceNode();

	public DocumentDataElementNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(List<DocumentPackage> documentsList) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (DocumentDataSection dde : documentPackage.document.documentDataSection.search(documentsList)) {
			filteredDdes.addAll(dde.getDocumentDataElements().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}
