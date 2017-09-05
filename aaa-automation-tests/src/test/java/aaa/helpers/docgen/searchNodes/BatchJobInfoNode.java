package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.BatchFileSummary;
import aaa.helpers.xml.models.BatchJobInfo;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchJobInfoNode extends SearchBy<BatchJobInfoNode, BatchJobInfo> {
	public BatchJobInfoNode batchJobEnvironmentType(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobEnvironmentType(), value));
	}

	public BatchJobInfoNode batchJobApplicationName(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobApplicationName(), value));
	}

	public BatchJobInfoNode batchJobName(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobName(), value));
	}

	public BatchJobInfoNode batchJobServerName(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobServerName(), value));
	}

	public BatchJobInfoNode batchJobIPAddress(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobIPAddress(), value));
	}

	public BatchJobInfoNode batchJobCorrelationId(String value) {
		return addCondition(d -> Objects.equals(d.getBatchJobCorrelationId(), value));
	}

	@Override
	public List<BatchJobInfo> search(StandardDocumentRequest sDocumentRequest) {
		return standardDocumentRequest.batchFileSummary.search(sDocumentRequest).stream().map(BatchFileSummary::getBatchJobInfo).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
