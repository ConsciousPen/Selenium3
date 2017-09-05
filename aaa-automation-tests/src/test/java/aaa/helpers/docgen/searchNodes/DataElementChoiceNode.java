package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aaa.helpers.xml.models.DataElementChoice;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DataElementChoiceNode extends SearchBy<DataElementChoiceNode, DataElementChoice> {
	public DataElementChoiceNode textField(String value) {
		return addCondition(dec -> Objects.equals(dec.getTextField(), value));
	}

	public DataElementChoiceNode dateTimeField(String value) {
		return addCondition(dec -> Objects.equals(dec.getDateTimeField(), value));
	}

	@Override
	public List<DataElementChoice> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.documentPackage.document.documentDataSection.documentDataElement.search(sDocumentRequest).stream()
				.map(DocumentDataElement::getDataElementChoice).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
