package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchFileSummary;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.List;
import java.util.stream.Collectors;

public class BatchFileSummaryNode extends SearchBy<BatchFileSummaryNode, BatchFileSummary> {
	public BatchJobInfoNode batchJobInfo = new BatchJobInfoNode();
	public BatchFileInfoNode batchFileInfo = new BatchFileInfoNode();

	@Override
	public List<BatchFileSummary> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.search(sDocumentRequest).stream().map(StandardDocumentRequest::getBatchFileSummary).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
