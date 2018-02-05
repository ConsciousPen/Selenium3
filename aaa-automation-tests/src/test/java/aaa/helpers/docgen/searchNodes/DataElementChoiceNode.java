package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public final class DataElementChoiceNode extends SearchBy<DataElementChoiceNode, DataElementChoice> {
	public DataElementChoiceNode textField(String value) {
		return addCondition("TextField", DataElementChoice::getTextField, value);
	}

	public DataElementChoiceNode dateTimeField(String value) {
		return addCondition("DateTimeField", DataElementChoice::getDateTimeField, value);
	}

	@Override
	public List<DataElementChoice> search(StandardDocumentRequest sDocumentRequest) {
		List<DataElementChoice> filteredDec = new ArrayList<>();
		standardDocumentRequest.documentPackage.document.documentDataSection.documentDataElement.search(sDocumentRequest).forEach(l -> filteredDec.addAll(filter(l.getDataElementChoice())));
		clearConditions();
		return filteredDec;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\Document\\DocumentDataSection\\DocumentDataElement\\DataElementChoice";
	}
}
