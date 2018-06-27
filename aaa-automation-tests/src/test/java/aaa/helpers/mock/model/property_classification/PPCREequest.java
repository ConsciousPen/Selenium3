package aaa.helpers.mock.model.property_classification;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "PPC_REQUEST")
public class PPCREequest {
	@ExcelColumnElement(name = "ID")
	private String id;

	private String protectionCodeType;
	private String cityName;
	private String zipCode;
	private String streetAddressLine;

	@ExcelColumnElement(name = "state")
	private String state;

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
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "PPCREequest{" +
				"id='" + id + '\'' +
				", protectionCodeType='" + protectionCodeType + '\'' +
				", cityName='" + cityName + '\'' +
				", zipCode='" + zipCode + '\'' +
				", streetAddressLine='" + streetAddressLine + '\'' +
				", state='" + state + '\'' +
				'}';
	}
}
