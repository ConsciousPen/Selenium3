package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StandardDocumentRequestNode extends SearchBy<StandardDocumentRequestNode, StandardDocumentRequest> {
	public DocumentPackageNode documentPackage = new DocumentPackageNode();
	public BatchFileSummaryNode batchFileSummary = new BatchFileSummaryNode();

	public StandardDocumentRequestNode correlationId(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getCorrelationId(), value));
	}

	public StandardDocumentRequestNode documentReturnMode(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getDocumentReturnMode(), value));
	}

	public StandardDocumentRequestNode productName(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getProductName(), value));
	}

	public StandardDocumentRequestNode productType(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getProductType(), value));
	}

	public StandardDocumentRequestNode requestingAppName(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getRequestingAppName(), value));
	}

	public StandardDocumentRequestNode requestingUserId(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getRequestingUserId(), value));
	}

	public StandardDocumentRequestNode state(String value) {
		return addCondition(sdr -> Objects.equals(sdr.getState(), value));
	}

	@Override
	public List<StandardDocumentRequest> search(StandardDocumentRequest standardDocumentRequest) {
		return Stream.of(standardDocumentRequest).filter(getConditionAndClear()).collect(Collectors.toList());
	}
}
