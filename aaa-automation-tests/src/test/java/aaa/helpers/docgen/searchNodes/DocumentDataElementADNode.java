package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.ArchiveData;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DocumentDataElementADNode extends SearchBy<DocumentDataElementADNode, DocumentDataElement> {
	public DataElementChoiceADNode dataElementChoice = new DataElementChoiceADNode();

	public DocumentDataElementADNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DocumentDataElement> copiedCondition = getConditionAndClear();
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (ArchiveData ad : standardDocumentRequest.documentPackage.archiveData.search(sDocumentRequest)) {
			filteredDdes.addAll(ad.getDocumentDataElements().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}
