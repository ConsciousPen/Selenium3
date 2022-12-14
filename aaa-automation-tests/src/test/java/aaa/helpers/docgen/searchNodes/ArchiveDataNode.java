package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.model.ArchiveData;
import aaa.helpers.xml.model.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public final class ArchiveDataNode extends SearchBy<ArchiveDataNode, ArchiveData> {
	public DocumentDataElementADNode documentDataElement = new DocumentDataElementADNode();

	public ArchiveDataNode sectionName(String value) {
		return addCondition("SectionName", ArchiveData::getSectionName, value);
	}

	@Override
	public List<ArchiveData> search(StandardDocumentRequest sDocumentRequest) {
		List<ArchiveData> filteredAd = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredAd.addAll(filter(l.getArchiveData())));
		clearConditions();
		return filteredAd;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\ArchiveData";
	}
}
