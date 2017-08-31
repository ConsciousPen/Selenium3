package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentPackages {
	@XmlElement(name = "DocumentPackage")
	private List<DocumentPackage> documentPackages = new ArrayList<>();

	public List<DocumentPackage> getDocumentPackages() {
		return documentPackages;
	}

	public void setDocumentPackages(List<DocumentPackage> documentPackages) {
		this.documentPackages = documentPackages;
	}

	@Override
	public String toString() {
		return "DocumentPackages{" +
				"documentPackages=" + documentPackages +
				'}';
	}
}
