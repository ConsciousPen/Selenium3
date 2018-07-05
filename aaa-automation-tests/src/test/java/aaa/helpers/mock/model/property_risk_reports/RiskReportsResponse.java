package aaa.helpers.mock.model.property_risk_reports;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "RISKREPORTS_RESPONSE", hasEmptyRows = true)
public class RiskReportsResponse {
	@ExcelColumnElement(name = "ID")
	private String id;

	private String state;
	private String zipCode;

	@ExcelColumnElement(name = "addressLine 1")
	private String addressLine1;

	@ExcelColumnElement(name = "addressLine 2")
	private String addressLine2;

	private String cityName;
	private String geocode;
	private Integer elevation;
	private String distancetoshorerange;
	private String faultcode;

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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getGeocode() {
		return geocode;
	}

	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}

	public Integer getElevation() {
		return elevation;
	}

	public void setElevation(Integer elevation) {
		this.elevation = elevation;
	}

	public String getDistancetoshorerange() {
		return distancetoshorerange;
	}

	public void setDistancetoshorerange(String distancetoshorerange) {
		this.distancetoshorerange = distancetoshorerange;
	}

	public String getFaultcode() {
		return faultcode;
	}

	public void setFaultcode(String faultcode) {
		this.faultcode = faultcode;
	}

	@Override
	public String toString() {
		return "RiskReportsResponse{" +
				"id='" + id + '\'' +
				", state='" + state + '\'' +
				", zipCode='" + zipCode + '\'' +
				", addressLine1='" + addressLine1 + '\'' +
				", addressLine2='" + addressLine2 + '\'' +
				", cityName='" + cityName + '\'' +
				", geocode='" + geocode + '\'' +
				", elevation=" + elevation +
				", distancetoshorerange='" + distancetoshorerange + '\'' +
				", faultcode='" + faultcode + '\'' +
				'}';
	}
}
