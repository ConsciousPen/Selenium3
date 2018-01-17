package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.xml.model.DocumentPackage;
import aaa.helpers.xml.model.StandardDocumentRequest;

public final class DocumentPackageNode extends SearchBy<DocumentPackageNode, DocumentPackage> {
	public ArchiveDataNode archiveData = new ArchiveDataNode();
	public DistributionChannelNode distributionChannel = new DistributionChannelNode();
	public DocumentNode document = new DocumentNode();
	public SortKeyNode sortKey = new SortKeyNode();
	public DocumentPackageDataNode documentPackageData = new DocumentPackageDataNode();

	public DocumentPackageNode correlationId(String value) {
		return addCondition("CorrelationId", DocumentPackage::getCorrelationId, value);
	}

	public DocumentPackageNode isPreview(String value) {
		return addCondition("IsPreview", DocumentPackage::getIsPreview, value);
	}

	public DocumentPackageNode packageIdentifier(String value) {
		return addCondition("PackageIdentifier", DocumentPackage::getPackageIdentifier, value);
	}

	public DocumentPackageNode productName(String value) {
		return addCondition("ProductName", DocumentPackage::getProductName, value);
	}

	public DocumentPackageNode requestingUserId(String value) {
		return addCondition("RequestingUserId", DocumentPackage::getRequestingUserId, value);
	}

	public DocumentPackageNode state(String value) {
		return addCondition("State", DocumentPackage::getState, value);
	}

	@Override
	public List<DocumentPackage> search(StandardDocumentRequest sDocumentRequest) {
		List<DocumentPackage> filteredDps = new ArrayList<>();
		standardDocumentRequest.search(sDocumentRequest).forEach(l -> filteredDps.addAll(filter(l.getDocumentPackages())));
		clearConditions();
		return filteredDps;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage";
	}
}
