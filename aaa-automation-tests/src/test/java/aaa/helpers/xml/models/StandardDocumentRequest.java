package aaa.helpers.xml.models;

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

	@XmlElement(name = "DocumentPackages")
	private DocumentPackages documentPackages;

	@XmlElement(name = "standardDocumentRequest")
	private String standardDocumentRequest;

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getDocumentReturnMode() {
		return documentReturnMode;
	}

	public void setDocumentReturnMode(String documentReturnMode) {
		this.documentReturnMode = documentReturnMode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getRequestingAppName() {
		return requestingAppName;
	}

	public void setRequestingAppName(String requestingAppName) {
		this.requestingAppName = requestingAppName;
	}

	public String getRequestingUserId() {
		return requestingUserId;
	}

	public void setRequestingUserId(String requestingUserId) {
		this.requestingUserId = requestingUserId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public DocumentPackages getDocumentPackages() {
		return documentPackages;
	}

	public void setDocumentPackages(DocumentPackages documentPackages) {
		this.documentPackages = documentPackages;
	}

	public String getStandardDocumentRequest() {
		return standardDocumentRequest;
	}

	public void setStandardDocumentRequest(String standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
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
				'}';
	}
}

