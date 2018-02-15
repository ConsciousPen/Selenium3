package aaa.helpers.openl.model.auto_ca.choice;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class AutoCaChoiceOpenLFile extends OpenLFile<AutoCaChoiceOpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	protected List<AutoCaChoiceOpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = VEHICLE_SHEET_NAME, headerRowNumber = VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLVehicle> vehicles;

	@ExcelTransient
	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	private List<OpenLAddress> address;

	@ExcelTransient
	@ExcelTableElement(sheetName = DRIVER_SHEET_NAME, headerRowNumber = DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLDriver> drivers;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<AutoOpenLCoverage> coverages;

	public List<AutoCaChoiceOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaChoiceOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public List<OpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<AutoCaChoiceOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaChoiceOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<AutoOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<AutoCaChoiceOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<AutoCaChoiceOpenLPolicy> policies) {
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
