package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.StandardDocumentRequest;

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
