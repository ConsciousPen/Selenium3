package aaa.helpers.openl.model.auto_ca.choice;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
public class AutoCaChoiceOpenLVehicle extends OpenLVehicle {
	private List<AutoOpenLCoverage> coverages;

	private Boolean antiLock;
	private Boolean antiTheft;
	private String vehType;
	private String vehicleUsageCd;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTableColumnElement(name = "umbiLiabilitySymbol")
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

	@Override
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

	@SuppressWarnings("RedundantMethodOverride")
	@Override
	public void setMpLiabilitySymbol(String mpLiabilitySymbol) {
		this.mpLiabilitySymbol = mpLiabilitySymbol;
	}

	@Override
	public String getUmLiabilitySymbol() {
		return umLiabilitySymbol;
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
