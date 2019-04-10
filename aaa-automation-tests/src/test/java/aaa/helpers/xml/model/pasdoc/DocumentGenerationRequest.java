package aaa.helpers.xml.model.pasdoc;

import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentGenerationRequest {

	@XmlElement(name = "RequestContext")
	private RequestContext requestContext;

	@XmlElementWrapper(name = "DistributionChannels")
	@XmlElement(name = "DistributionChannel", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	private List<String> distributionChannels = new LinkedList<>();

	@XmlElement(name = "DocumentData")
	private DocumentData documentData;

	@XmlElementWrapper(name = "Documents")
	@XmlElement(name = "Document")
	private List<Document> documents = new LinkedList<>();

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public DocumentGenerationRequest setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
		return this;
	}

	public List<String> getDistributionChannel() {
		return distributionChannels;
	}

	public DocumentData getDocumentData() {
		return documentData;
	}

	public DocumentGenerationRequest setDocumentData(DocumentData documentData) {
		this.documentData = documentData;
		return this;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public DocumentGenerationRequest setDocuments(List<Document> documents) {
		this.documents = documents;
		return this;
	}

	public DocumentGenerationRequest setDistributionChannels(List<String> distributionChannels) {
		this.distributionChannels = distributionChannels;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DocumentGenerationRequest)) {
			return false;
		}
		DocumentGenerationRequest that = (DocumentGenerationRequest) o;
		return Objects.equals(requestContext, that.requestContext) &&
				Objects.equals(distributionChannels, that.distributionChannels) &&
				Objects.equals(documentData, that.documentData) &&
				Objects.equals(documents, that.documents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestContext, distributionChannels, documentData, documents);
	}

	@Override
	public String toString() {
		return "DocumentGenerationRequest{" +
				"requestContext=" + requestContext +
				", distributionChannels=" + distributionChannels +
				", documentData=" + documentData +
				", documents=" + documents +
				'}';
	}
}
