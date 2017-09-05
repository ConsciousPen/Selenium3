package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;

public class SortKeyNode extends SearchBy<SortKeyNode, DocumentDataElement>{
	public DataElementChoiceSKNode dataElementChoice = new DataElementChoiceSKNode();

	public SortKeyNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DocumentDataElement> copiedCondition = getConditionAndClear();
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (DocumentPackage dp : standardDocumentRequest.documentPackage.search(sDocumentRequest)) {
			filteredDdes.addAll(dp.getSortKeys().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}
