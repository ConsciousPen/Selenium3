package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aaa.helpers.xml.models.ArchiveData;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class ArchiveDataNode extends SearchBy<ArchiveDataNode, ArchiveData> {
	public DocumentDataElementADNode documentDataElement = new DocumentDataElementADNode();

	public ArchiveDataNode sectionName(String value) {
		return addCondition(ad -> Objects.equals(ad.getSectionName(), value));
	}

	@Override
	public List<ArchiveData> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.documentPackage.search(sDocumentRequest).stream().map(DocumentPackage::getArchiveData).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
