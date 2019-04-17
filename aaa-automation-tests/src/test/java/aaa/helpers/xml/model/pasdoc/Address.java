package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

	@XmlElement(name = "Address1")
	private String address1;

	@XmlElement(name = "Address2")
	private String address2;

	@XmlElement(name = "AddressValidated")
	private Boolean addressValidated;

	@XmlElement(name = "CityName")
	private String cityName;

	@XmlElement(name = "Country")
	private String country;

	@XmlElement(name = "County")
	private String county;

	@XmlElement(name = "StateName")
	private String stateName;

	@XmlElement(name = "ZipCode")
	private String zipCode;

	public String getAddress1() {
		return address1;
	}

	public Address setAddress1(String address1) {
		this.address1 = address1;
		return this;
	}

	public String getAddress2() {
		return address2;
	}

	public Address setAddress2(String address2) {
		this.address2 = address2;
		return this;
	}

	public Boolean getAddressValidated() {
		return addressValidated;
	}

	public Address setAddressValidated(Boolean addressValidated) {
		this.addressValidated = addressValidated;
		return this;
	}

	public String getCityName() {
		return cityName;
	}

	public Address setCityName(String cityName) {
		this.cityName = cityName;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Address setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getCounty() {
		return county;
	}

	public Address setCounty(String county) {
		this.county = county;
		return this;
	}

	public String getStateName() {
		return stateName;
	}

	public Address setStateName(String stateName) {
		this.stateName = stateName;
		return this;
	}

	public String getZipCode() {
		return zipCode;
	}

	public Address setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Address)) {
			return false;
		}
		Address address = (Address) o;
		return Objects.equals(address1, address.address1) &&
				Objects.equals(address2, address.address2) &&
				Objects.equals(addressValidated, address.addressValidated) &&
				Objects.equals(cityName, address.cityName) &&
				Objects.equals(country, address.country) &&
				Objects.equals(county, address.county) &&
				Objects.equals(stateName, address.stateName) &&
				Objects.equals(zipCode, address.zipCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address1, address2, addressValidated, cityName, country, county, stateName, zipCode);
	}

	@Override
	public String toString() {
		return "Address{" +
				"address1='" + address1 + '\'' +
				", address2='" + address2 + '\'' +
				", addressValidated=" + addressValidated +
				", cityName='" + cityName + '\'' +
				", country='" + country + '\'' +
				", county='" + county + '\'' +
				", stateName='" + stateName + '\'' +
				", zipCode='" + zipCode + '\'' +
				'}';
	}
}
