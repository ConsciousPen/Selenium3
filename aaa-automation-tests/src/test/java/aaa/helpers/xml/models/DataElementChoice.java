package aaa.helpers.xml.models;

import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.AAAN_URI)
public class DataElementChoice {
	@XmlElement(name = "TextField")
	private String textField;

	@XmlElement(name = "DateTimeField")
	private String dateTimeField;

	public String getTextField() {
		return textField;
	}



	public String getDateTimeField() {
		return dateTimeField;
	}

	public DataElementChoice setTextField(String textField) {
		this.textField = textField;
		return this;
	}

	public DataElementChoice setDateTimeField(String dateTimeField) {
		this.dateTimeField = dateTimeField;
		return this;
	}

	@Override
	public String toString() {
		return "DataElementChoice{" +
				"textField='" + textField + '\'' +
				", dateTimeField='" + dateTimeField + '\'' +
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

		DataElementChoice that = (DataElementChoice) o;

		if (textField != null ? !textField.equals(that.textField) : that.textField != null) {
			return false;
		}
		return dateTimeField != null ? dateTimeField.equals(that.dateTimeField) : that.dateTimeField == null;
	}

	@Override
	public int hashCode() {
		int result = textField != null ? textField.hashCode() : 0;
		result = 31 * result + (dateTimeField != null ? dateTimeField.hashCode() : 0);
		return result;
	}
}
