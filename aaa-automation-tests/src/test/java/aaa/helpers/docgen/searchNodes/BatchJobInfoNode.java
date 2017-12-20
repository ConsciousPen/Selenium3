package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchJobInfo;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public class BatchJobInfoNode extends SearchBy<BatchJobInfoNode, BatchJobInfo> {
	public BatchJobInfoNode batchJobEnvironmentType(String value) {
		return addCondition("batchJobEnvironmentType", BatchJobInfo::getBatchJobEnvironmentType, value);
	}

	public BatchJobInfoNode batchJobApplicationName(String value) {
		return addCondition("batchJobApplicationName", BatchJobInfo::getBatchJobApplicationName, value);
	}

	public BatchJobInfoNode batchJobName(String value) {
		return addCondition("batchJobName", BatchJobInfo::getBatchJobName, value);
	}

	public BatchJobInfoNode batchJobServerName(String value) {
		return addCondition("batchJobServerName", BatchJobInfo::getBatchJobServerName, value);
	}

	public BatchJobInfoNode batchJobIPAddress(String value) {
		return addCondition("batchJobIPAddress", BatchJobInfo::getBatchJobIPAddress, value);
	}

	public BatchJobInfoNode batchJobCorrelationId(String value) {
		return addCondition("batchJobCorrelationId", BatchJobInfo::getBatchJobCorrelationId, value);
	}

	@Override
	public List<BatchJobInfo> search(StandardDocumentRequest sDocumentRequest) {
		List<BatchJobInfo> filteredBji = new ArrayList<>();
		standardDocumentRequest.batchFileSummary.search(sDocumentRequest).forEach(l -> filteredBji.addAll(filter(l.getBatchJobInfo())));
		clearConditions();
		return filteredBji;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\BatchFileSummary";
	}
}
