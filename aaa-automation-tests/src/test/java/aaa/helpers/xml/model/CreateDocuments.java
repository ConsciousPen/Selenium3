package aaa.helpers.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CreateDocuments {
	private StandardDocumentRequest standardDocumentRequest;

	public StandardDocumentRequest getStandardDocumentRequest() {
		return standardDocumentRequest;
	}

	public void setStandardDocumentRequest(StandardDocumentRequest standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
	}

	@Override
	public String toString() {
		return "CreateDocuments{" +
				"standardDocumentRequest='" + standardDocumentRequest + '\'' +
				'}';
	}
}

