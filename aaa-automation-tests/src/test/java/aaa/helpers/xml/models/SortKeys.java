package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1")
public class SortKeys {
	@XmlElement(name = "DocumentDataElement")
	List<DocumentDataElement> DocumentDataElements = new ArrayList<>();

	public List<DocumentDataElement> getDocumentDataElements() {
		return DocumentDataElements;
	}

	public void setDocumentDataElements(List<DocumentDataElement> documentDataElements) {
		DocumentDataElements = documentDataElements;
	}

	@Override
	public String toString() {
		return "SortKeys{" +
				"DocumentDataElements=" + DocumentDataElements +
				'}';
	}
}
