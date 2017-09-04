package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentPackage;

public class SortKeyNode extends SearchBy<SortKeyNode, DocumentDataElement>{
	public DataElementChoiceSKNode dataElementChoice = new DataElementChoiceSKNode();

	public SortKeyNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(List<DocumentPackage> documentsList) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (DocumentPackage dp : documentPackage.search(documentsList)) {
			filteredDdes.addAll(dp.getSortKeys().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}