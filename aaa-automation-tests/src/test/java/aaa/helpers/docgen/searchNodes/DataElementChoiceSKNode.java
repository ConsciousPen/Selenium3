package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import aaa.helpers.xml.models.DataElementChoice;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentPackage;

public class DataElementChoiceSKNode extends SearchBy<DataElementChoiceSKNode, DataElementChoice> {
	public DataElementChoiceSKNode textField(String value) {
		return addCondition(dec -> Objects.equals(dec.getTextField(), value));
	}

	@Override
	public List<DataElementChoice> search(List<DocumentPackage> documentsList) {
		Predicate<DataElementChoice> copiedCondition = getConditionAndClear();
		List<DataElementChoice> filteredDec = new ArrayList<>();
		for (DocumentDataElement dde : documentPackage.sortKey.search(documentsList)) {
			if (copiedCondition.test(dde.getDataElementChoice())) {
				filteredDec.add(dde.getDataElementChoice());
			}
		}
		return filteredDec;
	}
}
