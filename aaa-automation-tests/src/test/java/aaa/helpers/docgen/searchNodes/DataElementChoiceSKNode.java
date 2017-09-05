package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aaa.helpers.xml.models.DataElementChoice;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DataElementChoiceSKNode extends SearchBy<DataElementChoiceSKNode, DataElementChoice> {
	public DataElementChoiceSKNode textField(String value) {
		return addCondition(dec -> Objects.equals(dec.getTextField(), value));
	}

	@Override
	public List<DataElementChoice> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.documentPackage.sortKey.search(sDocumentRequest).stream().map(DocumentDataElement::getDataElementChoice).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
