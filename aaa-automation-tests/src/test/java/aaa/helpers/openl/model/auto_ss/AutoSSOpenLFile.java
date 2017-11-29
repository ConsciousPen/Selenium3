package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoSSOpenLFile extends OpenLFile<AutoSSOpenLPolicy> {
	@ExcelTableElement(sheetName = "Batch- PolicyAZ")
	private List<AutoSSOpenLPolicy> policies;

	@ExcelTableElement(sheetName = "Batch- CappingDetails")
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTableElement(sheetName = "Batch- VehicleAZ")
	private List<OpenLVehicle> vehicles;

	@ExcelTableElement(sheetName = "Batch- Address")
	private List<OpenLAddress> address;

	@ExcelTableElement(sheetName = "Batch- DriverAZ")
	private List<OpenLDriver> drivers;

	@ExcelTableElement(sheetName = "Batch- CoverageAZ")
	private List<OpenLCoverage> coverages;

	public List<OpenLCappingDetails> getCappingDetails() {
		return cappingDetails;
	}

	public void setCappingDetails(List<OpenLCappingDetails> cappingDetails) {
		this.cappingDetails = new ArrayList<>(cappingDetails);
	}

	public List<OpenLVehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<OpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public List<OpenLAddress> getAddress() {
		return address;
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<OpenLDriver> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<OpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<OpenLCoverage> getCoverages() {
		return coverages;
	}

	public void setCoverages(List<OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<AutoSSOpenLPolicy> getPolicies() {
		return policies;
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
