package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.StandardDocumentRequest;

public class SortKeyNode extends SearchBy<SortKeyNode, DocumentDataElement>{
	public DataElementChoiceSKNode dataElementChoice = new DataElementChoiceSKNode();

	public SortKeyNode name(String value) {
		return addCondition("Name", DocumentDataElement::getName, value);
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDdes.addAll(filter(l.getSortKeys())));
		return filteredDdes;
	}
}
