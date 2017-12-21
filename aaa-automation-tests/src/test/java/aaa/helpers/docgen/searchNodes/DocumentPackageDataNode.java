package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DocumentPackageData;
import aaa.helpers.xml.model.StandardDocumentRequest;

public class DocumentPackageDataNode extends SearchBy<DocumentPackageDataNode, DocumentPackageData> {
	public DocumentDataSectionNode documentDataSection = new DocumentDataSectionNode();

	@Override
	public List<DocumentPackageData> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentPackageData> filteredDpd = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDpd.addAll(filter(l.getDocumentPackageData())));
		clearConditions();
		return filteredDpd;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\DocumentPackageData";
	}
}
