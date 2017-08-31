package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentPackage {
	@XmlElement(name = "ArchiveData")
	private ArchiveData archiveData;

	@XmlElement(name = "CorrelationId")
	private String correlationId;

	@XmlElement(name = "DistributionChannels")
	private DistributionChannels distributionChannels;

	@XmlElement(name = "DocumentPackageData")
	private DocumentPackageData documentPackageData;

	@XmlElement(name = "Documents")
	private Documents documents;

	@XmlElement(name = "IsPreview")
	private String isPreview;

	@XmlElement(name = "PackageIdentifier")
	private String packageIdentifier;

	@XmlElement(name = "ProductName")
	private String productName;

	@XmlElement(name = "RequestingUserId")
	private String requestingUserId;

	@XmlElement(name = "SortKeys")
	private SortKeys sortKeys;

	@XmlElement(name = "State")
	private String State;

	public ArchiveData getArchiveData() {
		return archiveData;
	}

	public void setArchiveData(ArchiveData archiveData) {
		this.archiveData = archiveData;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public DistributionChannels getDistributionChannels() {
		return distributionChannels;
	}

	public void setDistributionChannels(DistributionChannels distributionChannels) {
		this.distributionChannels = distributionChannels;
	}

	public DocumentPackageData getDocumentPackageData() {
		return documentPackageData;
	}

	public void setDocumentPackageData(DocumentPackageData documentPackageData) {
		this.documentPackageData = documentPackageData;
	}

	public Documents getDocuments() {
		return documents;
	}

	public void setDocuments(Documents documents) {
		this.documents = documents;
	}

	public String getIsPreview() {
		return isPreview;
	}

	public void setIsPreview(String isPreview) {
		this.isPreview = isPreview;
	}

	public String getPackageIdentifier() {
		return packageIdentifier;
	}

	public void setPackageIdentifier(String packageIdentifier) {
		this.packageIdentifier = packageIdentifier;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getRequestingUserId() {
		return requestingUserId;
	}

	public void setRequestingUserId(String requestingUserId) {
		this.requestingUserId = requestingUserId;
	}

	public SortKeys getSortKeys() {
		return sortKeys;
	}

	public void setSortKeys(SortKeys sortKeys) {
		this.sortKeys = sortKeys;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	@Override
	public String toString() {
		return "DocumentPackage{" +
				"archiveData=" + archiveData +
				", correlationId='" + correlationId + '\'' +
				", distributionChannels=" + distributionChannels +
				", documentPackageData=" + documentPackageData +
				", documents=" + documents +
				", isPreview='" + isPreview + '\'' +
				", packageIdentifier='" + packageIdentifier + '\'' +
				", productName='" + productName + '\'' +
				", requestingUserId='" + requestingUserId + '\'' +
				", sortKeys=" + sortKeys +
				", State='" + State + '\'' +
				'}';
	}
}

