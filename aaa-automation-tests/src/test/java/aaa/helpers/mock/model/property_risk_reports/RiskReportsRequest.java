package aaa.helpers.mock.model.property_risk_reports;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "RISKREPORTS_REQUEST", hasEmptyRows = true)
public class RiskReportsRequest {
	@ExcelColumnElement(name = "ID")
	private String id;

	private String state;
	private String cityName;
	private String zipCode;
	private String streetAddressLine;
	private String streetAddressLine2;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getStreetAddressLine2() {
		return streetAddressLine2;
	}

	public void setStreetAddressLine2(String streetAddressLine2) {
		this.streetAddressLine2 = streetAddressLine2;
	}

	@Override
	public String toString() {
		return "RiskReportsRequest{" +
				"id='" + id + '\'' +
				", state='" + state + '\'' +
				", cityName='" + cityName + '\'' +
				", zipCode='" + zipCode + '\'' +
				", streetAddressLine='" + streetAddressLine + '\'' +
				", streetAddressLine2='" + streetAddressLine2 + '\'' +
				'}';
	}
}
