package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchFileInfo;
import aaa.helpers.xml.models.BatchFileSummary;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchFileInfoNode extends SearchBy <BatchFileInfoNode, BatchFileInfo> {
	public BatchFileInfoNode batchFileName(String value) {
		return addCondition(d -> Objects.equals(d.getBatchFileName(), value));
	}

	public BatchFileInfoNode batchFileCreationDateTime(String value) {
		return addCondition(d -> Objects.equals(d.getBatchFileCreationDateTime(), value));
	}

	public BatchFileInfoNode batchFileCorrelationId(String value) {
		return addCondition(d -> Objects.equals(d.getBatchFileCorrelationId(), value));
	}

	public BatchFileInfoNode batchFileLineItemCount(String value) {
		return addCondition(d -> Objects.equals(d.getBatchFileLineItemCount(), value));
	}

	@Override
	public List<BatchFileInfo> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.batchFileSummary.search(sDocumentRequest).stream().map(BatchFileSummary::getBatchFileInfo).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
