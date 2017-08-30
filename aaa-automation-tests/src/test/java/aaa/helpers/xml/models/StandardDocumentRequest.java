package aaa.helpers.xml.models;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "doc:standardDocumentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class StandardDocumentRequest {
	@XmlElement(name = "doc:CorrelationId")
	private String correlationId;

	@XmlElement(name = "doc:DocumentReturnMode")
	private String documentReturnMode;

	@XmlElement(name = "doc:ProductName")
	private String ProductName;

	@XmlElement(name = "doc:ProductType")
	private String productType;

	@XmlElement(name = "doc:RequestingAppName")
	private String requestingAppName;

	@XmlElement(name = "doc:RequestingUserId")
	private String requestingUserId;

	@XmlElement(name = "doc:State")
	private String state;

	@XmlElement(name = "doc:DocumentPackages")
	private List<DocumentPackage> documentPackages = new ArrayList<>();

	@XmlElement(name = "doc:standardDocumentRequest")
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
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
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

	public List<DocumentPackage> getDocumentPackages() {
		return documentPackages;
	}

	public void setDocumentPackages(List<DocumentPackage> documentPackages) {
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
				", ProductName='" + ProductName + '\'' +
				", productType='" + productType + '\'' +
				", requestingAppName='" + requestingAppName + '\'' +
				", requestingUserId='" + requestingUserId + '\'' +
				", state='" + state + '\'' +
				", documentPackages=" + documentPackages +
				", standardDocumentRequest='" + standardDocumentRequest + '\'' +
				'}';
	}
}

