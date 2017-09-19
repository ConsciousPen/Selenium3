package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchFileSummary;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public class BatchFileSummaryNode extends SearchBy<BatchFileSummaryNode, BatchFileSummary> {
	public BatchJobInfoNode batchJobInfo = new BatchJobInfoNode();
	public BatchFileInfoNode batchFileInfo = new BatchFileInfoNode();

	@Override
	public List<BatchFileSummary> search(StandardDocumentRequest sDocumentRequest) {
		List<BatchFileSummary> filteredBfs = new ArrayList<>();
		standardDocumentRequest.search(sDocumentRequest).forEach(l -> filteredBfs.addAll(filter(l.getBatchFileSummary())));
		conditionsMap.clear();
		return filteredBfs;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\BatchFileSummary";
	}
}
