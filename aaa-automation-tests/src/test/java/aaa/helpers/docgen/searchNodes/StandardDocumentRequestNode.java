package aaa.helpers.docgen.searchNodes;

import java.util.List;
import aaa.helpers.xml.model.StandardDocumentRequest;

public class StandardDocumentRequestNode extends SearchBy<StandardDocumentRequestNode, StandardDocumentRequest> {
	public DocumentPackageNode documentPackage = new DocumentPackageNode();
	public BatchFileSummaryNode batchFileSummary = new BatchFileSummaryNode();

	public StandardDocumentRequestNode correlationId(String value) {
		return addCondition("CorrelationId", StandardDocumentRequest::getCorrelationId, value);
	}

	public StandardDocumentRequestNode documentReturnMode(String value) {
		return addCondition("DocumentReturnMode", StandardDocumentRequest::getDocumentReturnMode, value);
	}

	public StandardDocumentRequestNode productName(String value) {
		return addCondition("ProductName", StandardDocumentRequest::getProductName, value);
	}

	public StandardDocumentRequestNode productType(String value) {
		return addCondition("ProductType", StandardDocumentRequest::getProductType, value);
	}

	public StandardDocumentRequestNode requestingAppName(String value) {
		return addCondition("RequestingAppName", StandardDocumentRequest::getRequestingAppName, value);
	}

	public StandardDocumentRequestNode requestingUserId(String value) {
		return addCondition("RequestingUserId", StandardDocumentRequest::getRequestingUserId, value);
	}

	public StandardDocumentRequestNode state(String value) {
		return addCondition("State", StandardDocumentRequest::getState, value);
	}

	@Override
	public List<StandardDocumentRequest> search(StandardDocumentRequest standardDocumentRequest) {
		List<StandardDocumentRequest> filteredList = filter(standardDocumentRequest);
		clearConditions();
		return filteredList;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest";
	}
}
