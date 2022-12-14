package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.StandardDocumentRequest;

public class SortKeyNode extends SearchBy<SortKeyNode, DocumentDataElement>{
	public DataElementChoiceSKNode dataElementChoice = new DataElementChoiceSKNode();

	public SortKeyNode name(String value) {
		return addCondition("Name", DocumentDataElement::getName, value);
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDdes.addAll(filter(l.getSortKeys())));
		clearConditions();
		return filteredDdes;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\SortKeys\\DocumentDataElement";
	}
}
