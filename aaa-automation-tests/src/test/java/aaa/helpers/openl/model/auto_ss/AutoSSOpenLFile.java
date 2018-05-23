package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class AutoSSOpenLFile extends OpenLFile<AutoSSOpenLPolicy> {
	private List<AutoSSOpenLPolicy> policies;

	@ExcelTransient
	private List<AutoSSOpenLCappingDetails> cappingDetails;
	@ExcelTransient
	private List<AutoSSOpenLVehicle> vehicles;
	@ExcelTransient
	private List<OpenLAddress> address;
	@ExcelTransient
	private List<AutoSSOpenLDriver> drivers;
	@ExcelTransient
	private List<AutoSSOpenLCoverage> coverages;

	public List<AutoSSOpenLCappingDetails> getCappingDetails() {
		return new ArrayList<>(cappingDetails);
	}

	public void setCappingDetails(List<AutoSSOpenLCappingDetails> cappingDetails) {
		this.cappingDetails = new ArrayList<>(cappingDetails);
	}

	public List<AutoSSOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoSSOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public List<OpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<AutoSSOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoSSOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<AutoSSOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoSSOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<AutoSSOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<AutoSSOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "AutoSSOpenLFile{" +
				"policies=" + policies +
				", cappingDetails=" + cappingDetails +
				", vehicles=" + vehicles +
				", address=" + address +
				", drivers=" + drivers +
				", coverages=" + coverages +
				", tests=" + tests +
				'}';
	}
}
