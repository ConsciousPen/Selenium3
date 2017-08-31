package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1")
public class DocumentDataElements {
	@XmlElement(name = "DocumentDataElement")
	private List<DocumentDataElement> documentDataElements = new ArrayList<>();

	public List<DocumentDataElement> getDocumentDataElements() {
		return documentDataElements;
	}

	public void setDocumentDataElement(List<DocumentDataElement> documentDataElements) {
		this.documentDataElements = documentDataElements;
	}

	@Override
	public String toString() {
		return "DocumentDataElements{" +
				"documentDataElements=" + documentDataElements +
				'}';
	}

}
