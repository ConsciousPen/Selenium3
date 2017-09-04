package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DocumentPackage;

public final class DocumentPackageNode extends SearchBy<DocumentPackageNode, DocumentPackage> {
	public ArchiveDataNode archiveData = new ArchiveDataNode();
	public DistributionChannelNode distributionChannel = new DistributionChannelNode();
	public DocumentNode document = new DocumentNode();
	public SortKeyNode sortKey = new SortKeyNode();

	public DocumentPackageNode correlationId(String value) {
		return addCondition(dp -> Objects.equals(dp.getCorrelationId(), value));
	}

	public DocumentPackageNode isPreview(String value) {
		return addCondition(dp -> Objects.equals(dp.getIsPreview(), value));
	}

	public DocumentPackageNode packageIdentifier(String value) {
		return addCondition(dp -> Objects.equals(dp.getPackageIdentifier(), value));
	}

	public DocumentPackageNode productName(String value) {
		return addCondition(dp -> Objects.equals(dp.getProductName(), value));
	}

	public DocumentPackageNode requestingUserId(String value) {
		return addCondition(dp -> Objects.equals(dp.getRequestingUserId(), value));
	}

	public DocumentPackageNode state(String value) {
		return addCondition(dp -> Objects.equals(dp.getState(), value));
	}

	@Override
	public List<DocumentPackage> search(List<DocumentPackage> documentsList) {
		return documentsList.stream().filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
