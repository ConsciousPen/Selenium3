package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.DocumentPackageData;
import aaa.helpers.xml.models.StandardDocumentRequest;

public class DocumentPackageDataNode extends SearchBy<DocumentPackageDataNode, DocumentPackageData> {
	public DocumentDataSectionNode documentDataSection = new DocumentDataSectionNode();
	
	@Override
	public List<DocumentPackageData> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.documentPackage.search(sDocumentRequest).stream().map(DocumentPackage::getDocumentPackageData).filter(getConditionAndClear()).collect(Collectors.toList());
	}

}
