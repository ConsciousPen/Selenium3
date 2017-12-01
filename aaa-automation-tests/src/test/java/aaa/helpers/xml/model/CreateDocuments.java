package aaa.helpers.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CreateDocuments {
	private StandardDocumentRequest standardDocumentRequest;

	public StandardDocumentRequest getStandardDocumentRequest() {
		return standardDocumentRequest;
	}

	public CreateDocuments setStandardDocumentRequest(StandardDocumentRequest standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
		return this;
	}

	@Override
	public String toString() {
		return "CreateDocuments{" +
				"standardDocumentRequest='" + standardDocumentRequest + '\'' +
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

		CreateDocuments that = (CreateDocuments) o;

		return standardDocumentRequest != null ? standardDocumentRequest.equals(that.standardDocumentRequest) : that.standardDocumentRequest == null;
	}

	@Override
	public int hashCode() {
		return standardDocumentRequest != null ? standardDocumentRequest.hashCode() : 0;
	}
}

