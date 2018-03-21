package aaa.helpers.openl.model.auto_ca.choice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class AutoCaChoiceOpenLPolicy extends OpenLPolicy {
	@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLDriver> drivers;

	@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLVehicle> vehicles;

	private Boolean multiCar;
	private String nanoPolicyType;
	private Integer term;

	public List<AutoCaChoiceOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaChoiceOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public List<AutoCaChoiceOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaChoiceOpenLVehicle> vehicles) {
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

	@Override
	public Integer getTerm() {
		return term;
	}

	@Override
	public LocalDateTime getEffectiveDate() {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException(String.format("Getting Effective Date for %s is not implemented", this.getClass().getSimpleName()));
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
