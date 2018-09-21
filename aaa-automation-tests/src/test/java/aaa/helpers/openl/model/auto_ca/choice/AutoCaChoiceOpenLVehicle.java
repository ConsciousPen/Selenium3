package aaa.helpers.openl.model.auto_ca.choice;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
public class AutoCaChoiceOpenLVehicle extends OpenLVehicle {
	private List<AutoOpenLCoverage> coverages;

	private Boolean antiLock;
	private Boolean antiTheft;
	private String vehType;
	private String vehicleUsageCd;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelColumnElement(name = "umbiLiabilitySymbol")
	private String umLiabilitySymbol;

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
	public List<AutoOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@SuppressWarnings("RedundantMethodOverride")
	@Override
	public void setMpLiabilitySymbol(String mpLiabilitySymbol) {
		this.mpLiabilitySymbol = mpLiabilitySymbol;
	}

	@Override
	public String getUmLiabilitySymbol() {
		return umLiabilitySymbol;
	}

	public Boolean hasAntiLock() {
		return antiLock;
	}

	public Boolean hasAntiTheft() {
		return antiTheft;
	}
}
