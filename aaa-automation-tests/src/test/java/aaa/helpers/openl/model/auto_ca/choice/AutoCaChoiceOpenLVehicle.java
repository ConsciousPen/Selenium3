package aaa.helpers.openl.model.auto_ca.choice;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class AutoCaChoiceOpenLVehicle extends OpenLVehicle {
	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<AutoOpenLCoverage> coverages;

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

	public List<AutoOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoOpenLCoverage> coverages) {
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
				", oldStatCode='" + oldStatCode + '\'' +
				", biLiabilitySymbol='" + biLiabilitySymbol + '\'' +
				", pdLiabilitySymbol='" + pdLiabilitySymbol + '\'' +
				", mpLiabilitySymbol='" + mpLiabilitySymbol + '\'' +
				", umLiabilitySymbol='" + umLiabilitySymbol + '\'' +
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
