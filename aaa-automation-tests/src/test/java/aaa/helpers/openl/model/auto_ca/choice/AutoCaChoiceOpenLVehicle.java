package aaa.helpers.openl.model.auto_ca.choice;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoCaChoiceOpenLVehicle extends OpenLVehicle {
	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowNumber = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLCoverage> coverages;

	private Boolean antiLock;
	private Boolean antiTheft;
	private String vehType;
	private String vehicleUsageCd;

	public String getVehType() {
		return vehType;
	}

	public void setVehType(String vehType) {
		this.vehType = vehType;
	}

	public String getVehicleUsageCd() {
		return vehicleUsageCd;
	}

	public void setVehicleUsageCd(String vehicleUsageCd) {
		this.vehicleUsageCd = vehicleUsageCd;
	}

	public List<AutoCaChoiceOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoCaChoiceOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public Boolean getAntiLock() {
		return antiLock;
	}

	public void setAntiLock(Boolean antiLock) {
		this.antiLock = antiLock;
	}

	public Boolean getAntiTheft() {
		return antiTheft;
	}

	public void setAntiTheft(Boolean antiTheft) {
		this.antiTheft = antiTheft;
	}

	@Override
	public String toString() {
		return "AutoCaChoiceOpenLVehicle{" +
				"coverages=" + coverages +
				", antiLock=" + antiLock +
				", antiTheft=" + antiTheft +
				", vehType='" + vehType + '\'' +
				", vehicleUsageCd='" + vehicleUsageCd + '\'' +
				", number=" + number +
				", annualMileage=" + annualMileage +
				", collSymbol=" + collSymbol +
				", compSymbol=" + compSymbol +
				", id='" + id + '\'' +
				", modelYear=" + modelYear +
				", statCode='" + statCode + '\'' +
				", address=" + address +
				'}';
	}

	public Boolean hasAntiLock() {
		return antiLock;
	}

	public Boolean hasAntiTheft() {
		return antiTheft;
	}
}
