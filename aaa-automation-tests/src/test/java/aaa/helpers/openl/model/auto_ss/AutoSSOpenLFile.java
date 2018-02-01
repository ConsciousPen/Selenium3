package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class AutoSSOpenLFile extends OpenLFile<AutoSSOpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME + "AZ", headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	protected List<AutoSSOpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = CAPPINGDETAILS_SHEET_NAME, headerRowNumber = CAPPINGDETAILS_HEADER_ROW_NUMBER)
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTransient
	@ExcelTableElement(sheetName = VEHICLE_SHEET_NAME + "AZ", headerRowNumber = VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoSSOpenLVehicle> vehicles;

	@ExcelTransient
	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	private List<OpenLAddress> address;

	@ExcelTransient
	@ExcelTableElement(sheetName = DRIVER_SHEET_NAME + "AZ", headerRowNumber = DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> drivers;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME + "AZ", headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<AutoSSOpenLCoverage> coverages;

	public List<OpenLCappingDetails> getCappingDetails() {
		return new ArrayList<>(cappingDetails);
	}

	public void setCappingDetails(List<OpenLCappingDetails> cappingDetails) {
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

	public List<OpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<OpenLDriver> drivers) {
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
