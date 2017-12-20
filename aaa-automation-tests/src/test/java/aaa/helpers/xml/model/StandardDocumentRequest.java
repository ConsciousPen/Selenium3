package aaa.helpers.xml.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class StandardDocumentRequest {
	@XmlElement(name = "CorrelationId")
	private String correlationId;

	@XmlElement(name = "DocumentReturnMode")
	private String documentReturnMode;

	@XmlElement(name = "ProductName")
	private String productName;

	@XmlElement(name = "ProductType")
	private String productType;

	@XmlElement(name = "RequestingAppName")
	private String requestingAppName;

	@XmlElement(name = "RequestingUserId")
	private String requestingUserId;

	@XmlElement(name = "State")
	private String state;

	@XmlElementWrapper(name = "DocumentPackages")
	@XmlElement(name = "DocumentPackage")
	private List<DocumentPackage> documentPackages = new ArrayList<>();

	@XmlElement(name = "standardDocumentRequest")
	private String standardDocumentRequest;

	@XmlElement(name = "BatchFileSummary")
	BatchFileSummary batchFileSummary;

	public String getCorrelationId() {
		return correlationId;
	}

	public String getDocumentReturnMode() {
		return documentReturnMode;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductType() {
		return productType;
	}

	public String getRequestingAppName() {
		return requestingAppName;
	}

	public String getRequestingUserId() {
		return requestingUserId;
	}

	public String getState() {
		return state;
	}

	public List<DocumentPackage> getDocumentPackages() {
		return documentPackages;
	}

	public String getStandardDocumentRequest() {
		return standardDocumentRequest;
	}

	public BatchFileSummary getBatchFileSummary() {
		return batchFileSummary;
	}

	public StandardDocumentRequest setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return this;
	}

	public StandardDocumentRequest setDocumentReturnMode(String documentReturnMode) {
		this.documentReturnMode = documentReturnMode;
		return this;
	}

	public StandardDocumentRequest setProductName(String productName) {
		this.productName = productName;
		return this;
	}

	public StandardDocumentRequest setProductType(String productType) {
		this.productType = productType;
		return this;
	}

	public StandardDocumentRequest setRequestingAppName(String requestingAppName) {
		this.requestingAppName = requestingAppName;
		return this;
	}

	public StandardDocumentRequest setRequestingUserId(String requestingUserId) {
		this.requestingUserId = requestingUserId;
		return this;
	}

	public StandardDocumentRequest setState(String state) {
		this.state = state;
		return this;
	}

	public StandardDocumentRequest setDocumentPackages(List<DocumentPackage> documentPackages) {
		this.documentPackages = documentPackages;
		return this;
	}

	public StandardDocumentRequest setStandardDocumentRequest(String standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
		return this;
	}

	public StandardDocumentRequest setBatchFileSummary(BatchFileSummary batchFileSummary) {
		this.batchFileSummary = batchFileSummary;
		return this;
	}

	@Override
	public String toString() {
		return "StandardDocumentRequest{" +
				"correlationId='" + correlationId + '\'' +
				", documentReturnMode='" + documentReturnMode + '\'' +
				", productName='" + productName + '\'' +
				", productType='" + productType + '\'' +
				", requestingAppName='" + requestingAppName + '\'' +
				", requestingUserId='" + requestingUserId + '\'' +
				", state='" + state + '\'' +
				", documentPackages=" + documentPackages +
				", standardDocumentRequest='" + standardDocumentRequest + '\'' +
				", batchFileSummary=" + batchFileSummary +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		StandardDocumentRequest that = (StandardDocumentRequest) o;

		if (correlationId != null ? !correlationId.equals(that.correlationId) : that.correlationId != null) {
			return false;
		}
		if (documentReturnMode != null ? !documentReturnMode.equals(that.documentReturnMode) : that.documentReturnMode != null) {
			return false;
		}
		if (productName != null ? !productName.equals(that.productName) : that.productName != null) {
			return false;
		}
		if (productType != null ? !productType.equals(that.productType) : that.productType != null) {
			return false;
		}
		if (requestingAppName != null ? !requestingAppName.equals(that.requestingAppName) : that.requestingAppName != null) {
			return false;
		}
		if (requestingUserId != null ? !requestingUserId.equals(that.requestingUserId) : that.requestingUserId != null) {
			return false;
		}
		if (state != null ? !state.equals(that.state) : that.state != null) {
			return false;
		}
		if (documentPackages != null ? !documentPackages.equals(that.documentPackages) : that.documentPackages != null) {
			return false;
		}
		if (standardDocumentRequest != null ? !standardDocumentRequest.equals(that.standardDocumentRequest) : that.standardDocumentRequest != null) {
			return false;
		}
		return batchFileSummary != null ? batchFileSummary.equals(that.batchFileSummary) : that.batchFileSummary == null;
	}

	@Override
	public int hashCode() {
		int result = correlationId != null ? correlationId.hashCode() : 0;
		result = 31 * result + (documentReturnMode != null ? documentReturnMode.hashCode() : 0);
		result = 31 * result + (productName != null ? productName.hashCode() : 0);
		result = 31 * result + (productType != null ? productType.hashCode() : 0);
		result = 31 * result + (requestingAppName != null ? requestingAppName.hashCode() : 0);
		result = 31 * result + (requestingUserId != null ? requestingUserId.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (documentPackages != null ? documentPackages.hashCode() : 0);
		result = 31 * result + (standardDocumentRequest != null ? standardDocumentRequest.hashCode() : 0);
		result = 31 * result + (batchFileSummary != null ? batchFileSummary.hashCode() : 0);
		return result;
	}
}

