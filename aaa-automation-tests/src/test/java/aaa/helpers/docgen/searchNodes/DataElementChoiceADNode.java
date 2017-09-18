package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.DataElementChoice;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public final class DataElementChoiceADNode extends SearchBy<DataElementChoiceADNode, DataElementChoice> {
	public DataElementChoiceADNode textField(String value) {
		return addCondition("TextField", DataElementChoice::getTextField, value);
	}

	public DataElementChoiceADNode dateTimeField(String value) {
		return addCondition("DateTimeField", DataElementChoice::getDateTimeField, value);
	}

	@Override
	public List<DataElementChoice> search(StandardDocumentRequest sDocumentRequest) {
		List<DataElementChoice> filteredDec = new ArrayList<>();
		standardDocumentRequest.documentPackage.archiveData.documentDataElement.search(sDocumentRequest).forEach(l -> filteredDec.addAll(filter(l.getDataElementChoice())));
		conditionsMap.clear();
		return filteredDec;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\ArchiveData\\DocumentDataElement\\DataElementChoice";
	}
}
