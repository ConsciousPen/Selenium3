package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1")
public class DataElementChoice {
	@XmlElement(name = "TextField")
	private String textField;

	@XmlElement(name = "DateTimeField")
	private String dateTimeField;

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public String getDateTimeField() {
		return dateTimeField;
	}

	public void setDateTimeField(String dateTimeField) {
		this.dateTimeField = dateTimeField;
	}

	@Override
	public String toString() {
		return "DataElementChoice{" +
				"textField='" + textField + '\'' +
				", dateTimeField='" + dateTimeField + '\'' +
				'}';
	}
}
