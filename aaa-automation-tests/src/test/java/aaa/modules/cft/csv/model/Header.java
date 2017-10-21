package aaa.modules.cft.csv.model;

public class Header {

	private String code;
	private String date;
	private String notKnownAttribute;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNotKnownAttribute() {
		return notKnownAttribute;
	}

	public void setNotKnownAttribute(String notKnownAttribute) {
		this.notKnownAttribute = notKnownAttribute;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Header header = (Header) o;

		if (code != null ? !code.equals(header.code) : header.code != null) return false;
		if (date != null ? !date.equals(header.date) : header.date != null) return false;
		return notKnownAttribute != null ? notKnownAttribute.equals(header.notKnownAttribute) : header.notKnownAttribute == null;
	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (notKnownAttribute != null ? notKnownAttribute.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Header{" +
				"code='" + code + '\'' +
				", date='" + date + '\'' +
				", notKnownAttribute='" + notKnownAttribute + '\'' +
				'}';
	}
}
