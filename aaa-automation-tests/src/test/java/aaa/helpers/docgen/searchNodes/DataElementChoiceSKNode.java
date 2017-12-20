package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public final class DataElementChoiceSKNode extends SearchBy<DataElementChoiceSKNode, DataElementChoice> {
	public DataElementChoiceSKNode textField(String value) {
		return addCondition("TextField", DataElementChoice::getTextField, value);
	}

	@Override
	public List<DataElementChoice> search(StandardDocumentRequest sDocumentRequest) {
		List<DataElementChoice> filteredDec = new ArrayList<>();
		standardDocumentRequest.documentPackage.sortKey.search(sDocumentRequest).forEach(l -> filteredDec.addAll(filter(l.getDataElementChoice())));
		clearConditions();
		return filteredDec;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\SortKeys\\DocumentDataElement\\DataElementChoice";
	}
}
