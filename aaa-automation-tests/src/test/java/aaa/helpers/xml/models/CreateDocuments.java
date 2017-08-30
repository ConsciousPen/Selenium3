package aaa.helpers.xml.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "CreateDocuments")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocgenTemplate", propOrder = {
		"standardDocumentRequest",
})
public class CreateDocuments {
	@XmlElement(name = "standardDocumentRequest")
	private String standardDocumentRequest;

	public String getStandardDocumentRequest() {
		return standardDocumentRequest;
	}

	public void setStandardDocumentRequest(String standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
	}

	@Override
	public String toString() {
		return "CreateDocuments{" +
				"standardDocumentRequest='" + standardDocumentRequest + '\'' +
				'}';
	}
}

