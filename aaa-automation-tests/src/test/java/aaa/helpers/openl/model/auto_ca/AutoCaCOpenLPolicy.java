package aaa.helpers.openl.model.auto_ca;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoCaCOpenLPolicy extends OpenLPolicy {
	@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowNumber = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaCOpenLDriver> drivers;

	@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowNumber = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaCOpenLVehicle> vehicles;

	private Boolean multiCar;
	private String nanoPolicyType;
	private Integer term;

	public List<AutoCaCOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaCOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<AutoCaCOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaCOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public Boolean getMultiCar() {
		return multiCar;
	}

	public void setMultiCar(Boolean multiCar) {
		this.multiCar = multiCar;
	}

	public String getNanoPolicyType() {
		return nanoPolicyType;
	}

	public void setNanoPolicyType(String nanoPolicyType) {
		this.nanoPolicyType = nanoPolicyType;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	@Override
	public String toString() {
		return "AutoCaCOpenLPolicy{" +
				"drivers=" + drivers +
				", vehicles=" + vehicles +
				", multiCar=" + multiCar +
				", nanoPolicyType='" + nanoPolicyType + '\'' +
				", term=" + term +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
