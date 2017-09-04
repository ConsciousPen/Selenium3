package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.ArchiveData;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentPackage;

public class DocumentDataElementADNode extends SearchBy<DocumentDataElementADNode, DocumentDataElement> {
	public DataElementChoiceADNode dataElementChoice = new DataElementChoiceADNode();

	public DocumentDataElementADNode name(String value) {
		return addCondition(dde -> Objects.equals(dde.getName(), value));
	}

	@Override
	public List<DocumentDataElement> search(List<DocumentPackage> documentsList) {
		List<DocumentDataElement> filteredDdes = new ArrayList<>();
		for (ArchiveData ad : documentPackage.archiveData.search(documentsList)) {
			filteredDdes.addAll(ad.getDocumentDataElements().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}
		return filteredDdes;
	}
}
