package aaa.helpers.xml.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "CreateDocuments")
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

