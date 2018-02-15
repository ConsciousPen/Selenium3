package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLDriver {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;
	protected String id;
	protected String gender;
	protected String maritalStatus;
	protected Integer tyde;
	protected Integer dsr;
	protected Boolean goodStudent;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Integer getTyde() {
		return tyde;
	}

	public void setTyde(Integer tyde) {
		this.tyde = tyde;
	}

	public Integer getDsr() {
		return dsr;
	}

	public void setDsr(Integer dsr) {
		this.dsr = dsr;
	}

	public void setGoodStudent(Boolean goodStudent) {
		this.goodStudent = goodStudent;
	}

	public Boolean isGoodStudent() {
		return goodStudent;
	}

	@Override
	public String toString() {
		return "OpenLDriver{" +
				"number=" + number +
				", id='" + id + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatus='" + maritalStatus + '\'' +
				", tyde=" + tyde +
				", dsr=" + dsr +
				", goodStudent=" + goodStudent +
				'}';
	}
}
