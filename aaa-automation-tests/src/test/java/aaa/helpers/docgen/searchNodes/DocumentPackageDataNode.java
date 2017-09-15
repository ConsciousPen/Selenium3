package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.DocumentPackageData;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public class DocumentPackageDataNode extends SearchBy<DocumentPackageDataNode, DocumentPackageData> {
	public DocumentDataSectionNode documentDataSection = new DocumentDataSectionNode();

	@Override
	public List<DocumentPackageData> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentPackageData> filteredDpd = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDpd.addAll(filter(l.getDocumentPackageData())));
		return filteredDpd;
	}
}
