package aaa.helpers.openl.model.auto_ca.select;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class AutoCaSelectOpenLFile extends OpenLFile<AutoCaSelectOpenLPolicy> {
	protected List<AutoCaSelectOpenLPolicy> policies;

	@ExcelTransient
	private List<AutoCaSelectOpenLVehicle> vehicles;

	@ExcelTransient
	private List<OpenLAddress> address;

	@ExcelTransient
	private List<AutoCaSelectOpenLDriver> drivers;

	@ExcelTransient
	private List<AutoCaSelectOpenLCoverage> coverages;

	public List<AutoCaSelectOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaSelectOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public List<OpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<AutoCaSelectOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaSelectOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<AutoCaSelectOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoCaSelectOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<AutoCaSelectOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<AutoCaSelectOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLFile{" +
				"policies=" + policies +
				", vehicles=" + vehicles +
				", address=" + address +
				", drivers=" + drivers +
				", coverages=" + coverages +
				", tests=" + tests +
				'}';
	}
}
