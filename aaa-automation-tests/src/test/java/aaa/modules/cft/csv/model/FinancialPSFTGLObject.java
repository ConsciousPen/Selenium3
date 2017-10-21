package aaa.modules.cft.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FinancialPSFTGLObject {

	private Header header;
	private List<Record> records;
	private Footer footer;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public List<Record> getRecords() {
		return Objects.isNull(this.records) ? this.records = new ArrayList<>() : records;
	}

	public Footer getFooter() {
		return footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FinancialPSFTGLObject object = (FinancialPSFTGLObject) o;

		if (header != null ? !header.equals(object.header) : object.header != null) return false;
		if (records != null ? !records.equals(object.records) : object.records != null) return false;
		return footer != null ? footer.equals(object.footer) : object.footer == null;
	}

	@Override
	public int hashCode() {
		int result = header != null ? header.hashCode() : 0;
		result = 31 * result + (records != null ? records.hashCode() : 0);
		result = 31 * result + (footer != null ? footer.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "FinancialPSFTGLObject{" +
				"header=" + header +
				", records=" + records +
				", footer=" + footer +
				'}';
	}
}
