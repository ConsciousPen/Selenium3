package aaa.helpers.xml.models;

import aaa.main.enums.DocGenEnum;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
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
