package aaa.helpers.openl.model.auto_ca.choice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class AutoCaChoiceOpenLPolicy extends OpenLPolicy {
	@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLDriver> drivers;

	@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaChoiceOpenLVehicle> vehicles;

	@ExcelTransient
	private LocalDateTime effectiveDate;

	private Boolean multiCar;
	private String nanoPolicyType;
	private Integer term;
	private Integer monsOfPriorIns; // unknown type, it's always empty in excel

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

	public String getNanoPolicyType() {
		return nanoPolicyType;
	}

	public void setNanoPolicyType(String nanoPolicyType) {
		this.nanoPolicyType = nanoPolicyType;
	}

	public void setMultiCar(Boolean multiCar) {
		this.multiCar = multiCar;
	}

	@Override
	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Integer getMonsOfPriorIns() {
		return monsOfPriorIns;
	}

	public void setMonsOfPriorIns(Integer monsOfPriorIns) {
		this.monsOfPriorIns = monsOfPriorIns;
	}

	@Override
	public LocalDateTime getEffectiveDate() {
		if (effectiveDate == null) {
			return TimeSetterUtil.getInstance().getCurrentTime();
		}
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public String toString() {
		return "AutoCaChoiceOpenLPolicy{" +
				"drivers=" + drivers +
				", vehicles=" + vehicles +
				", effectiveDate=" + effectiveDate +
				", multiCar=" + multiCar +
				", nanoPolicyType='" + nanoPolicyType + '\'' +
				", term=" + term +
				", monsOfPriorIns=" + monsOfPriorIns +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}

	public Boolean isMultiCar() {
		return multiCar;
	}
}
