package aaa.helpers.mock.model.address;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "ADDRESS_REFERENCE")
public class AddressReference {
	@ExcelColumnElement(name = "PostalCode")
	private String postalCode;

	@ExcelColumnElement(name = "City")
	private String city;

	@ExcelColumnElement(name = "County")
	private String county;

	@ExcelColumnElement(name = "State")
	private String state;

	@ExcelColumnElement(name = "Country")
	private String country;

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "AddressReference{" +
				"postalCode='" + postalCode + '\'' +
				", city='" + city + '\'' +
				", county='" + county + '\'' +
				", state='" + state + '\'' +
				", country='" + country + '\'' +
				'}';
	}
}
