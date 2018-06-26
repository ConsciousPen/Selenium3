package aaa.helpers.mock.model.property_classification;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public class PPCREequest {
	@ExcelColumnElement(name = "ID")
	private String id;

	private String protectionCodeType;
	private String cityName;
	private String zipCode;
	private String streetAddressLine;

	@ExcelColumnElement(name = "State")
	private String State;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProtectionCodeType() {
		return protectionCodeType;
	}

	public void setProtectionCodeType(String protectionCodeType) {
		this.protectionCodeType = protectionCodeType;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getStreetAddressLine() {
		return streetAddressLine;
	}

	public void setStreetAddressLine(String streetAddressLine) {
		this.streetAddressLine = streetAddressLine;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}
}
