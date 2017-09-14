package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import aaa.helpers.xml.models.*;

public final class DocumentPackageNode extends SearchBy<DocumentPackageNode, DocumentPackage> {
	public ArchiveDataNode archiveData = new ArchiveDataNode();
	public DistributionChannelNode distributionChannel = new DistributionChannelNode();
	public DocumentNode document = new DocumentNode();
	public SortKeyNode sortKey = new SortKeyNode();
	public DocumentPackageDataNode documentPackageData = new DocumentPackageDataNode();

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
	public List<DocumentPackage> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DocumentPackage> copiedCondition = getConditionAndClear();
		List<DocumentPackage> filteredDps = new ArrayList<>();
		for (StandardDocumentRequest sdr : standardDocumentRequest.search(sDocumentRequest)) {
			filteredDps.addAll(sdr.getDocumentPackages().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDps;
	}
}
