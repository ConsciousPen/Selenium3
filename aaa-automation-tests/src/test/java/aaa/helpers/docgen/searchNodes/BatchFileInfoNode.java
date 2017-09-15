package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchFileInfo;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public class BatchFileInfoNode extends SearchBy<BatchFileInfoNode, BatchFileInfo> {
	public BatchFileInfoNode batchFileName(String value) {
		return addCondition("batchFileName", BatchFileInfo::getBatchFileName, value);
	}

	public BatchFileInfoNode batchFileCreationDateTime(String value) {
		return addCondition("batchFileCreationDateTime", BatchFileInfo::getBatchFileCreationDateTime, value);
	}

	public BatchFileInfoNode batchFileCorrelationId(String value) {
		return addCondition("batchFileCorrelationId", BatchFileInfo::getBatchFileCorrelationId, value);
	}

	public BatchFileInfoNode batchFileLineItemCount(String value) {
		return addCondition("batchFileLineItemCount", BatchFileInfo::getBatchFileLineItemCount, value);
	}

	@Override
	public List<BatchFileInfo> search(StandardDocumentRequest sDocumentRequest) {
		List<BatchFileInfo> filteredBfi = new ArrayList<>();
		standardDocumentRequest.batchFileSummary.search(sDocumentRequest).forEach(l -> filteredBfi.addAll(filter(l.getBatchFileInfo())));
		return filteredBfi;
	}
}
