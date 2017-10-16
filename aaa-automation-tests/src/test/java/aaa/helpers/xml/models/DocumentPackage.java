package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentPackage {
	@XmlElement(name = "ArchiveData")
	private ArchiveData archiveData;

	@XmlElement(name = "CorrelationId")
	private String correlationId;

	@XmlElementWrapper(name = "DistributionChannels")
	@XmlElement(name = "DistributionChannel", namespace = DocGenEnum.XmlnsNamespaces.DOC_URI2)
	List<DistributionChannel> DistributionChannels = new ArrayList<>();

	@XmlElement(name = "DocumentPackageData")
	private DocumentPackageData documentPackageData;

	@XmlElementWrapper(name = "Documents")
	@XmlElement(name = "Document")
	List<Document> documents = new ArrayList<>();

	@XmlElement(name = "IsPreview")
	private String isPreview;

	@XmlElement(name = "PackageIdentifier")
	private String packageIdentifier;

	@XmlElement(name = "ProductName")
	private String productName;

	@XmlElement(name = "RequestingUserId")
	private String requestingUserId;

	@XmlElementWrapper(name = "SortKeys")
	@XmlElement(name = "DocumentDataElement", namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
	List<DocumentDataElement> sortKeys = new ArrayList<>();

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

	public List<DistributionChannel> getDistributionChannels() {
		return DistributionChannels;
	}

	public void setDistributionChannels(List<DistributionChannel> distributionChannels) {
		DistributionChannels = distributionChannels;
	}

	public DocumentPackageData getDocumentPackageData() {
		return documentPackageData;
	}

	public void setDocumentPackageData(DocumentPackageData documentPackageData) {
		this.documentPackageData = documentPackageData;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
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

	public List<DocumentDataElement> getSortKeys() {
		return sortKeys;
	}

	public void setSortKeys(List<DocumentDataElement> sortKeys) {
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
				", DistributionChannels=" + DistributionChannels +
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

