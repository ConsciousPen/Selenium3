package aaa.utils.openl.model;

public class OpenLAddress {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private int number;

	private String state;
	private String zip;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
