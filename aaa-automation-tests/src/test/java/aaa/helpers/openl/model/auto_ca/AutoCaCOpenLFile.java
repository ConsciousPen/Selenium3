package aaa.helpers.openl.model.auto_ca;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class AutoCaCOpenLFile extends OpenLFile<AutoCaCOpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	protected List<AutoCaCOpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = VEHICLE_SHEET_NAME + "AZ", headerRowNumber = VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaCOpenLVehicle> vehicles;

	@ExcelTransient
	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	private List<OpenLAddress> address;

	@ExcelTransient
	@ExcelTableElement(sheetName = DRIVER_SHEET_NAME + "AZ", headerRowNumber = DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> drivers;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME + "AZ", headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<OpenLCoverage> coverages;

	public List<AutoCaCOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaCOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public List<OpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<OpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<OpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<OpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<AutoCaCOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<AutoCaCOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "AutoCaCOpenLFile{" +
				"policies=" + policies +
				", vehicles=" + vehicles +
				", address=" + address +
				", drivers=" + drivers +
				", coverages=" + coverages +
				", tests=" + tests +
				'}';
	}
}
