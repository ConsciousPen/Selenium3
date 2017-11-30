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

	public String getCorrelationId() {
		return correlationId;
	}

	public List<DistributionChannel> getDistributionChannels() {
		return DistributionChannels;
	}

	public DocumentPackageData getDocumentPackageData() {
		return documentPackageData;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public String getIsPreview() {
		return isPreview;
	}

	public String getPackageIdentifier() {
		return packageIdentifier;
	}

	public String getProductName() {
		return productName;
	}
	public String getRequestingUserId() {
		return requestingUserId;
	}

	public List<DocumentDataElement> getSortKeys() {
		return sortKeys;
	}

	public String getState() {
		return State;
	}

	public DocumentPackage setArchiveData(ArchiveData archiveData) {
		this.archiveData = archiveData;
		return this;
	}

	public DocumentPackage setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return this;
	}

	public DocumentPackage setDistributionChannels(List<DistributionChannel> distributionChannels) {
		DistributionChannels = distributionChannels;
		return this;
	}

	public DocumentPackage setDocumentPackageData(DocumentPackageData documentPackageData) {
		this.documentPackageData = documentPackageData;
		return this;
	}

	public DocumentPackage setDocuments(List<Document> documents) {
		this.documents = documents;
		return this;
	}

	public DocumentPackage setIsPreview(String isPreview) {
		this.isPreview = isPreview;
		return this;
	}

	public DocumentPackage setPackageIdentifier(String packageIdentifier) {
		this.packageIdentifier = packageIdentifier;
		return this;
	}

	public DocumentPackage setProductName(String productName) {
		this.productName = productName;
		return this;
	}

	public DocumentPackage setRequestingUserId(String requestingUserId) {
		this.requestingUserId = requestingUserId;
		return this;
	}

	public DocumentPackage setSortKeys(List<DocumentDataElement> sortKeys) {
		this.sortKeys = sortKeys;
		return this;
	}

	public DocumentPackage setState(String state) {
		State = state;
		return this;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DocumentPackage that = (DocumentPackage) o;

		if (archiveData != null ? !archiveData.equals(that.archiveData) : that.archiveData != null) {
			return false;
		}
		if (correlationId != null ? !correlationId.equals(that.correlationId) : that.correlationId != null) {
			return false;
		}
		if (DistributionChannels != null ? !DistributionChannels.equals(that.DistributionChannels) : that.DistributionChannels != null) {
			return false;
		}
		if (documentPackageData != null ? !documentPackageData.equals(that.documentPackageData) : that.documentPackageData != null) {
			return false;
		}
		if (documents != null ? !documents.equals(that.documents) : that.documents != null) {
			return false;
		}
		if (isPreview != null ? !isPreview.equals(that.isPreview) : that.isPreview != null) {
			return false;
		}
		if (packageIdentifier != null ? !packageIdentifier.equals(that.packageIdentifier) : that.packageIdentifier != null) {
			return false;
		}
		if (productName != null ? !productName.equals(that.productName) : that.productName != null) {
			return false;
		}
		if (requestingUserId != null ? !requestingUserId.equals(that.requestingUserId) : that.requestingUserId != null) {
			return false;
		}
		if (sortKeys != null ? !sortKeys.equals(that.sortKeys) : that.sortKeys != null) {
			return false;
		}
		return State != null ? State.equals(that.State) : that.State == null;
	}

	@Override
	public int hashCode() {
		int result = archiveData != null ? archiveData.hashCode() : 0;
		result = 31 * result + (correlationId != null ? correlationId.hashCode() : 0);
		result = 31 * result + (DistributionChannels != null ? DistributionChannels.hashCode() : 0);
		result = 31 * result + (documentPackageData != null ? documentPackageData.hashCode() : 0);
		result = 31 * result + (documents != null ? documents.hashCode() : 0);
		result = 31 * result + (isPreview != null ? isPreview.hashCode() : 0);
		result = 31 * result + (packageIdentifier != null ? packageIdentifier.hashCode() : 0);
		result = 31 * result + (productName != null ? productName.hashCode() : 0);
		result = 31 * result + (requestingUserId != null ? requestingUserId.hashCode() : 0);
		result = 31 * result + (sortKeys != null ? sortKeys.hashCode() : 0);
		result = 31 * result + (State != null ? State.hashCode() : 0);
		return result;
	}
}

